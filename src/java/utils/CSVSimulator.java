package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.ReadingDAO;
import model.ReadingDTO;
import model.SensorDAO;
import model.SensorDTO;

public class CSVSimulator {

    private String csvPath;
    private SensorDAO sensorDAO = new SensorDAO();
    private ReadingDAO readingDAO = new ReadingDAO();
    private service.AlertService alertService = new service.AlertService();
    private ExecutorService executor;

    public CSVSimulator(String csvPath) {
        this.csvPath = csvPath;
    }

    public void startSimulation() {
        List<SensorDTO> sensors = sensorDAO.getAllSensorsWithMap();

        if (sensors == null || sensors.isEmpty()) {
            System.out.println(">>> Number of sensors found: 0. Check the Sensor table and SensorPollutantMap again!");
            return;
        }

        System.out.println(">>> Found" + sensors.size() + " sensor. Start creating a thread...");
        this.executor = Executors.newCachedThreadPool();
        for (SensorDTO sensor : sensors) {
            executor.execute(new SensorWorker(sensor));
        }
    }

    public void stopSimulation() {
        System.out.println(">>> [SYSTEM] Đang yêu cầu các luồng dừng lại...");
        if (executor != null) {
            try {
                // 1. Gửi tín hiệu dừng ngay lập tức cho tất cả Worker
                executor.shutdownNow();

                // 2. Chờ đợi tối đa 3 giây để các Worker dọn dẹp và thoát
                if (!executor.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.out.println(">>> [WARN] Một số luồng chưa kịp dừng, cưỡng chế đóng!");
                }

                System.out.println(">>> [SYSTEM] Đã ngắt kết nối và dừng Simulator.");
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addSingleSensorSimulation(SensorDTO newSensor) {
        if (executor != null && !executor.isShutdown()) {
            System.out.println(">>> [SYSTEM] Kích hoạt luồng bổ sung cho Sensor: " + newSensor.getSensorID());
            executor.execute(new SensorWorker(newSensor));
        }
    }

    private class SensorWorker implements Runnable {

        private SensorDTO sensor;

        public SensorWorker(SensorDTO sensor) {
            this.sensor = sensor;
        }

        @Override
        public void run() {
            // Định dạng khớp với file: 3/10/2004 18:00:00 (M/d/yyyy H:mm:ss)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");

            try ( BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
                String line;
                br.readLine(); // Bỏ qua Header

                while ((line = br.readLine()) != null) {

                    String[] data = line.split(",");

                    // 2. Kiểm tra dòng trống hoặc thiếu dữ liệu (file có rất nhiều dòng rác)
                    if (data.length < 5 || data[0].trim().isEmpty()) {
                        continue;
                    }

                    try {
                        // 3. Gộp Ngày và Giờ
                        String fullTs = data[0].trim() + " " + data[1].trim();
                        LocalDateTime ts = LocalDateTime.parse(fullTs, formatter);

                        // 4. Lấy index từ bảng Map
                        int colIndex = sensor.getCsvColumnIndex();
                        if (colIndex >= data.length) {
                            continue;
                        }

                        String rawValue = data[colIndex].trim();
                        if (rawValue.isEmpty()) {
                            continue;
                        }

                        double value = Double.parseDouble(rawValue);

                        // 5. Kiểm tra giá trị lỗi
                        if (value <= -200) {
                            // Cập nhật Sensor thành Inactive (0)
                            String sqlUpdate = "UPDATE Sensor SET status = 0, last_seen_ts = GETDATE() WHERE sensor_id = ?";
                            try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                                ps.setInt(1, sensor.getSensorID());
                                ps.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(">>> Sensor " + sensor.getSensorID() + " has stopped working!");
                        } else {
                            // --- PHẦN THÊM MỚI: TỰ ĐỘNG KÍCH HOẠT LẠI ---
                            // Cập nhật status thành Active (1) và làm mới last_seen_ts
                            // Chúng ta thêm "AND status = 0" để chỉ update khi thực sự cần chuyển trạng thái
                            String sqlRestore = "UPDATE Sensor SET status = 1, last_seen_ts = GETDATE() WHERE sensor_id = ? AND status = 0";
                            try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sqlRestore)) {
                                ps.setInt(1, sensor.getSensorID());
                                int rows = ps.executeUpdate();
                                if (rows > 0) {
                                    System.out.println(">>> [RECOVERY] Sensor " + sensor.getSensorID() + " is back ONLINE!");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // --------------------------------------------

                            // Lưu vào DB nếu dữ liệu ổn (Giữ nguyên code cũ)
                            ReadingDTO reading = new ReadingDTO(0, sensor.getSensorID(), 1, ts, value, "NORMAL", LocalDateTime.now());
                            boolean success = readingDAO.insertReading(reading);
                            if (success) {
                                System.out.println("Sensor " + sensor.getSensorID() + " [OK]: " + value + " lúc " + fullTs);
                                alertService.processReading(reading, sensor.getRoomID());
                            }
                        }

                        // Nghỉ mô phỏng
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Giữ lại trạng thái ngắt
                        break; // Thoát vòng lặp ngay khi bị shutdown
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi luồng Sensor " + sensor.getSensorID() + ": " + e.getMessage());
            }
        }
    }
}

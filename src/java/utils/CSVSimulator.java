package utils;

import java.io.BufferedReader;
import java.io.FileReader;
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
        if (sensors == null || sensors.isEmpty()) return;

        this.executor = Executors.newCachedThreadPool();
        for (SensorDTO sensor : sensors) {
            executor.execute(new SensorWorker(sensor));
        }
    }

    public void stopSimulation() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public void addSingleSensorSimulation(SensorDTO newSensor) {
        if (executor != null && !executor.isShutdown()) {
            System.out.println(">>> [CORE] Kích hoạt mô phỏng tức thì cho Sensor mới: " + newSensor.getSensorID());
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");

            try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
                String line;
                br.readLine(); // Bỏ qua Header

                while ((line = br.readLine()) != null) {
                    
                    // 1. Kiểm tra trạng thái Active/Inactive ngay trong vòng lặp
                    if (!sensorDAO.isSensorActive(sensor.getSensorID())) {
                        System.out.println(">>> [STOP] Luồng Sensor " + sensor.getSensorID() + " đã dừng.");
                        break; 
                    }

                    String[] data = line.split(",");
                    if (data.length < 5 || data[0].trim().isEmpty()) continue;

                    try {
                        String fullTs = data[0].trim() + " " + data[1].trim();
                        LocalDateTime ts = LocalDateTime.parse(fullTs, formatter);

                        int colIndex = sensor.getCsvColumnIndex();
                        if (colIndex >= data.length) continue;

                        String rawValue = data[colIndex].trim();
                        if (rawValue.isEmpty()) continue;

                        double value = Double.parseDouble(rawValue);

                        // 2. Xử lý logic Pollutant ID linh hoạt
                        // Giả sử pollutant_id tương ứng với csv_column_index (Hoặc bạn có thể thêm field vào DTO)
                        // Nếu bạn thêm Sensor đo CO (Index 2), ID chất đo sẽ là 2
                        int actualPollutantID = colIndex; 

                        if (value <= -200) {
                            sensorDAO.updateSensorStatus(sensor.getSensorID(), false);
                            break; 
                        } else {
                            // 3. Ghi dữ liệu vào DB
                            ReadingDTO reading = new ReadingDTO(0, sensor.getSensorID(), actualPollutantID, ts, value, "NORMAL", LocalDateTime.now());
                            boolean success = readingDAO.insertReading(reading);
                            if (success) {
                                // Gửi qua AlertService để kiểm tra ngưỡng ngay lập tức
                                alertService.processReading(reading, sensor.getRoomID());
                            }
                        }

                        // Nghỉ 3 giây để mô phỏng thời gian thực
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        // Bỏ qua dòng lỗi, tiếp tục dòng sau
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi Simulator Sensor " + sensor.getSensorID() + ": " + e.getMessage());
            }
        }
    }
}

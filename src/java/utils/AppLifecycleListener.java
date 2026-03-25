package utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private CSVSimulator simulator;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> [SYSTEM] Ứng dụng đang khởi động. Đang kích hoạt Simulation...");
// 1. Lấy đường dẫn đến thư mục 'build/web' (nơi ứng dụng đang chạy)
        String webPath = sce.getServletContext().getRealPath("/");
        java.io.File webFolder = new java.io.File(webPath);

        // 2. Leo lên 2 cấp: web -> build -> AQSystem-Final123 (Thư mục gốc)
        // .getParentFile() lần 1 là lên 'build', lần 2 là lên thư mục Project
        java.io.File projectRoot = webFolder.getParentFile().getParentFile();

        // 3. Kết hợp với tên file CSV của bạn
        java.io.File csvFile = new java.io.File(projectRoot, "AirQualityUCI.csv");
        String fullPath = csvFile.getAbsolutePath();

        System.out.println(">>> [DEBUG] Đang tìm file tại Project Root: " + fullPath);

        // Kiểm tra file có tồn tại không trước khi chạy
        File file = new File(fullPath);
        if (file.exists()) {
            // 2. Khởi tạo và bắt đầu simulation
            simulator = new CSVSimulator(fullPath);
            simulator.startSimulation();
            System.out.println(">>> [SYSTEM] Simulation đã bắt đầu chạy ngầm.");
            sce.getServletContext().setAttribute("GLOBAL_SIMULATOR", simulator);
        } else {
            System.err.println(">>> [ERROR] Không tìm thấy file CSV tại: " + fullPath);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> [SYSTEM] Context đang bị hủy...");
        CSVSimulator sim = (CSVSimulator) sce.getServletContext().getAttribute("GLOBAL_SIMULATOR");
        if (sim != null) {
            sim.stopSimulation();
        }
        // Sau khi dừng sim, bạn có thể thêm một chút delay nhỏ để Driver kịp nhả kết nối
        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }
}

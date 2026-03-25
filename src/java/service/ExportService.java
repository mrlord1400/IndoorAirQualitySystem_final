package service;

import model.ReadingDTO;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExportService {

    // Đây là hàm chính để xuất file, nhận vào danh sách và tên file muốn lưu
    public void exportToCSV(List<ReadingDTO> list, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // 1. Ghi dòng tiêu đề (Header)
            writer.append("ReadingID,SensorID,PollutantID,Timestamp,Value,QualityFlag,CreatedAt\n");

            // 2. Duyệt qua từng Object trong List và ghi vào file
            for (ReadingDTO r : list) {
                writer.append(String.valueOf(r.getReadingID())).append(",")
                      .append(String.valueOf(r.getSensorID())).append(",")
                      .append(String.valueOf(r.getPollutantID())).append(",")
                      .append(r.getTs().toString()).append(",")
                      .append(String.valueOf(r.getValue())).append(",")
                      .append(r.getQualityFlag()).append(",")
                      .append(r.getCreatedAt().toString()).append("\n");
            }
            
            System.out.println("Đã xuất file thành công tại: " + filePath);
            
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }
}
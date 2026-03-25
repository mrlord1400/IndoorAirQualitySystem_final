package service;

import java.util.List;
import model.*;

public class AlertService {
    private ReadingDAO readingDAO = new ReadingDAO();
    private AlertDAO alertDAO = new AlertDAO();
    private RuleDAO ruleDAO = new RuleDAO(); 

    public void processReading(ReadingDTO currentReading, int roomID) {
        // 1. Lấy danh sách quy luật của phòng và chất tương ứng
        List<RuleDTO> rules = ruleDAO.getRulesByRoomAndPollutant(roomID, currentReading.getPollutantID());

        for (RuleDTO rule : rules) {
            // 2. Kiểm tra vi phạm tức thời
            if (isViolating(currentReading.getValue(), rule.getUpperBound(), rule.getLowerBound())) {
                
                // 3. Kiểm tra quá khứ (Look-back)
                // Vì file CSV 1 tiếng/dòng, số dòng cần check = duration_min / 60
                int requiredReadings = Math.max(1, rule.getDurationMin() / 60);
                List<ReadingDTO> recentReadings = readingDAO.getRecentReadings(roomID, currentReading.getPollutantID(), requiredReadings);
                
                int violationCount = countViolationsInPast(recentReadings, rule.getUpperBound(), rule.getLowerBound());

                // 4. Nếu số lần vi phạm liên tục đủ thời gian yêu cầu
                if (violationCount >= requiredReadings) {
                    // 5. Kiểm tra khử trùng lặp (De-duplication)
                    if (!alertDAO.hasActiveAlert(currentReading.getSensorID(), roomID, rule.getRuleID())) {
                        AlertDTO newAlert = new AlertDTO();
                        newAlert.setRoomID(roomID);
                        newAlert.setSensorID(currentReading.getSensorID());
                        newAlert.setRuleID(rule.getRuleID());
                        newAlert.setPollutantID(currentReading.getPollutantID());
                        newAlert.setStartTs(currentReading.getTs());
                        newAlert.setSeverity(rule.getSeverity());
                        newAlert.setMessage("Warning: " + rule.getSeverity() + " threshold exceeded. Value: " + currentReading.getValue());
                        
                        alertDAO.createAlert(newAlert);
                        // Thông báo giả lập cho Lab Manager
                        System.out.println("[SYSTEM-ALERT] New alert triggered for Room ID: " + roomID);
                    }
                }
            }
        }
    }

    public boolean isViolating(double value, Double upper, Double lower) {
        if (upper != null && value > upper) return true;
        if (lower != null && value < lower) return true;
        return false;
    }

    public int countViolationsInPast(List<ReadingDTO> readingList, Double upper, Double lower) {
        int count = 0;
        for (ReadingDTO r : readingList) {
            if (isViolating(r.getValue(), upper, lower)) count++;
        }
        return count;
    }
}
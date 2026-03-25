package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import utils.DBUtils;

public class AlertActionDAO {
    public boolean insertAlertAction(AlertActionDTO dto) {
        String sql = "INSERT INTO AlertAction (alertID, actorUserID, actionType, note, actionTs) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, dto.getAlertID());
            ps.setInt(2, dto.getActorUserID());
            ps.setString(3, dto.getActionType());
            ps.setString(4, dto.getNote());
            ps.setTimestamp(5, Timestamp.valueOf(dto.getActionTs()));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

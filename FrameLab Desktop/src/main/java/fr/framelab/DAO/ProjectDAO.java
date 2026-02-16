package fr.framelab.DAO;

import fr.framelab.DataBaseManager;
import fr.framelab.model.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;


public class ProjectDAO {



    public void createProject (Project projet) throws SQLException {
        String sql = "INSERT INTO projects (name,image) VALUES (?,?)";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS
        )) {
            pstmt.setString(1,projet.getName());
            pstmt.setString(2,projet.getImage());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()){
                if (keys.next()){
                    projet.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Save failed", e);
        }
    }

    public Optional<Project> findById (int id) {
        String sql = "SELECT id,name,image FROM projects WHERE id = ?";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql);) {
                pstmt.setInt(1,id);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()){
                    rs.getInt("id");
                    rs.getString("name");
                    rs.getString("image");
                }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Find faled", e);
        }
    }

    public void updateproject (Project project) {
        String sql = "UPDATE projects SET name = ?, image = ? WHERE id = ?";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql)) {
            pstmt.setString(1,project.getName());
            pstmt.setString(2,project.getImage());
            pstmt.setInt(3,project.getId());

            int rows = pstmt.executeUpdate();
            if(rows == 0) {
                throw new IllegalStateException("Project not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    public boolean deleteproject (int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";

        try(PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql)) {
            pstmt.setInt(1,id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }
}

package fr.framelab.DAO;

import fr.framelab.DataBaseManager;
import fr.framelab.model.Project;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProjectDAO {



    public void createProject (Project project) throws SQLException {
        String sql = "INSERT INTO projects (name,challengeId) VALUES (?,?)";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS
        )) {
            pstmt.setString(1,project.getName());
            pstmt.setInt(2,project.getChallengeId());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()){
                if (keys.next()){
                    project.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Save failed", e);
        }
    }

    public Optional<Project> findById (int id) {
        String sql = "SELECT id,name,challengeId FROM projects WHERE id = ?";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql);) {
                pstmt.setInt(1,id);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()){
                    Project project = new Project(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("challengeId")
                    );
                    return Optional.of(project);
                }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Find faled", e);
        }
    }

    public static Optional<Project> findByName(String name) {
        String sql = "SELECT id,name,challengeId FROM projects WHERE name = ?";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql);) {
            pstmt.setString(1,name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                Project project = new Project(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("challengeId")
                );
                return Optional.of(project);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Find faled", e);
        }
    }

    public void updateproject (Project project) {
        String sql = "UPDATE projects SET name = ?, challengeId = ? WHERE id = ?";

        try (PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql)) {
            pstmt.setString(1,project.getName());
            pstmt.setInt(2,project.getChallengeId());
            pstmt.setInt(3,project.getId());

            int rows = pstmt.executeUpdate();
            if(rows == 0) {
                throw new IllegalStateException("Project not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    public static boolean deleteProject(int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";

        try(PreparedStatement pstmt =DataBaseManager.getConnexion().prepareStatement(sql)) {
            pstmt.setInt(1,id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    public static List<Project> findAll() {
        String sql = "SELECT id,name,challengeId FROM projects";
        List<Project> projects = new ArrayList<>();

        try (PreparedStatement pstmt = DataBaseManager.getConnexion().prepareStatement(sql);) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int challengeId = rs.getInt("challengeId");
                Project project = new Project(id,name,challengeId);
                projects.add(project);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find projects failes" + e);
        }
        return projects;
        }
    }

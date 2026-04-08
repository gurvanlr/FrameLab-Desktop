package fr.framelab.controller;

import fr.framelab.AppSession;
import fr.framelab.DAO.ProjectDAO;
import fr.framelab.Main;
import fr.framelab.dto.ChallengeResponse;
import fr.framelab.model.Project;
import fr.framelab.service.ChallengeService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.setAll;

public class HomeController {

    @FXML private Label title;
    @FXML private Label description;
    @FXML private ImageView imageChallenge;
    @FXML private ListView<Project> projects;
    private int idChallenge;
    private String selectedProject;

    private final ChallengeService challengeService = new ChallengeService();

    @FXML
    public void initialize() {

        if (AppSession.isDemoMode()){
            setupDemoChallenge();
            loadDemoProjects();

        }else {
            this.setupChallenge();
            this.loadProjects();
        }


        projects.setOnMouseClicked(mouseEvent -> {
            String selected = projects.getSelectionModel().getSelectedItem().getName();
            if (selected != null) {
                selectedProject = selected;
            }
        });
    }

    @FXML
    private void setupChallenge() {

        Task<ChallengeResponse[]> task = new Task<>() {
            @Override
            protected ChallengeResponse[] call() throws Exception {
                try {
                return challengeService.getChallenge();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw   new RuntimeException();
                }
            }
        };

        task.setOnSucceeded(event -> {
            ChallengeResponse[] data = task.getValue();
            if (data != null && data.length > 0) {
                ChallengeResponse currentChallenge = data[0];
                if (currentChallenge != null) {
                    title.setText(currentChallenge.getTitle());
                    description.setText(currentChallenge.getDescription());
                    idChallenge = currentChallenge.getId();

                    String url = currentChallenge.getFullUrl();
                    Image img = new Image(url);
                    imageChallenge.setImage(img);
                    BufferedImage swingImage = SwingFXUtils.fromFXImage(img, null);

                    try {
                        ImageIO.write(swingImage,"png",new File("Challenges/chall#"+currentChallenge.getId()+".png"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        task.setOnFailed(event -> {
            System.out.println("fail");
        });

        new Thread(task).start();
    }

    public void loadProjects() {
        ProjectDAO dao = new ProjectDAO();
        List<Project> allProjects = dao.findAll();

        ObservableList<Project> observableProject = FXCollections.observableArrayList(allProjects);
        projects.setItems(observableProject);
    }

    public void newProject () throws IOException, SQLException {


        JOptionPane jop = new JOptionPane();
        String nom = JOptionPane.showInputDialog(jop, "merci de rentrer un nom");

        if (nom != null) {
            if (AppSession.isDemoMode()) {
                Project project = new Project(nom,-1);

                ProjectDAO dao = new ProjectDAO();
                dao.createProject(project);

                File folder = new File("Projects/" + nom);
                folder.mkdir();

                File file = new File("Challenges/demo_challenge.jpg");
                Image img = new Image(file.toURI().toString());
                BufferedImage swingImage = SwingFXUtils.fromFXImage(img, null);
                ImageIO.write(swingImage,"png",new File("Projects/"+ nom +"/base.png" ));



                EditorController controlller = (EditorController) Main.goTo("/fr/framelab/editor-view.fxml");

                controlller.initData(project);
                return;

            }
            Project project = new Project(nom, idChallenge);

            ProjectDAO dao = new ProjectDAO();
            dao.createProject(project);

            File folder = new File("Projects/" + nom);
            folder.mkdir();

            File file = new File("Challenges/chall#"+project.getChallengeId()+".png");
            Image img = new Image(file.toURI().toString());
            BufferedImage swingImage = SwingFXUtils.fromFXImage(img, null);
            ImageIO.write(swingImage,"png",new File("Projects/" + nom   + "/base.png" ));



           EditorController controlller = (EditorController) Main.goTo("/fr/framelab/editor-view.fxml");

           controlller.initData(project);
        }
    }

    public void resumeProject () throws IOException {
        Project project = ProjectDAO.findByName(selectedProject)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        EditorController controlller = (EditorController) Main.goTo("/fr/framelab/editor-view.fxml");
        controlller.initData(project);


    }

    public void deleteProject () throws SQLException {

        if (AppSession.isDemoMode()) {
            JOptionPane.showMessageDialog(null, "Action désactivée en mode démo.");
            return;
        }

        if (selectedProject == null) {
            System.out.println("pas d'élément séléctionner");
            return;
        }
        Project project = ProjectDAO.findByName(selectedProject)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        String path = ("Projects/" + project.getId());
        File dir = new File(path);

        if (!dir.delete()) {
             File[] files = dir.listFiles();
             if (files != null) {
                 for (File file : files) {
                     if (file.delete()){
                         System.out.println("le fichiers a bien été supprimer");
                     } else {
                         System.out.println("le fichier n'as pas été supprimer ");
                     }
                 }
             }
             dir.delete();
        }
        ProjectDAO.deleteProject(project.getId());
        loadProjects();
    }

    private void setupDemoChallenge() {
        title.setText("Défi Démo");
        description.setText("Vous êtes en mode démo");
        idChallenge = -1;

        String path = ("Challenges/demo_challenge.jpg");
        File file = new File(path);
        String img_path = file.toURI().toString();
        Image img = new Image(img_path);
        imageChallenge.setImage(img);
    }

    public void loadDemoProjects() {
        List<Project> demoProjects = List.of(
                new Project("Projet Démo 1", -1),
                new Project("Projet Démo 2", -1)
        );
        ObservableList<Project> observable = FXCollections.observableArrayList(demoProjects);
        projects.setItems(observable);
    }
}

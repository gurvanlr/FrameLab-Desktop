package fr.framelab.controller;

import fr.framelab.Main;
import fr.framelab.dto.ChallengeResponse;
import fr.framelab.service.ChallengeService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HomeController {

    @FXML private Label title;
    @FXML private Label description;
    @FXML private ImageView imageChallenge;

    private final ChallengeService challengeService = new ChallengeService();

    @FXML
    public void initialize() {
        this.setupChallenge();
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
                    String url = currentChallenge.getFullUrl();
                    System.out.println(url);
                    Image img = new Image(url,200,200,true,true);
                    imageChallenge.setImage(img);
                    BufferedImage swingImage = SwingFXUtils.fromFXImage(img, null);
                    try {
                        ImageIO.write(swingImage,"png",new File("Challenges/"+currentChallenge.getTitle()+".png"));
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

    public void newProject () throws IOException {
        Main.goTo("/fr/framelab/project.fxml");
    }
}

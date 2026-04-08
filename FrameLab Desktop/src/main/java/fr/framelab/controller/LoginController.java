package fr.framelab.controller;

import fr.framelab.AppSession;
import fr.framelab.Main;
import fr.framelab.dto.TokenResponse;
import fr.framelab.service.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML private TextField mailInput;
    @FXML private PasswordField passwordInput;
    @FXML private Label feedback;

    private final AuthService authService = new AuthService();

    @FXML
    public void login() {
        String mail = mailInput.getText();
        String password = passwordInput.getText();


        Task<TokenResponse> task = new Task<>() {
            @Override
            protected TokenResponse call() throws Exception{
                    return authService.login(mail, password);
            }
        };

        task.setOnSucceeded(event -> {
            TokenResponse token = task.getValue();
            if (token != null) {
                try {
                    Main.goTo("/fr/framelab/home-screen.fxml");
                } catch (IOException e) {}
            } else {
                feedback.setText("Identifiants invalides.");
            }
        }) ;

        task.setOnFailed(event -> {
            feedback.setText("Erreur : impossible de contacter le serveur.");
        });

        new Thread(task).start();
    }

    public void demo () throws IOException {
        AppSession.setDemoMode(true);
        Main.goTo("/fr/framelab/home-screen.fxml");
    }
}

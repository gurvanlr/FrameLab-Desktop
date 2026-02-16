package fr.framelab;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    static Stage primaryStage;

    @Override
    public void start (Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/framelab/login-view.fxml"));
        Parent root = loader.load();


        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("test");
        stage.show();
        primaryStage = stage;
    }

    public static Object goTo(String path) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource(path));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("test");
        primaryStage.show();
        return loader.getController();
    }



    public static void main (String[] args) {
        launch(args);
    }

}

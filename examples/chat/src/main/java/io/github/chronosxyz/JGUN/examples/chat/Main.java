package io.github.chronosxyz.JGUN.examples.chat;

import io.github.chronosx88.JGUN.api.Gun;
import io.github.chronosx88.JGUN.network.NetworkNode;
import io.github.chronosx88.JGUN.storage.MemoryStorage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Parent root = fxmlLoader.load();

        FXMLController controller = fxmlLoader.getController();
        controller.setGunInstance(getGun());
        Scene scene = new Scene(root);
        stage.setTitle("Gun Chat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static Gun getGun() throws UnknownHostException, URISyntaxException, InterruptedException {
        var storage = new MemoryStorage();
        return new Gun(storage, new NetworkNode(InetAddress.getLocalHost(), 5054, storage));
    }
}
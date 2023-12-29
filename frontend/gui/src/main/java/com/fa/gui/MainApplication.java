package com.fa.gui;

import com.fa.gui.controllers.BaseController;
import com.fa.logic.StateManager;
import com.fa.logic.TokenRefresher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Главный класс программы
 */
public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // загрузка текущего состояния приложения
        StateManager.getInstance().loadState();
        FXMLLoader loader = new FXMLLoader();
        if (StateManager.getInstance().getLastFXML() != null) {
            loader.setLocation(MainApplication.class.getResource(StateManager.getInstance().getLastFXML()));
        } else {
            loader.setLocation(MainApplication.class.getResource("/view/signin.fxml"));
        }
        Parent root = loader.load();
        BaseController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Менеджер облигаций");
        // Установка иконки для Stage
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/assets/logo-bondapp.png"))));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // запуск обновление токена
        TokenRefresher tokenRefresher = new TokenRefresher();
        tokenRefresher.startTokenRefreshTask(new TokenRefresher.TokenRefreshTask(), 30);
        // Остановка обновления токена
        primaryStage.setOnCloseRequest(event -> {
            // Останавливаем обновление токена при закрытии окна
            tokenRefresher.stop();
        });
//        Runtime.getRuntime().addShutdownHook(new Thread(tokenRefresher::stop));

    }

    public static void setScene(BaseController controller, String fxml) {

        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(fxml));
        try {
            controller.stage.setScene(new Scene(loader.load()));
            BaseController currentController = loader.getController();
            currentController.setStage(controller.stage);
            StateManager.getInstance().setCurrentStage(fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
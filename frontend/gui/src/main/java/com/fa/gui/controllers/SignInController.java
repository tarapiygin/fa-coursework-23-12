package com.fa.gui.controllers;


import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.gui.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Класс для отображения окна Входа
 */
public class SignInController extends BaseController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authSingInButton;

    @FXML
    private Text errorText;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void showRegisterStage(MouseEvent event) {
        MainApplication.setScene(this, "/view/signup.fxml");
    }

    @FXML
    void initialize() {
        authSingInButton.setOnAction(actionEvent -> {
            try {
                ApiClient.signIn(loginField.getText(), passwordField.getText());
                MainApplication.setScene(this, "/view/mainPanel.fxml");
            } catch (RequestError e) {
                errorText.setText(e.getMessage());
                errorText.setWrappingWidth(280);
                errorText.setFill(Color.web("#f26565"));
                errorText.setVisible(true);
            }

        });
    }

}

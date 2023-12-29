package com.fa.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.UserChangePasswordDTO;
import com.fa.dto.UserGetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

/**
 * Класс для отображения Профиля пользователя
 */
public class UserProfileController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text errorText;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void onClickSubmit(ActionEvent event) {
        UserChangePasswordDTO dto = new UserChangePasswordDTO();
        dto.setUsername(usernameField.textProperty().getValue());
        dto.setPassword(passwordField.textProperty().getValue());
        dto.setNewPassword(newPasswordField.textProperty().getValue());
        errorText.setText("Пароль изменен успешно!");
        try {
            ApiClient.passwordChange(dto);
        } catch (RequestError e) {
            errorText.setText(e.getMessage());
        }

    }

    @FXML
    void initialize() {
        try {
            UserGetDTO user = ApiClient.getUser();
            usernameField.textProperty().setValue(user.getUsername());
        } catch (RequestError e) {
            errorText.setText(e.getMessage());
        }

    }

}

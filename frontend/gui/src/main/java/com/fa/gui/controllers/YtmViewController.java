package com.fa.gui.controllers;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.YTMItemDTO;
import com.fa.dto.YTMListDTO;
import com.fa.gui.callbacks.OnEventCallback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import lombok.Data;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Класс для отображения формы подсчета расчета текущей доходности портфеля к погашению
 */
@Data
public class YtmViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<YTMItemDTO> tableYTM;

    @FXML
    private TableColumn<?, ?> ticketColumn;

    @FXML
    private TableColumn<?, ?> weightInPortfolioColumn;

    @FXML
    private TableColumn<?, ?> ytmColumn;

    @FXML
    private Text ytmResult;

    @FXML
    private Text textTitle;

    private OnEventCallback onEventCallback;

    private String calculateMethod;

    public void setCalculateMethod(String calculateMethod) {
        this.calculateMethod = calculateMethod;
        updateWindow();
    }

    public void updateWindow(){
        try {
            YTMListDTO ytmListDTO;
            if(Objects.equals(calculateMethod, "NEWTON")) {
                ytmListDTO = ApiClient.getPortfolioYTMNewton();
                textTitle.setText("Текущая доходность портфеля к погашению. Метод Ньютона-Рафсона.");

            }
            else if(Objects.equals(calculateMethod, "BASIC")) {
                ytmListDTO = ApiClient.getPortfolioYTMBasic();
                textTitle.setText("Текущая доходность портфеля к погашению. Базовый метод.");
            }
            else throw new RuntimeException("Недопустимый метод расчета YTM");
            ytmResult.textProperty().setValue("Доходность портфеля: " + ytmListDTO.getYtmPortfolio() + "%");

            ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
            weightInPortfolioColumn.setCellValueFactory(new PropertyValueFactory<>("weightInPortfolio"));
            ytmColumn.setCellValueFactory(new PropertyValueFactory<>("ytm"));

            ObservableList<YTMItemDTO> ytmItemDTOS = FXCollections.observableArrayList();
            ytmItemDTOS.addAll(ytmListDTO.getYtmList());
            tableYTM.setItems(ytmItemDTOS);

        } catch (RequestError e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {


    }
    @FXML
    void onCloseForm(MouseEvent event) {
        onEventCallback.onCallback(false, "");
    }

}

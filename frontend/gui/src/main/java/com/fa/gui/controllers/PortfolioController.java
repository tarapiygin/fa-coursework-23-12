package com.fa.gui.controllers;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.UserPortfolioItemDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Класс для отображения портфеля пользователя
 */
public class PortfolioController {


    @FXML
    private ResourceBundle resources;

    @FXML
    private BorderPane borderPane;

    @FXML
    private URL location;

    @FXML
    private TableColumn<?, ?> averageCostColumn;

    @FXML
    private TableView<UserPortfolioItemDTO> bondsTable;

    @FXML
    private TableColumn<?, ?> couponRateColumn;

    @FXML
    private TableColumn<?, ?> maturityDateColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> parValueColumn;

    @FXML
    private TableColumn<?, ?> quantityColumn;

    @FXML
    private TableColumn<?, ?> ticketColumn;

    @FXML
    private TableColumn<?, ?> paymentsPerYearColumn;

    private void onCloseWindowYTM(Boolean status, String message) {
        borderPane.setLeft(null);
    }

    @FXML
    void onCalculateBasicYTM(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ytmView.fxml"));
            Parent parent = loader.load();
            YtmViewController formController = loader.getController();
            formController.setOnEventCallback(this::onCloseWindowYTM);
            formController.setCalculateMethod("BASIC");
            borderPane.setLeft(parent);

        } catch (IOException ex) {
            //TODO
            throw new RuntimeException(ex);
        }
    }


    @FXML
    void onCalculateNewtonYTM(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ytmView.fxml"));
            Parent parent = loader.load();
            YtmViewController formController = loader.getController();
            formController.setOnEventCallback(this::onCloseWindowYTM);
            formController.setCalculateMethod("NEWTON");
            borderPane.setLeft(parent);

        } catch (IOException ex) {
            //TODO
            throw new RuntimeException(ex);
        }
    }

    @FXML
    void initialize() {
        try {
            List<UserPortfolioItemDTO> portfolioList = ApiClient.getPortfolio();

            ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            averageCostColumn.setCellValueFactory(new PropertyValueFactory<>("averageCost"));
            couponRateColumn.setCellValueFactory(new PropertyValueFactory<>("couponRate"));
            maturityDateColumn.setCellValueFactory(new PropertyValueFactory<>("maturityDate"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            parValueColumn.setCellValueFactory(new PropertyValueFactory<>("parValue"));
            paymentsPerYearColumn.setCellValueFactory(new PropertyValueFactory<>("paymentsPerYear"));

            ObservableList<UserPortfolioItemDTO> portfolioItems = FXCollections.observableArrayList();
            portfolioItems.addAll(portfolioList);
            bondsTable.setItems(portfolioItems);
        } catch (RequestError e) {
            //TODO
            throw new RuntimeException(e);

        }
    }

}

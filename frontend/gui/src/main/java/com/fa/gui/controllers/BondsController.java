package com.fa.gui.controllers;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.BondItemDTO;
import com.fa.dto.BondSearchParams;
import com.fa.gui.utils.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Класс представления для окна облигаций
 */
public class BondsController {
    @FXML
    private BorderPane borderPane;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<?, ?> averageCostColumn;

    @FXML
    private TableView<BondItemDTO> bondsTable;

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
    private Text notification;

    @FXML
    private Button resetSearchButton;


    @FXML
    private TextField searchCouponRate;

    @FXML
    private DatePicker searchMaturityDate;

    private Notification notificationController;

    private void onCreatePurchase(Boolean status, String message) {
        borderPane.setLeft(null);
        notificationController.showNotification(message, 5, status);
    }


    @FXML
    void showAddPurchaseForm(BondItemDTO bond) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/createPurchaseForm.fxml"));
            Parent parent = loader.load();
            CreatePurchaseFormController formController = loader.getController();
            formController.setCurrentBond(bond);
            formController.setPurchaseCallback(this::onCreatePurchase);
            borderPane.setLeft(parent);

        } catch (IOException ex) {
            //TODO
            throw new RuntimeException(ex);
        }
    }

    @FXML
    void onSubmitSearchForm(ActionEvent event) {
        resetSearchButton.setVisible(true);
        BondSearchParams params = new BondSearchParams();

        // Обработка поля ввода процентной ставки
        if (!searchCouponRate.getText().isEmpty()) {
            try {
                params.setCouponRateMin(Double.valueOf(searchCouponRate.getText()));
            } catch (NumberFormatException e) {
                notificationController.showNotification("Неправильные данные в поле купонной ставки", 15, false);
                return;
            }
        }
        // Обработка поля ввода даты погашения
        if (searchMaturityDate.getValue() != null) {
            // Преобразование LocalDate в строку в формате yyyy-MM-dd
            String formattedDate = searchMaturityDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            params.setMaturityDateFrom(formattedDate);
        }

        // Отправка запроса и обработка результатов
        try {
            List<BondItemDTO> bonds = ApiClient.getBonds(params);
            // Обновление таблицы или отображение результатов
            updateBondTable(bonds);
        } catch (RequestError e) {
            notificationController.showNotification(e.getMessage(), 15, false);
        }
    }

    @FXML
    void onResetSearchForm(ActionEvent event){

        try {
            List<BondItemDTO> bonds = ApiClient.getBonds(null);
            // Обновление таблицы или отображение результатов
            updateBondTable(bonds);
            // Очистка поля купонной ставки
            searchCouponRate.textProperty().setValue("");
            // Очистка поля даты погашения (если используется DatePicker)
            if (searchMaturityDate != null) {
                searchMaturityDate.setValue(null);
            }
            // Скрытие кнопки сброса
            resetSearchButton.setVisible(false);
        } catch (RequestError e) {
            notificationController.showNotification(e.getMessage(), 15, false);
        }
    }

    void updateBondTable(List<BondItemDTO> bonds) {
            ObservableList<BondItemDTO> bondItems = FXCollections.observableArrayList();
            bondItems.addAll(bonds);
            bondsTable.setItems(bondItems);
    }

    @FXML
    void initialize() {
        notificationController = new Notification(notification);

        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("marketPrice"));
        couponRateColumn.setCellValueFactory(new PropertyValueFactory<>("couponRate"));
        maturityDateColumn.setCellValueFactory(new PropertyValueFactory<>("maturityDate"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        parValueColumn.setCellValueFactory(new PropertyValueFactory<>("parValue"));

        try {
            List<BondItemDTO> bonds = ApiClient.getBonds(null);
            updateBondTable(bonds);
        } catch (RequestError e) {
            //TODO
            throw new RuntimeException(e);
        }

        bondsTable.setOnMouseClicked(event -> {
            if (!bondsTable.getSelectionModel().isEmpty()) {
                BondItemDTO selectedItem = bondsTable.getSelectionModel().getSelectedItem();
                showAddPurchaseForm(selectedItem);
            }
        });

    }

}

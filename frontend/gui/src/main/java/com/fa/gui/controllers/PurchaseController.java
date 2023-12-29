package com.fa.gui.controllers;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.PurchaseItemDTO;
import com.fa.gui.utils.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Класс для отображения сделок пользователя
 */
public class PurchaseController {
    @FXML
    private BorderPane borderPane;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<?, ?> bondTicketColumn;

    @FXML
    private TableColumn<?, ?> creationDateTimeColumn;


    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> priceColumn;


    @FXML
    private TableView<PurchaseItemDTO> purchasesTable;

    @FXML
    private TableColumn<?, ?> quantityColumn;
    @FXML
    private Text notification;

    private Notification notificationController;

    @FXML
    void showUpdateOrDeletePurchaseForm(PurchaseItemDTO purchase) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/updateOrDeletePurchaseForm.fxml"));
            Parent parent = loader.load();
            UpdateOrDeletePurchaseFormController formController = loader.getController();
            formController.setEditablePurchase(purchase);
            formController.setOnEventCallback(this::onUpdatePurchases);
            borderPane.setLeft(parent);

        } catch (IOException ex) {
            //TODO
            throw new RuntimeException(ex);
        }
    }

    private void onUpdatePurchases(Boolean status, String message) {
        if (status) updateTable();
        borderPane.setLeft(null);
        notificationController.showNotification(message, 5, status);
    }


    private void updateTable() {
        try {
            List<PurchaseItemDTO> purchases = ApiClient.getPurchases();
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            bondTicketColumn.setCellValueFactory(new PropertyValueFactory<>("bondTicket"));
            creationDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("creationDateTime"));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

            ObservableList<PurchaseItemDTO> purchaseItemDTOS = FXCollections.observableArrayList();
            purchaseItemDTOS.addAll(purchases);
            purchasesTable.setItems(purchaseItemDTOS);
        } catch (RequestError e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {
        notificationController = new Notification(notification);
        updateTable();
        purchasesTable.setOnMouseClicked(event -> {
            if (!purchasesTable.getSelectionModel().isEmpty()) {
                PurchaseItemDTO selectedItem = purchasesTable.getSelectionModel().getSelectedItem();
                showUpdateOrDeletePurchaseForm(selectedItem);
            }
        });
    }


}

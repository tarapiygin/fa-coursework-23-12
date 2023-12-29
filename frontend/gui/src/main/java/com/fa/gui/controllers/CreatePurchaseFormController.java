package com.fa.gui.controllers;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.BondItemDTO;
import com.fa.dto.PurchaseItemCreateDTO;
import com.fa.gui.callbacks.OnEventCallback;
import com.fa.gui.utils.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import lombok.Data;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Форма создания сделки по облигации
 */
@Data
public class CreatePurchaseFormController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private DatePicker creationDateTimeField;

    @FXML
    private TextField couponRateField;

    @FXML
    private TextField maturityDateField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField parValueField;

    @FXML
    private TextField paymentsPerYearField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField ticketField;

    private OnEventCallback onEventCallback;

    private BondItemDTO currentBond;

    @FXML
    Text notificationElement;

    private Notification notification;

    public void setCurrentBond(BondItemDTO currentBond) {
        this.currentBond = currentBond;
        updateFormFields();
    }

    public void setPurchaseCallback(OnEventCallback callback) {
        this.onEventCallback = callback;
    }

    @FXML
    void onCloseForm(MouseEvent event) {
        onEventCallback.onCallback(false, "");
    }

    @FXML
    void onCreatePurchase(ActionEvent event) {
        PurchaseItemCreateDTO purchase = new PurchaseItemCreateDTO();
        purchase.setBondTicket(ticketField.textProperty().getValue());

        try {
            purchase.setCreationDateTime(Date.from(creationDateTimeField.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant()));
        } catch (NullPointerException e) {
            notification.showNotification("Неправильные данные в поле Дата", 15, false);
            return;
        }


        // Валидация полей
        try {
            purchase.setPrice(Double.valueOf(priceField.textProperty().getValue()));
        } catch (NumberFormatException e) {
            notification.showNotification("Неправильные данные в поле Цена", 15, false);
            return;
        }

        try {
            purchase.setQuantity(Integer.valueOf(quantityField.textProperty().getValue()));
        } catch (NumberFormatException e) {
            notification.showNotification("Неправильные данные в поле Количество", 15, false);
            return;
        }


        try {
            ApiClient.createPurchase(purchase);
            onEventCallback.onCallback(true, "Сделка успешно добавлена!");
        } catch (RequestError e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {
        creationDateTimeField.setEditable(false);
        notification = new Notification(notificationElement);
    }

    private void updateFormFields() {
        if (currentBond != null) {
            // Предустанавливаем значения текущей облигации в форму
            ticketField.textProperty().setValue(currentBond.getTicket());
            nameField.textProperty().setValue(currentBond.getName());
            couponRateField.textProperty().setValue(currentBond.getCouponRate().toString());
            paymentsPerYearField.textProperty().setValue(String.valueOf(currentBond.getPaymentsPerYear()));
            parValueField.textProperty().setValue(currentBond.getParValue().toString());
            maturityDateField.textProperty().setValue(currentBond.getMaturityDate());
            priceField.textProperty().setValue(currentBond.getMarketPrice().toString());
        }
    }
}

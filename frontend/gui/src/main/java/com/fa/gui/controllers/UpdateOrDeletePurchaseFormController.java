package com.fa.gui.controllers;

import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.BondItemDTO;
import com.fa.dto.PurchaseItemDTO;
import com.fa.dto.PurchaseItemUpdateDTO;
import com.fa.gui.callbacks.OnEventCallback;
import com.fa.gui.utils.Notification;
import com.fa.gui.utils.SuggestionsMenu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import lombok.Data;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Класс для отображения формы обновления или удаления сделки по облигации
 */
@Data
public class UpdateOrDeletePurchaseFormController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private DatePicker creationDateTimeField;

    @FXML
    private Text idText;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField ticketField;

    @FXML
    Text notificationElement;

    private Notification notification;

    private SuggestionsMenu suggestionsMenu;

    private OnEventCallback onEventCallback;

    private PurchaseItemDTO editablePurchase;

    public void setEditablePurchase(PurchaseItemDTO editablePurchase) {
        this.editablePurchase = editablePurchase;
        updateFormFields();
    }

    @FXML
    void initialize() {
        creationDateTimeField.setEditable(false);
        ticketField.setEditable(false);

        notification = new Notification(notificationElement);
        // Инициализация suggestionsMenu
        try {
            List<BondItemDTO> bonds = ApiClient.getBonds(null);
            List<String> tickets = bonds.stream().map(BondItemDTO::getTicket).toList();
            SuggestionsMenu suggestionsMenu = new SuggestionsMenu(ticketField, tickets);
            suggestionsMenu.initSuggestions();
        } catch (RequestError e) {
            throw new RuntimeException(e);
        }

    }

    private void updateFormFields() {
        if (editablePurchase != null) {
            idText.setText(idText.getText() + editablePurchase.getId());
            priceField.setText(""+editablePurchase.getPrice());
            quantityField.setText(""+editablePurchase.getQuantity());
            ticketField.setText(editablePurchase.getBondTicket());
            creationDateTimeField.setValue(LocalDate.parse(editablePurchase.getCreationDateTime()));
        }
    }

    @FXML
    void onCloseForm(MouseEvent event) {
        onEventCallback.onCallback(false, "");
    }


    @FXML
    void onDeletePurchase(ActionEvent event) {
        try {
            ApiClient.deletePurchase(editablePurchase.id);
            onEventCallback.onCallback(true, String.format("Сделка с №%s, по облигации %s - удалена!", editablePurchase.id, editablePurchase.bondTicket));
        } catch (RequestError e) {
            notification.showNotification(e.getMessage(), 10, false);
        }
    }

    @FXML
    void onUpdatePurchase(ActionEvent event) {
        PurchaseItemUpdateDTO purchase =  new PurchaseItemUpdateDTO();
        purchase.setCreationDateTime(Date.from(creationDateTimeField.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant()));
        purchase.setId(editablePurchase.getId());
        purchase.setBondTicket(ticketField.textProperty().getValue());

        try {
            purchase.setPrice(Double.valueOf(priceField.textProperty().getValue()));

        } catch (NumberFormatException e){
            notification.showNotification("Неправильные данные в поле цена", 15, false);
            return;
        }

        try {
            purchase.setQuantity(Integer.valueOf(quantityField.textProperty().getValue()));

        } catch (NumberFormatException e){
            notification.showNotification("Неправильные данные в поле Количество", 15, false);
            return;
        }

        try {
            ApiClient.updateOrCreatePurchase(purchase);
            onEventCallback.onCallback(true, String.format("Сделка с №%s, сохранена!", editablePurchase.id));
        } catch (RequestError e) {
            notification.showNotification(e.getMessage(), 10, false);
        }
    }

}

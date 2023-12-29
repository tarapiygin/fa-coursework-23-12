package com.fa.gui.controllers;

import java.io.IOException;

import com.fa.gui.MainApplication;
import com.fa.logic.StateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;


/**
 * Главный класс программы
 * @author Vassilyev Denis (<a href="https://github.com/tarapiygin">GitHub</a>)
 */
public class MainPanelController extends BaseController {

    @FXML
    private Button purchaseTableView;

    @FXML
    private Button portfolioTableView;

    @FXML
    private Button bondsTableView;

    @FXML
    private Button authorView;

    @FXML
    private BorderPane borderPane;

    private Button activeButton;

    void setDisableButtonStyle() {
        if (activeButton != null) activeButton.setStyle(
                "-fx-border-width: 0 0 1 0;-fx-border-color: transparent transparent #172e3b transparent; -fx-border-style: dashed;"
        );

    }

    void setActiveButtonStyle() {
        if (activeButton != null) activeButton.setStyle("");
    }

    @FXML
    void loadPortfolioTableView(ActionEvent event) {
        setActiveButtonStyle();
        activeButton = portfolioTableView;
        setDisableButtonStyle();
        loadFXML("/view/portfolioTable");
    }

    @FXML
    void loadPurchasesTableView(ActionEvent event) {
        setActiveButtonStyle();
        activeButton = purchaseTableView;
        setDisableButtonStyle();
        loadFXML("/view/purchasesTable");
    }

    @FXML
    void loadBondsTableView(ActionEvent event) {
        setActiveButtonStyle();
        activeButton = bondsTableView;
        setDisableButtonStyle();
        loadFXML("/view/bondsTable");
    }

    @FXML
    void loadUserProfileView(MouseEvent event) {
        setActiveButtonStyle();
        activeButton = null;
        loadFXML("/view/userProfile");
    }

    @FXML
    void loadAuthorView(ActionEvent event) {
        setActiveButtonStyle();
        activeButton = authorView;
        setDisableButtonStyle();
        loadFXML("/view/author");
    }


    @FXML
    void onExitUser(ActionEvent event) {
        StateManager.getInstance().setToken(null);
        StateManager.getInstance().setCurrentStage("/view/signin.fxml");
        MainApplication.setScene(this, "/view/signin.fxml");
    }

    @FXML
    private void loadFXML(String fileName) {
        Parent parent;
        try {

            parent = FXMLLoader.load(getClass().getResource(fileName + ".fxml"));

            borderPane.setCenter(parent);

        } catch (IOException ex) {
            //TODO
            throw new RuntimeException(ex);

        }
    }

    @FXML
    void initialize() {
        activeButton = portfolioTableView;
        setDisableButtonStyle();
        loadFXML("/view/portfolioTable");
    }
}

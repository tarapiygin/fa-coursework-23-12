package com.fa.gui.controllers;

import javafx.stage.Stage;


/**
 * Базовый класс контроллера для реализации переключений между сценами с помощью метода setScene MainApplication
 */
public abstract class BaseController {
    public Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

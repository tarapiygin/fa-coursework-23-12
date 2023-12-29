module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires logic;
    requires java.net.http;
    requires static lombok;


    opens com.fa.gui to javafx.fxml;
    exports com.fa.gui;
    exports com.fa.gui.controllers;
    opens com.fa.gui.controllers to javafx.fxml;
}
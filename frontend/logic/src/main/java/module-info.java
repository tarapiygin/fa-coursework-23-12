module logic {
    requires okhttp3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires static lombok;
    requires javafx.controls;
    requires javafx.fxml;

    exports com.fa.logic;
    exports com.fa.dto;
    exports com.fa.api;
}
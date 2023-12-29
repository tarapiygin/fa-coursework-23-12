package com.fa.gui.utils;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Objects;

public class Notification {
    Text notification;

    public Notification(Text notification) {
        this.notification = notification;
    }

    public void showNotification(String message, int duration, boolean success) {
        notification.setVisible(false);
        if (!success) notification.setFill(Paint.valueOf("#e91e63"));
        if (success) notification.setFill(Paint.valueOf("#009688"));
        if (Objects.equals(message, "")) return;
        notification.textProperty().setValue(message);
        notification.setVisible(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(duration));
        delay.setOnFinished(event -> notification.setVisible(false));
        delay.play();
    }
}

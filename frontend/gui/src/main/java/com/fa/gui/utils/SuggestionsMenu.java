package com.fa.gui.utils;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionsMenu {
    TextField field;
    List<String> suggestions;


    public SuggestionsMenu(TextField field, List<String> suggestions) {
        this.field = field;
        this.suggestions = suggestions;
    }

    public void initSuggestions() {
        // Инициализация suggestionsMenu
        ContextMenu suggestionsMenu = new ContextMenu();
        field.sceneProperty().addListener((observableScene, oldScene, newScene) -> {

            if (newScene != null) {
                field.textProperty().addListener((observable, oldValue, newValue) -> {
                    // Очищаем предыдущие подсказки
                    suggestionsMenu.getItems().clear();

                    if (!newValue.isEmpty()) {
                        // Получаем подсказки для введенного текста
                        List<String> suggestionsList = getSuggestions(newValue);

                        // Создаем элементы меню для каждой подсказки и добавляем их в контекстное меню
                        for (String suggestion : suggestionsList) {
                            MenuItem item = new MenuItem(suggestion);
                            item.setOnAction(event -> handleSuggestionSelect(suggestion));
                            suggestionsMenu.getItems().add(item);
                        }

                        // Отображаем контекстное меню
                        if (!suggestionsMenu.isShowing() && suggestionsMenu.getScene() != null) {
                            suggestionsMenu.show(field, Side.BOTTOM, 0, 0);
                        }
                    } else {
                        suggestionsMenu.hide();
                    }
                });
            }

        });


    }

    private List<String> getSuggestions (String value) {
        return suggestions.stream()
                .filter(ticket -> ticket.toLowerCase().startsWith(value.toLowerCase()))
                .limit(5)
                .collect(Collectors.toList());
    }

    // Обработка выбора подсказки
    private void handleSuggestionSelect(String suggestion) {
        field.setText(suggestion);
        // Дополнительные действия при выборе подсказки, например, заполнение других полей формы
    }
}

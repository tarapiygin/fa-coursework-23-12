package com.fa.logic;

import com.fa.dto.TokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/**
 * Класс управляющий данными приложения, сохраняет текущее состояние авторизации и выбранное окно пользователя.
 */
@Data
public class StateManager {
    private static final ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);;

    private static final String APP_FOLDER = System.getProperty("user.home") + File.separator + "bondsApp";
    private static final String STATE_FILE = APP_FOLDER + File.separator + "state.json";
    private static StateManager instance;

    public String lastFXML;
    public TokenDTO token;



    public StateManager() {}

    public static StateManager getInstance() {
        if (instance == null) {
            instance = new StateManager();
        }
        return instance;
    }

    public void setCurrentStage(String lastFXML) {
        this.lastFXML = lastFXML;
        this.saveState();
    }

    public void setToken(TokenDTO token) {
        this.token = token;
        this.saveState();
    }

    public void saveState() {
        try {
            File appDir = new File(APP_FOLDER);
            if (!appDir.exists()) {
                appDir.mkdirs(); // Создаём директорию, если она не существует
            }
            FileWriter writer = new FileWriter(STATE_FILE);
            objectMapper.writeValue(writer, this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            try {
                File appDir = new File(APP_FOLDER);
                if (!appDir.exists()) {
                    appDir.mkdirs(); // Создаём директорию, если она не существует
                }
                file.createNewFile();
                this.token = null;
                this.lastFXML = null;
                saveState(); // Сохраняем начальное состояние в новый файл
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (FileReader reader = new FileReader(STATE_FILE)) {
            if (!file.exists() || file.length() == 0) return;
            StateManager loadedState = objectMapper.readValue(file, StateManager.class);

            if (loadedState != null) {
                if (loadedState.token != null) {

                    if (new Date().getTime() > (loadedState.token.getIssuedAt().getTime() + loadedState.token.getLifetime())){
                        this.token = null;
                        this.lastFXML = null;
                        this.saveState();
                        return;
                    }
                    this.token = loadedState.token;
                    this.lastFXML = loadedState.lastFXML;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

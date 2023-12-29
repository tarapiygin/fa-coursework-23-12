package com.fa.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для загрузки конфигурационного файла
 */
public class ConfigLoader {

    public static String getProperty(String key) {
        Properties properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            properties.load(input);
            return properties.getProperty(key);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

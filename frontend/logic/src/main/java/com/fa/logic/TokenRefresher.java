package com.fa.logic;
import com.fa.api.ApiClient;
import com.fa.api.RequestError;
import com.fa.dto.TokenDTO;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Класс предоставляющий методы для асинхронного обновления токена, запускающего отдельный процесс от основного приложения
 */
public class TokenRefresher {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startTokenRefreshTask(Runnable task, long refreshPeriod) {
        // Запускаем задачу с заданным периодом
        scheduler.scheduleAtFixedRate(task, 0, refreshPeriod, TimeUnit.MINUTES);
    }

    public void stop() {
        scheduler.shutdown(); // Плавно останавливаем планировщик
        try {
            // Ожидаем завершения текущих задач в течение определенного времени
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Если задачи не завершились, принудительно их останавливаем
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow(); // Принудительно останавливаем в случае прерывания
            Thread.currentThread().interrupt(); // Восстанавливаем прерванный статус
        }
    }

    /** Задача для обновления токена */
    public static class TokenRefreshTask implements Runnable {

        public TokenRefreshTask() {}

        @Override
        /**  Логика отправки запроса на сервер для обновления токена */
        public void run() {
            try {

                TokenDTO currentToken = StateManager.getInstance().getToken();
                if (currentToken.getToken() != null) {
                    TokenDTO token = ApiClient.refreshToken();
                    StateManager.getInstance().setToken(token);
                }

            } catch (RequestError e) {
                //TODO
                throw new RuntimeException(e);
            }
        }
    }
}

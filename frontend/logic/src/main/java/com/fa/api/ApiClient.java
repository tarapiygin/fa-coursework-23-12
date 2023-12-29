package com.fa.api;

import com.fa.dto.*;
import com.fa.logic.ConfigLoader;
import com.fa.logic.StateManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Статический класс для работы с API сервера
 */
public class ApiClient {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String baseUrl = ConfigLoader.getProperty("api.baseUrl");


    public static ResponseDTO createRequest(String dataJson, String url, String type, Boolean authorization) {

        RequestBody body = RequestBody.create(dataJson, MediaType.get("application/json; charset=utf-8"));

        Request.Builder requestBuilder = new Request.Builder();
        if (authorization && StateManager.getInstance().getToken() != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + StateManager.getInstance().getToken().getToken());
        }
        requestBuilder.url(baseUrl + url);
        // Определяем тип запроса к серверу
        if (Objects.equals(type, "GET")) requestBuilder.get();
        if (Objects.equals(type, "POST")) requestBuilder.post(body);
        if (Objects.equals(type, "PUT")) requestBuilder.put(body);
        if (Objects.equals(type, "DELETE")) requestBuilder.delete();


        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {


                if (response.code() == 400) {
                    return new ResponseDTO(
                            false,
                            response.code() + " Неправильные данные. Такой пользователь уже существует.",
                            ""
                    );
                }

                if (response.code() == 401) {
                    return new ResponseDTO(
                            false,
                            response.code() + " Ошибка аутентификации. Неправильный логин или пароль.",
                            ""
                    );
                }

                if (response.code() >= 500) {
                    return new ResponseDTO(
                            false,
                            response.code() + " Произошла ошибка на сервере. Попробуйте подключиться позже",
                            ""
                    );
                }

                if (response.code() < 200 || response.code() >= 300) {
                    return new ResponseDTO(
                            false,
                            response.code() + " Возникла непредвиденная ошибка",
                            ""
                    );
                }

                return new ResponseDTO(
                        false,
                        response.code() + "Нет соединения с сервером. Проверьте подключение к интернету",
                        ""
                );
            }


            if (response.body() != null) {
                String data = response.body().string();
                return new ResponseDTO(
                        true,
                        "",
                        data
                );
            }
            return new ResponseDTO(
                    true,
                    "",
                    ""
            );
        } catch (IOException e) {
            return new ResponseDTO(
                    false,
                    "Нет соединения с сервером. Проверьте подключение к интернету и попробуйте подключиться позже",
                    ""
            );
        }

    }

    public static void signIn(String username, String password) throws RequestError {
        AuthDTO authDTO = new AuthDTO(username, password);
        try {
            String json = objectMapper.writeValueAsString(authDTO);
            String url = "/auth/signin";
            ResponseDTO response = ApiClient.createRequest(json, url, "POST", false);
            if (!response.isOk) throw new RequestError(response.error);

            StateManager.getInstance().setToken(objectMapper.readValue(response.data, TokenDTO.class));
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static UserGetDTO getUser() throws RequestError {
        try {
            String url = "/secured/user";
            ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

            if (!response.isOk) throw new RequestError(response.error);
            return objectMapper.readValue(response.data, UserGetDTO.class);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static void passwordChange(UserChangePasswordDTO dto) throws RequestError {
        try {
            String json = objectMapper.writeValueAsString(dto);
            String url = "/auth/password/change";
            ResponseDTO response = ApiClient.createRequest(json, url, "POST", false);

            if (!response.isOk) throw new RequestError(response.error);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static void signUp(String username, String password) throws RequestError {
        AuthDTO authDTO = new AuthDTO(username, password);
        try {
            String json = objectMapper.writeValueAsString(authDTO);
            String url = "/auth/signup";
            ResponseDTO response = ApiClient.createRequest(json, url, "POST", false);
            if (!response.isOk) throw new RequestError(response.error);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }


    public static TokenDTO refreshToken() throws RequestError {
        String url = "/auth/token/refresh";
        ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

        if (!response.isOk) throw new RequestError(response.error);
        try {
            return objectMapper.readValue(response.data, TokenDTO.class);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static YTMListDTO getPortfolioYTMNewton() throws RequestError {
        String url = "/secured/user/portfolio/ytm/newton-raphson";
        ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

        if (!response.isOk) throw new RequestError(response.error);
        try {
            return objectMapper.readValue(response.data, YTMListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static YTMListDTO getPortfolioYTMBasic() throws RequestError {
        String url = "/secured/user/portfolio/ytm/basic";
        ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

        if (!response.isOk) throw new RequestError(response.error);
        try {
            return objectMapper.readValue(response.data, YTMListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static List<UserPortfolioItemDTO> getPortfolio() throws RequestError {
        String url = "/secured/user/portfolio";
        ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

        if (!response.isOk) throw new RequestError(response.error);


        try {
            return objectMapper.readValue(response.data, new TypeReference<List<UserPortfolioItemDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }


    public static List<PurchaseItemDTO> getPurchases() throws RequestError {
        String url = "/secured/user/purchases";
        ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

        if (!response.isOk) throw new RequestError(response.error);

        try {
            return objectMapper.readValue(response.data, new TypeReference<List<PurchaseItemDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static PurchaseItemDTO createPurchase(PurchaseItemCreateDTO dto) throws RequestError {
        String url = "/secured/user/purchases";
        try {
            String json = objectMapper.writeValueAsString(dto);
            ResponseDTO response = ApiClient.createRequest(json, url, "POST", true);

            if (!response.isOk) throw new RequestError(response.error);

            return objectMapper.readValue(response.data, PurchaseItemDTO.class);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static void updateOrCreatePurchase(PurchaseItemUpdateDTO dto) throws RequestError {
        String url = "/secured/user/purchases/" + dto.getId();
        try {
            String json = objectMapper.writeValueAsString(dto);
            ResponseDTO response = ApiClient.createRequest(json, url, "PUT", true);

            if (!response.isOk) throw new RequestError(response.error);
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }

    public static void deletePurchase(Long id) throws RequestError {
        String url = "/secured/user/purchases/" + id;
        ResponseDTO response = ApiClient.createRequest("", url, "DELETE", true);

        if (!response.isOk) throw new RequestError(response.error);
    }


    public static List<BondItemDTO> getBonds(BondSearchParams params) throws RequestError {
        StringBuilder urlBuilder = new StringBuilder("/secured/bonds");
        // Проверка, есть ли параметры для добавления в URL
        if (params != null) {
            List<String> queryParams = new ArrayList<>();

            if (params.getMaturityDateFrom() != null && !params.getMaturityDateFrom().isEmpty()) {
                queryParams.add("maturityDateFrom=" + params.getMaturityDateFrom());
            }

            if (params.getCouponRateMin() != null) {
                queryParams.add("couponRateMin=" + params.getCouponRateMin());
            }

            // Добавляем параметры в URL
            if (!queryParams.isEmpty()) {
                urlBuilder.append("?");
                urlBuilder.append(String.join("&", queryParams));
            }
        }
        String url = urlBuilder.toString();
        ResponseDTO response = ApiClient.createRequest("", url, "GET", true);

        if (!response.isOk) throw new RequestError(response.error);

        try {
            return objectMapper.readValue(response.data, new TypeReference<List<BondItemDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RequestError(e.getMessage());
        }
    }
}

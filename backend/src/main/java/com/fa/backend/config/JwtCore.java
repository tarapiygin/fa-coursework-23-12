package com.fa.backend.config;

import com.fa.backend.domain.User;
import com.fa.backend.dto.TokenGetDTO;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Класс для системы авторизации пользователя.
 * Реализует генерацию и валидацию токенов, а также предоставляет информацию о времени жизни выданного токена.
 */
@Component
public class JwtCore {
    @Value("${backend.app.secret}")
    private String secret;
    @Value("${backend.app.lifetime}")
    private int lifetime;

    /**
     * Метод создает зашифрованный токен.
     *
     * @param username Имя пользователя
     * @return JWT токен
     */
    private String buildToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + lifetime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * Метод генерирует токен для пользователя прошедшего аутентификацию.
     *
     * @param authentication класс аутентификации SpringSecurity
     * @return JWT токен
     */
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return buildToken(user.getUsername());
    }

    /**
     * Расшифровывает токен и возвращает username
     *
     * @param token JWT токен
     * @return username
     */
    public String getUserNameFromJwt(String token) {
        String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        return username;
    }

    /**
     * Проверяет токен на валидность
     *
     * @param token JWT токен
     * @return true | false
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверяет токен на валидность и генерирует новый токе
     *
     * @param token JWT токен
     * @return token JWT токен
     */
    public String refreshToken(String token) {
        if (!isTokenValid(token)) {
            throw new SecurityException("Invalid token");
        }
        String username = getUserNameFromJwt(token);
        return buildToken(username);
    }

    /**
     * Возвращает объект представления токена для API
     *
     * @param token JWT токен
     * @return объект представления токена
     */
    public TokenGetDTO getTokenDTOFromJwt(String token) {

        Date expiration = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
        Date issuedAt = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getIssuedAt();
        TokenGetDTO dto = new TokenGetDTO();
        dto.setLifetime(expiration.getTime() - new Date().getTime() - 900000);
        dto.setIssuedAt(issuedAt);
        dto.setToken(token);
        return dto;
    }
}

package com.fa.backend.controllers;

import com.fa.backend.config.JwtCore;
import com.fa.backend.domain.User;
import com.fa.backend.dto.UserChangePasswordDTO;
import com.fa.backend.dto.UserSignInDTO;
import com.fa.backend.dto.UserSignUpDTO;
import com.fa.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Класс представления системы авторизации и регистрации, предоставляет методы для работы системы безопасности
 * Имеет методы изменения пароля пользователя и обновления авторизационного токена
 */
@RestController
@RequestMapping("/auth")
public class SecurityController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody UserSignUpDTO userSignUpDTO) {
        if (userRepository.existsUserByUsername(userSignUpDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }

        String hashed = passwordEncoder.encode(userSignUpDTO.getPassword());
        User user = new User();
        user.setUsername(userSignUpDTO.getUsername());
        user.setPassword(hashed);
        userRepository.save(user);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody UserSignInDTO userSignInDTO) {
        Authentication authentication = null;
        try {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userSignInDTO.getUsername(),
                    userSignInDTO.getPassword()
            );
            authentication = authenticationManager.authenticate(token);

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwtCore.getTokenDTOFromJwt(token));
    }

    @PostMapping("/password/change")
    public ResponseEntity<?> getUserPortfolio(Principal principal, @RequestBody UserChangePasswordDTO dto) {
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    dto.getUsername(),
                    dto.getPassword()
            );
            authentication = authenticationManager.authenticate(token);

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String hashedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(hashedNewPassword);

        userRepository.save(user);

        return ResponseEntity.ok("Success");
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            try {
                String newToken = jwtCore.refreshToken(token);
                return ResponseEntity.ok(jwtCore.getTokenDTOFromJwt(newToken));
            } catch (SecurityException e) {
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Token is missing", HttpStatus.BAD_REQUEST);
        }
    }

}


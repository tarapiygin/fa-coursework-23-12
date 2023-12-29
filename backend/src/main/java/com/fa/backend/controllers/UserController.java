package com.fa.backend.controllers;

import com.fa.backend.domain.User;
import com.fa.backend.dto.UserGetDTO;
import com.fa.backend.repos.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Класс представления для пользователя
 */
@RestController
@RequestMapping("/secured")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public UserGetDTO getUser(Principal principal) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        UserGetDTO response = new UserGetDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        return response;
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> userDelete(Principal principal) {
        return ResponseEntity.noContent().build();
    }

}

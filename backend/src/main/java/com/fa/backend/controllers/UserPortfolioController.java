package com.fa.backend.controllers;

import com.fa.backend.domain.Purchase;
import com.fa.backend.domain.User;
import com.fa.backend.dto.PortfolioItemDTO;
import com.fa.backend.dto.YTMItemGetDTO;
import com.fa.backend.dto.YTMListDTO;
import com.fa.backend.logic.Utils;
import com.fa.backend.repos.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * Класс представления для Портфеля пользователя
 */
@RestController
@RequestMapping("/secured/user")
public class UserPortfolioController {

    private final UserRepository userRepository;

    public UserPortfolioController(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @GetMapping("/portfolio")
    public List<PortfolioItemDTO> getUserPortfolio(Principal principal) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Purchase> purchases = user.getPurchases();

        return Utils.calculatePortfolio(purchases);
    }

    @GetMapping("/portfolio/ytm/newton-raphson")
    public YTMListDTO getUserPortfolioYTMNewton(Principal principal) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Purchase> purchases = user.getPurchases();
        double tolerance = 0.0001;
        List<YTMItemGetDTO> ytmItemGetDTOList = Utils.calculatePortfolioYTM(purchases, tolerance, "NEWTON");
        double weightedYTM = Utils.calculateTotalPortfolioYTM(ytmItemGetDTOList, tolerance);
        YTMListDTO response = new YTMListDTO();
        response.setYtmPortfolio(weightedYTM);
        response.setYtmList(ytmItemGetDTOList);
        return response;
    }

    @GetMapping("/portfolio/ytm/basic")
    public YTMListDTO getUserPortfolioYTMBasic(Principal principal) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Purchase> purchases = user.getPurchases();
        double tolerance = 0.0001;
        List<YTMItemGetDTO> ytmItemGetDTOList = Utils.calculatePortfolioYTM(purchases, tolerance, "BASIC");
        double weightedYTM = Utils.calculateTotalPortfolioYTM(ytmItemGetDTOList, tolerance);
        YTMListDTO response = new YTMListDTO();
        response.setYtmPortfolio(weightedYTM);
        response.setYtmList(ytmItemGetDTOList);
        return response;
    }


}

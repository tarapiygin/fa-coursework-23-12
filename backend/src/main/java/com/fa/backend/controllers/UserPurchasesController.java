package com.fa.backend.controllers;

import com.fa.backend.domain.Bond;
import com.fa.backend.domain.Purchase;
import com.fa.backend.domain.User;
import com.fa.backend.dto.PurchaseCreateDTO;
import com.fa.backend.dto.PurchaseGetDTO;
import com.fa.backend.errors.PurchaseNotFoundException;
import com.fa.backend.repos.BondRepository;
import com.fa.backend.repos.UserRepository;
import com.fa.backend.service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс представления для сделок пользователя
 */
@RestController
@RequestMapping("/secured/user")
public class UserPurchasesController {

    private final UserRepository userRepository;

    private final BondRepository bondRepository;

    private final PurchaseService purchaseService;

    public UserPurchasesController(UserRepository userRepository,
                                   BondRepository bondRepository,
                                   PurchaseService purchaseService) {

        this.userRepository = userRepository;
        this.bondRepository = bondRepository;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/purchases")
    List<PurchaseGetDTO> getPurchaseList(Principal principal) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return user.getPurchases().stream()
                .map(purchaseService::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/purchases")
    ResponseEntity<PurchaseGetDTO> createPurchase(Principal principal, @RequestBody PurchaseCreateDTO dto) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Bond bond = bondRepository.findByTicket(dto.getBondTicket()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bond not found"));

        Purchase purchase = purchaseService.createPurchaseFromDTO(dto, user, bond);
        return ResponseEntity.created(URI.create(String.format("/purchases/%d", purchase.getId()))).body(purchaseService.convertToDTO(purchase));
    }

    @GetMapping("/purchases/{id}")
    ResponseEntity<PurchaseGetDTO> getPurchase(Principal principal, @PathVariable Long id) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        try {
            Purchase purchase = purchaseService.getPurchaseById(id);
            if (Objects.equals(purchase.getUser().getId(), user.getId()))
                return ResponseEntity.ok(purchaseService.convertToDTO(purchase));
        } catch (PurchaseNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping("/purchases/{id}")
    ResponseEntity<?> replacePurchase(Principal principal, @RequestBody PurchaseCreateDTO dto, @PathVariable Long id) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Bond bond = bondRepository.findByTicket(dto.getBondTicket()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bond not found"));
        try {

            Purchase purchase = purchaseService.getPurchaseById(id);
            if (!Objects.equals(user.getId(), purchase.getUser().getId()))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);


            purchaseService.updatePurchaseFromDTO(purchase, dto, user, bond);

            return ResponseEntity.noContent().build();
        } catch (PurchaseNotFoundException e) {
            Purchase newPurchase = purchaseService.createPurchaseFromDTO(id, dto, user, bond);

            return ResponseEntity.created(URI.create(String.format("/purchases/%d", id))).body(purchaseService.convertToDTO(newPurchase));
        }
    }

    @DeleteMapping("/purchases/{id}")
    ResponseEntity<?> deletePurchase(Principal principal, @PathVariable Long id) {
        if (principal == null) return null;
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        try {
            Purchase purchase = purchaseService.getPurchaseById(id);
            if (!Objects.equals(user.getId(), purchase.getUser().getId()))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            purchaseService.deletePurchaseById(id);
            return ResponseEntity.noContent().build();
        } catch (PurchaseNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

}

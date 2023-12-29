package com.fa.backend.service;

import com.fa.backend.domain.Bond;
import com.fa.backend.domain.Purchase;
import com.fa.backend.domain.User;
import com.fa.backend.dto.PurchaseCreateDTO;
import com.fa.backend.dto.PurchaseGetDTO;
import com.fa.backend.errors.PurchaseNotFoundException;
import com.fa.backend.repos.PurchaseRepository;
import lombok.Data;
import org.springframework.stereotype.Service;


@Service
@Data
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;

    }

    /**
     * Возвращает объект Purchase по его идентификатору.
     *
     * @param id идентификатор объекта Purchase
     * @return объект Purchase, соответствующий переданному идентификатору
     * @throws PurchaseNotFoundException если объект Purchase не найден
     */
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElseThrow(() -> new PurchaseNotFoundException(id));
    }


    public void deletePurchaseById(Long id) {
        purchaseRepository.deleteById(id);
    }


    public PurchaseGetDTO convertToDTO(Purchase purchase) {
        PurchaseGetDTO dto = new PurchaseGetDTO();
        dto.setId(purchase.getId());
        dto.setCreationDateTime(purchase.getCreationDateTime());
        dto.setQuantity(purchase.getQuantity());
        dto.setPrice(purchase.getPrice());
        if (purchase.getBond() != null) dto.setBondTicket(purchase.getBond().getTicket()); // Получаем ticket облигации
        return dto;
    }

    // Метод без id
    public Purchase createPurchaseFromDTO(PurchaseCreateDTO dto, User user, Bond bond) {
        return createPurchaseFromDTO(null, dto, user, bond);
    }

    // Метод с id
    public Purchase createPurchaseFromDTO(Long id, PurchaseCreateDTO dto, User user, Bond bond) {
        Purchase purchase = new Purchase();
        purchase.setCreationDateTime(dto.getCreationDateTime());
        purchase.setQuantity(dto.getQuantity());
        purchase.setPrice(dto.getPrice());
        purchase.setUser(user);
        purchase.setBond(bond);

        if (id != null) {
            purchase.setId(id);
        }

        return purchaseRepository.save(purchase);
    }

    public void updatePurchaseFromDTO(Purchase purchase, PurchaseCreateDTO dto, User user, Bond bond) {
        purchase.setCreationDateTime(dto.getCreationDateTime());
        purchase.setPrice(dto.getPrice());
        purchase.setQuantity(dto.getQuantity());
        purchase.setUser(user);
        purchase.setBond(bond);

        purchaseRepository.save(purchase);
    }
}

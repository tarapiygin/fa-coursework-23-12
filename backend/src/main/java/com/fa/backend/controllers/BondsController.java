package com.fa.backend.controllers;

import com.fa.backend.domain.Bond;
import com.fa.backend.errors.BondNotFoundException;
import com.fa.backend.repos.BondRepository;
import com.fa.backend.search.BondSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * Класс представления для Облигаций
 */
@RestController
@RequestMapping("/secured")
public class BondsController {

    private final BondRepository bondRepository;

    public BondsController(BondRepository bondRepository) {


        this.bondRepository = bondRepository;
    }

    //    @GetMapping("/bonds")
//    List<Bond> getBondList(Principal principal, @RequestParam) {
//        if (principal == null) return null;
//
//        return bondRepository.findAll();
//    }
    @GetMapping("/bonds")
    public List<Bond> getBondList(Principal principal,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date maturityDateFrom,
                                  @RequestParam(required = false) Double couponRateMin) {
        if (principal == null) return null;

        Specification<Bond> spec = Specification.where(BondSpecifications.maturityDateGreaterThanOrEqualTo(maturityDateFrom))
                .and(BondSpecifications.couponRateGreaterThanOrEqualTo(couponRateMin));

        return bondRepository.findAll(spec);
    }

    @GetMapping("/bonds/{ticket}")
    ResponseEntity<?> getBond(Principal principal, @PathVariable String ticket) {
        if (principal == null) return null;
        try {
            Bond bond = bondRepository.findByTicket(ticket).orElseThrow(() -> new BondNotFoundException(ticket));
            return ResponseEntity.ok(bond);
        } catch (BondNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

package com.fa.backend.errors;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(Long id) {
        super("Could not find Purchase " + id);
    }
}
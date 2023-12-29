package com.fa.backend.errors;

public class BondNotFoundException extends RuntimeException {

    public BondNotFoundException(String ticket) {
        super("Could not find Bond " + ticket);
    }
}
package com.thiagoalves.estaparbackendtest.exceptions;

public class SectorNotFoundException extends RuntimeException {
    public SectorNotFoundException(String message) {
        super(message);
    }
}

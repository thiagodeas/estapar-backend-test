package com.thiagoalves.estaparbackendtest.exceptions;

public class NoAvailableSectorException extends RuntimeException {
    public NoAvailableSectorException(String message) {
        super(message);
    }
}

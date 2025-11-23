package com.thiagoalves.estaparbackendtest.exceptions;

public class VehicleNotInsideException extends RuntimeException {
    public VehicleNotInsideException(String message) {
        super(message);
    }
}

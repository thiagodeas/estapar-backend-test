package com.thiagoalves.estaparbackendtest.exceptions;

public class VehicleAlreadyInsideException extends RuntimeException {
    public VehicleAlreadyInsideException(String message) {
        super(message);
    }
}

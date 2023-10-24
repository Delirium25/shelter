package com.github.delirium25.shelter.service;

public class DataInconsistencyException extends RuntimeException {

    public  DataInconsistencyException(String message) {
        super(message);
    }
}

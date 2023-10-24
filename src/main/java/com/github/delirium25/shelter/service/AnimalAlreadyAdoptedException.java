package com.github.delirium25.shelter.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AnimalAlreadyAdoptedException extends RuntimeException {

}

package com.example.demo.exception;

public class SessaoNaoAbertaException extends RuntimeException {
    public SessaoNaoAbertaException(String message) {
        super(message);
    }
}

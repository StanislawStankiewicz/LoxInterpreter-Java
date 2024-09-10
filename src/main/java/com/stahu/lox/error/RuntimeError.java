package com.stahu.lox.error;

import com.stahu.lox.model.Token;

public class RuntimeError extends RuntimeException {
    final Token token;

    public Token token() { return token; }

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
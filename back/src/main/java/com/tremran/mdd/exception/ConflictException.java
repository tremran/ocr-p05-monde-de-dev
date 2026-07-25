package com.tremran.mdd.exception;

/**
 * Indique qu'une opération viole une contrainte d'unicité ou d'état.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
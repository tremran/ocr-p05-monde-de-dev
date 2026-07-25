package com.tremran.mdd.exception;

/**
 * Indique qu'une ressource attendue est introuvable.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
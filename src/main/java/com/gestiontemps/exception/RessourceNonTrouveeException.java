package com.gestiontemps.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " avec l'ID " + id + " n'a pas été trouvé");
    }
}
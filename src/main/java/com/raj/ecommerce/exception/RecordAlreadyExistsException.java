package com.raj.ecommerce.exception;

public class RecordAlreadyExistsException extends RuntimeException{

    public RecordAlreadyExistsException(String message){
        super(message);
    }
}

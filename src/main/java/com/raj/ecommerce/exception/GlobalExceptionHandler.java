package com.raj.ecommerce.exception;

import com.raj.ecommerce.dto.ExceptionResponse;
import com.razorpay.RazorpayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String,String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error->errors.put(error.getField(),error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyExistRecord(RecordAlreadyExistsException ex){
        return new ResponseEntity<>(new ExceptionResponse("Record already exists with Us!",
                ex.getMessage(),HttpStatus.IM_USED.toString() ),HttpStatus.IM_USED);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundRecord(RecordNotFoundException ex){
        return new ResponseEntity<>(new ExceptionResponse("Record not found",
                ex.getMessage(),HttpStatus.NOT_FOUND.toString()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(BadRequestException ex){
        return new ResponseEntity<>(new ExceptionResponse("Bad Request- In proper details",
                ex.getMessage(),HttpStatus.BAD_REQUEST.toString()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerDownException.class)
    public ResponseEntity<ExceptionResponse> handleApplicationDown(ServerDownException ex){
        return new ResponseEntity<>(new ExceptionResponse("Something went wrong!",
                ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString()),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RazorpayException.class)
    public ResponseEntity<ExceptionResponse> handleRazorpayException(RazorpayException ex){
        return new ResponseEntity<>(new ExceptionResponse("RazorPay Server Down!",
                ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

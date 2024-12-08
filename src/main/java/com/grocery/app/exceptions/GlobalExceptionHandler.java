package com.grocery.app.exceptions;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.payloads.responses.ErrorResponse;
import com.grocery.app.payloads.responses.ViolationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        ResCode errorCode = ResCode.INTERNAl_SERVER_ERROR;
        ErrorResponse errorResponse=new ErrorResponse(errorCode.getCode(),e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ViolationErrorResponse> handleConstraintViolationException(MethodArgumentNotValidException e){
        Map<String,String> errors=new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error->{
            String fieldName=((FieldError) error).getField();
            String message=error.getDefaultMessage();
            errors.put(fieldName,message);
        });
        ViolationErrorResponse violationErrorResponse=new ViolationErrorResponse(ResCode.VALIDATION_ERROR.getCode(), ResCode.VALIDATION_ERROR.getMessage(),errors);
        return new ResponseEntity<>(violationErrorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceException.class)
    public  ResponseEntity<ErrorResponse> handleResourceException(ResourceException e){
        ErrorResponse errorResponse=new ErrorResponse(e.getCode(),e.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        ErrorResponse errorResponse=new ErrorResponse(e.getCode(),e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
        ErrorResponse errorResponse=new ErrorResponse(ResCode.FILE_UPLOAD_ERROR.getCode(),ResCode.FILE_UPLOAD_ERROR.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }






    

    
}
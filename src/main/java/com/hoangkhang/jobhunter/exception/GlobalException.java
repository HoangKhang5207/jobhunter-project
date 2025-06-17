package com.hoangkhang.jobhunter.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.hoangkhang.jobhunter.domain.response.RestResponse;
import com.hoangkhang.jobhunter.exception.custom.StorageException;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.exception.custom.PermissionException;

@RestControllerAdvice
public class GlobalException {

    // handle all exceptions
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllExceptions(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        restResponse.setMessage(ex.getMessage());
        restResponse.setError("Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(restResponse);
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class
    })
    public ResponseEntity<RestResponse<Object>> handleException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setMessage(ex.getMessage());
        restResponse.setError("Exception occurred");

        return ResponseEntity.badRequest().body(restResponse);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNoResourceFoundException(
            NoResourceFoundException ex) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        restResponse.setMessage(ex.getMessage());
        restResponse.setError("Resource not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).toList();
        restResponse.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.badRequest().body(restResponse);
    }

    @ExceptionHandler(value = StorageException.class)
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(StorageException ex) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setMessage(ex.getMessage());
        restResponse.setError("Exception uploading file");

        return ResponseEntity.badRequest().body(restResponse);
    }

    @ExceptionHandler(value = PermissionException.class)
    public ResponseEntity<RestResponse<Object>> handlePermissionException(PermissionException ex) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        restResponse.setMessage(ex.getMessage());
        restResponse.setError("Forbidden");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restResponse);
    }
}

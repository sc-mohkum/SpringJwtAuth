package com.kafka.springjwt.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<Object> handleDuplicateUsernameException(DuplicateUsernameException ex, WebRequest request) {
        String bodyOfResponse = "Username already exists. Please choose a different username.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object>handleNullUserNamePasswordException(IllegalArgumentException ex,WebRequest request)
    {
        String bodyOfResponse= ex.getMessage();
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.BAD_REQUEST,request);
    }
    

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String bodyOfResponse = "An unexpected error occurred";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public void handleExpiredJwtException(ExpiredJwtException ex, HttpServletResponse response) throws IOException {
        String bodyOfResponse = ex.getMessage();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(bodyOfResponse);
        response.getWriter().flush();
    }

    @ExceptionHandler(InavlidTokenAuthentication.class)
    public void handleInvalidTokenException(InavlidTokenAuthentication ex, HttpServletResponse response) throws IOException {
        String bodyOfResponse = ex.getMessage();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(bodyOfResponse);
        response.getWriter().flush();
    }
}

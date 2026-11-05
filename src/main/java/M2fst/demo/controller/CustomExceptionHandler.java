package M2fst.demo.controller;

import M2fst.demo.dto.ErreurExceptionDTO;
import M2fst.demo.exception.BadRequestException;
import M2fst.demo.exception.ForbiddenException;
import M2fst.demo.exception.NotFoundException;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    // Erreur 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErreurExceptionDTO> handleNotFound(NotFoundException ex) {
        ErreurExceptionDTO erreur = new ErreurExceptionDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    // Erreur 400 pour la validation dans les controllers
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurExceptionDTO> handleBadRequestValidController(MethodArgumentNotValidException ex){
        ErreurExceptionDTO erreur = new ErreurExceptionDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getFieldError().getDefaultMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }


    // Erreur 400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErreurExceptionDTO> handleBadRequest(BadRequestException ex) {
        ErreurExceptionDTO erreur = new ErreurExceptionDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    // Erreur 403 Forbidden pour le role dans les controllers
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErreurExceptionDTO> handleForbiddenController(AccessDeniedException ex) {
        ErreurExceptionDTO erreur = new ErreurExceptionDTO(
                HttpStatus.FORBIDDEN.value(),
                "Accès refusé : vous n'avez pas les autorisations"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erreur);
    }

    // Erreur 403 Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErreurExceptionDTO> handleForbidden(ForbiddenException ex) {
        ErreurExceptionDTO erreur = new ErreurExceptionDTO(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erreur);
    }

    // Erreur Interne 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurExceptionDTO> handleException(Exception ex) {
        ErreurExceptionDTO erreur = new ErreurExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Une erreur interne est survenue"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
    }
}

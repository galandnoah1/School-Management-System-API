package com.truhoster.school_management_system.common.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère le cas où une ressource demandée (Classroom, Student, Subject, SubjectByClassroom...)
     * n'existe pas en base.
     *
     * @param ex l'exception levée par le service
     * @return une réponse 404 avec le message d'erreur
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        log.warn("Ressource non trouvée: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Gère les erreurs de validation déclenchées par @Valid sur les DTOs de requête
     * (ex: champ obligatoire manquant, type invalide, etc.).
     * Renvoie un map champ -> message d'erreur pour chaque champ en erreur.
     *
     * @param ex l'exception contenant le détail des erreurs de binding
     * @return une réponse 400 avec la liste des champs en erreur
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        log.warn("Erreur de validation: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Gère les conflits métier (ex: association classe/matière déjà existante).
     * Utilisé quand une opération est refusée pour une raison logique/métier,
     * indépendamment de la validité technique de la requête.
     *
     * @param ex l'exception levée par le service
     * @return une réponse 409 avec le message d'erreur
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        log.warn("Conflit métier: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Filet de sécurité pour toute exception non gérée explicitement ci-dessus.
     * Évite de renvoyer une stacktrace brute au client et logue l'erreur complète côté serveur.
     *
     * @param ex l'exception inattendue
     * @return une réponse 500 générique
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}
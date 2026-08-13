package com.truhoster.school_management_system.schoolfee.controller;

import com.truhoster.school_management_system.schoolfee.dto.SchoolFeeRequest;
import com.truhoster.school_management_system.schoolfee.dto.SchoolFeeResponse;
import com.truhoster.school_management_system.schoolfee.service.SchoolFeeService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/school-fees")
@RequiredArgsConstructor
@Tag(name = "School Fees", description = "Gestion des grilles de frais de scolarité par classe")
public class SchoolFeeController {

    private final SchoolFeeService schoolFeeService;

    @Operation(summary = "Créer une grille de frais")
    @PostMapping
    public ResponseEntity<SchoolFeeResponse> create(@Valid @RequestBody SchoolFeeRequest request) {
        log.info("POST /school-fees - création avec la requête: {}", request);
        SchoolFeeResponse response = schoolFeeService.create(request);
        log.info("Grille de frais créée avec succès: {}", response.getLabel());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Mettre à jour une grille de frais")
    @PutMapping("/{id}")
    public ResponseEntity<SchoolFeeResponse> update(
            @Parameter(description = "Identifiant de la grille") @PathVariable Integer id,
            @Valid @RequestBody SchoolFeeRequest request) {
        log.info("PUT /school-fees/{} - mise à jour avec la requête: {}", id, request);
        SchoolFeeResponse response = schoolFeeService.update(id, request);
        log.info("Grille de frais {} mise à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une grille de frais par son id")
    @GetMapping("/{id}")
    public ResponseEntity<SchoolFeeResponse> getById(
            @Parameter(description = "Identifiant de la grille") @PathVariable Integer id) {
        log.info("GET /school-fees/{}", id);
        return ResponseEntity.ok(schoolFeeService.getById(id));
    }

    @Operation(summary = "Lister toutes les grilles de frais")
    @GetMapping
    public ResponseEntity<List<SchoolFeeResponse>> getAll() {
        log.info("GET /school-fees - récupération de toutes les grilles");
        List<SchoolFeeResponse> responses = schoolFeeService.getAll();
        log.info("{} grille(s) récupérée(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Récupérer la grille de frais d'une classe")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<SchoolFeeResponse> getByClassroom(
            @Parameter(description = "Identifiant de la classe") @PathVariable Integer classroomId) {
        log.info("GET /school-fees/classroom/{}", classroomId);
        return ResponseEntity.ok(schoolFeeService.getByClassroom(classroomId));
    }

    @Operation(summary = "Supprimer une grille de frais")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de la grille") @PathVariable Integer id) {
        log.info("DELETE /school-fees/{}", id);
        schoolFeeService.delete(id);
        log.info("Grille de frais {} supprimée avec succès", id);
        return ResponseEntity.noContent().build();
    }
}
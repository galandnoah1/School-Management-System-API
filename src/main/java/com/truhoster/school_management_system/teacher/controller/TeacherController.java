package com.truhoster.school_management_system.teacher.controller;

import com.truhoster.school_management_system.teacher.dto.AffectationRequest;
import com.truhoster.school_management_system.teacher.dto.TeacherRequest;
import com.truhoster.school_management_system.teacher.dto.TeacherResponse;
import com.truhoster.school_management_system.teacher.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Gestion des professeurs et de leurs affectations")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * Crée un nouveau professeur ainsi que sa première affectation (classe + matière).
     *
     * @param request les données du professeur à créer
     * @return le DTO du professeur créé, avec statut 201
     */
    @Operation(summary = "Créer un professeur", description = "Crée un professeur et son affectation initiale à une classe/matière.")
    @PostMapping
    public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        log.info("POST /teachers - création d'un professeur avec la requête: {}", request);
        TeacherResponse response = teacherService.create(request);
        log.info("Professeur créé avec succès: {}", response.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Met à jour les informations d'un professeur existant.
     * Si classroomId/subjectId sont fournis, une nouvelle affectation est créée
     * (erreur 409 si elle existe déjà).
     *
     * @param id l'identifiant du professeur à mettre à jour
     * @param request les nouvelles données
     * @return le DTO du professeur mis à jour
     */
    @Operation(summary = "Mettre à jour un professeur", description = "Met à jour les informations d'un professeur et peut ajouter une nouvelle affectation.")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> update( @PathVariable Integer id,
            @Valid @RequestBody AffectationRequest request) {
        log.info("PUT /teachers/{} - mise à jour avec la requête: {}", id, request);
        TeacherResponse response = teacherService.update(id, request);
        log.info("Professeur {} mis à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère un professeur par son id, avec ses affectations.
     *
     * @param id l'identifiant recherché
     * @return le DTO du professeur trouvé
     */
    @Operation(summary = "Récupérer un professeur par son id")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getById(@PathVariable Integer id) {
        log.info("GET /teachers/{}", id);
        TeacherResponse response = teacherService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère la liste de tous les professeurs.
     *
     * @return la liste des DTOs de tous les professeurs
     */
    @Operation(summary = "Lister tous les professeurs")
    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAll() {
        log.info("GET /teachers - récupération de tous les professeurs");
        List<TeacherResponse> responses = teacherService.getAll();
        log.info("{} professeur(s) récupéré(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * Récupère tous les enseignants intervenant dans une classe donnée.
     *
     * @param classroomId l'identifiant de la classe
     * @return la liste des DTOs des professeurs de cette classe
     */
    @Operation(summary = "Lister les professeurs d'une classe")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<TeacherResponse>> getByClassroom( @PathVariable Integer classroomId) {
        log.info("GET /teachers/classroom/{}", classroomId);
        List<TeacherResponse> responses = teacherService.getByClassroom(classroomId);
        log.info("{} professeur(s) trouvé(s) pour la classe {}", responses.size(), classroomId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Supprime un professeur par son id (et ses affectations, en cascade).
     *
     * @param id l'identifiant du professeur à supprimer
     * @return réponse vide avec statut 204
     */
    @Operation(summary = "Supprimer un professeur", description = "Supprime le professeur et toutes ses affectations associées.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /teachers/{}", id);
        teacherService.delete(id);
        log.info("Professeur {} supprimé avec succès", id);
        return ResponseEntity.noContent().build();
    }
}
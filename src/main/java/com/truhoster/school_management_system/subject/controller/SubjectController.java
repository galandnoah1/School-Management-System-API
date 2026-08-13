package com.truhoster.school_management_system.subject.controller;

import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.subject.dto.SubjectByClassroomRequest;
import com.truhoster.school_management_system.subject.dto.SubjectByClassroomResponse;
import com.truhoster.school_management_system.subject.dto.SubjectRequest;
import com.truhoster.school_management_system.subject.dto.SubjectResponse;
import com.truhoster.school_management_system.subject.service.SubjectService;
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
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Subjects", description = "Gestion des matières et de leur association aux classes")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Crée une nouvelle matière.
     *
     * @param request les données de la matière à créer
     * @return le DTO de la matière créée, avec statut 201
     */
    @Operation(summary = "Créer une matière", description = "Crée une nouvelle matière.")
    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        log.info("POST /subjects - création d'une matière avec la requête: {}", request);
        SubjectResponse response = subjectService.create(request);
        log.info("Matière créée avec succès: {}", response.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Met à jour une matière existante.
     *
     * @param id l'identifiant de la matière à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de la matière mise à jour
     */
    @Operation(summary = "Mettre à jour une matière", description = "Met à jour les informations d'une matière existante.")
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> update(@PathVariable Integer id,
            @Valid @RequestBody SubjectRequest request) {
        log.info("PUT /subjects/{} - mise à jour avec la requête: {}", id, request);
        SubjectResponse response = subjectService.update(id, request);
        log.info("Matière {} mise à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère une matière par son id, avec la liste de ses classes associées.
     *
     * @param id l'identifiant recherché
     * @return le DTO de la matière trouvée
     */
    @Operation(summary = "Récupérer une matière par son id")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getById( @PathVariable Integer id) {
        log.info("GET /subjects/{}", id);
        SubjectResponse response = subjectService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère la liste de toutes les matières.
     *
     * @return la liste des DTOs de toutes les matières
     */
    @Operation(summary = "Lister toutes les matières")
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAll() {
        log.info("GET /subjects - récupération de toutes les matières");
        List<SubjectResponse> responses = subjectService.getAll();
        log.info("{} matière(s) récupérée(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * Récupère toutes les matières d'une section donnée.
     *
     * @param section la section recherchée
     * @return la liste des DTOs des matières de cette section
     */
    @Operation(summary = "Lister les matières par section")
    @GetMapping("/section/{section}")
    public ResponseEntity<List<SubjectResponse>> getBySection( @PathVariable Section section) {
        log.info("GET /subjects/section/{}", section);
        List<SubjectResponse> responses = subjectService.getBySection(section);
        log.info("{} matière(s) trouvée(s) pour la section {}", responses.size(), section);
        return ResponseEntity.ok(responses);
    }

    /**
     * Supprime une matière par son id.
     *
     * @param id l'identifiant de la matière à supprimer
     * @return réponse vide avec statut 204
     */
    @Operation(summary = "Supprimer une matière")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /subjects/{}", id);
        subjectService.delete(id);
        log.info("Matière {} supprimée avec succès", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Associe une matière à une classe en définissant son coefficient.
     *
     * @param request les données (coefficient, classroomId, subjectId)
     * @return le DTO de l'association créée, avec statut 201
     */
    @Operation(summary = "Associer une matière à une classe", description = "Crée une association matière/classe avec un coefficient.")
    @PostMapping("/coefficients")
    public ResponseEntity<SubjectByClassroomResponse> setCoefficient(
            @Valid @RequestBody SubjectByClassroomRequest request) {
        log.info("POST /subjects/coefficients - création d'une association avec la requête: {}", request);
        SubjectByClassroomResponse response = subjectService.setCoefficient(request);
        log.info("Association matière/classe créée avec succès (coefficient: {})", response.getCoefficient());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Met à jour le coefficient d'une association matière/classe existante.
     *
     * @param id l'identifiant de l'association SubjectByClassroom
     * @param coefficient le nouveau coefficient
     * @return le DTO de l'association mise à jour
     */
    @Operation(summary = "Modifier le coefficient d'une matière pour une classe")
    @PatchMapping("/coefficients/{id}")
    public ResponseEntity<SubjectByClassroomResponse> updateCoefficient( @PathVariable Integer id,@RequestParam int coefficient) {
        log.info("PATCH /subjects/coefficients/{} - nouveau coefficient: {}", id, coefficient);
        SubjectByClassroomResponse response = subjectService.updateCoefficient(id, coefficient);
        log.info("Coefficient de l'association {} mis à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère toutes les matières enseignées dans une classe donnée.
     *
     * @param classroomId l'identifiant de la classe
     * @return la liste des DTOs des matières de cette classe
     */
    @Operation(summary = "Lister les matières d'une classe")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<SubjectByClassroomResponse>> getByClassroom( @PathVariable Integer classroomId) {
        log.info("GET /subjects/classroom/{}", classroomId);
        List<SubjectByClassroomResponse> responses = subjectService.getByClassroom(classroomId);
        log.info("{} matière(s) trouvée(s) pour la classe {}", responses.size(), classroomId);
        return ResponseEntity.ok(responses);
    }
}
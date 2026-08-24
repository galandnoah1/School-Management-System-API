package com.truhoster.school_management_system.student.controller;

import com.truhoster.school_management_system.student.dto.StudentRequest;
import com.truhoster.school_management_system.student.dto.StudentResponse;
import com.truhoster.school_management_system.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Gestion des étudiants (CRUD et recherche par classe)")
public class StudentController {

    private final StudentService studentService;

    /**
     * Crée un nouvel étudiant.
     * Le matricule est généré automatiquement côté service.
     *
     * @param request les données de l'étudiant à créer
     * @return le DTO de l'étudiant créé, avec statut 201
     */
    @Operation(summary = "Créer un étudiant", description = "Crée un nouvel étudiant et génère automatiquement son matricule.")
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        log.info("POST /students - création d'un étudiant avec la requête: {}", request);
        StudentResponse response = studentService.create(request);
        log.info("Étudiant créé avec succès: {} {} (matricule: {})",
                response.getFirstname(), response.getLastname(), response.getMatricule());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Met à jour un étudiant existant.
     * Le matricule reste inchangé.
     *
     * @param id l'identifiant de l'étudiant à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de l'étudiant mis à jour
     */
    @Operation(summary = "Mettre à jour un étudiant", description = "Met à jour les informations d'un étudiant existant.")
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
            @Parameter(description = "Identifiant de l'étudiant") @PathVariable Integer id,
            @Valid @RequestBody StudentRequest request) {
        log.info("PUT /students/{} - mise à jour avec la requête: {}", id, request);
        StudentResponse response = studentService.update(id, request);
        log.info("Étudiant {} mis à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère un étudiant par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO de l'étudiant trouvé
     */
    @Operation(summary = "Récupérer un étudiant par son id")
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(
            @Parameter(description = "Identifiant de l'étudiant") @PathVariable Integer id) {
        log.info("GET /students/{}", id);
        StudentResponse response = studentService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère la liste de tous les étudiants.
     *
     * @return la liste des DTOs de tous les étudiants
     */
    @Operation(summary = "Lister tous les étudiants")
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        log.info("GET /students - récupération de tous les étudiants");
        List<StudentResponse> responses = studentService.getAll();
        log.info("{} étudiant(s) récupéré(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * Récupère tous les étudiants appartenant à une classe donnée.
     *
     * @param classroomId l'identifiant de la classe
     * @return la liste des DTOs des étudiants de cette classe
     */
    @Operation(summary = "Lister les étudiants par classe")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<StudentResponse>> getByClassroom(
            @Parameter(description = "Identifiant de la classe") @PathVariable Integer classroomId) {
        log.info("GET /students/classroom/{}", classroomId);
        List<StudentResponse> responses = studentService.getByClassroom(classroomId);
        log.info("{} étudiant(s) trouvé(s) pour la classe {}", responses.size(), classroomId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Supprime un étudiant par son id.
     *
     * @param id l'identifiant de l'étudiant à supprimer
     * @return réponse vide avec statut 204
     */
    @Operation(summary = "Supprimer un étudiant")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de l'étudiant") @PathVariable Integer id) {
        log.info("DELETE /students/{}", id);
        studentService.delete(id);
        log.info("Étudiant {} supprimé avec succès", id);
        return ResponseEntity.noContent().build();
    }
}
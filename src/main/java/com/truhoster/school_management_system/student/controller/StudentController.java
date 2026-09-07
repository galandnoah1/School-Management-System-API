package com.truhoster.school_management_system.student.controller;

import com.truhoster.school_management_system.student.dto.StudentRequest;
import com.truhoster.school_management_system.student.dto.StudentResponse;
import com.truhoster.school_management_system.student.dto.UpdateStudent;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.util.Arrays;
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
     * @param request les données de l'étudiant à créer
     * @return le DTO de l'étudiant créé, avec statut 201
     */
    @Operation(summary = "Créer un étudiant", description = "Crée un nouvel étudiant et génère automatiquement son matricule.")
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request, MultipartFile file) throws IOException {
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
     * @param update les nouvelles données
     * @return le DTO de l'étudiant mis à jour
     */

    @Operation(summary = "Mettre à jour un étudiant", description = "Met à jour les informations d'un étudiant existant.")
    @PutMapping(value = "/{id}")
    public ResponseEntity<StudentResponse> update(
            @PathVariable Integer id,
            @RequestBody UpdateStudent update) throws IOException {
        log.info("PUT /students/{} - modification des informations de l'élève: {}", id, update);


        StudentResponse response = studentService.update(id, update);


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
            @PathVariable Integer id) {
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
            @PathVariable Integer classroomId) {
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
             @PathVariable Integer id) {
        log.info("DELETE /students/{}", id);
        studentService.delete(id);
        log.info("Étudiant {} supprimé avec succès", id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/pictures", consumes = "multipart/form-data")
    public ResponseEntity<StudentResponse> addProfilePicture(@PathVariable Integer id ,@RequestParam("image") MultipartFile file)
    {
        log.info("PATCH /api/v1/students/{id}/pictures - ajoute ou change la photo de profil d'un élève");

        StudentResponse studentResponse = studentService.addStudentProfilePicture(id, file);

        return ResponseEntity
                .ok(studentResponse);
    }
}
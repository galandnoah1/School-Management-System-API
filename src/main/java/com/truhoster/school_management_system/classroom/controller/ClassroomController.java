package com.truhoster.school_management_system.classroom.controller;

import com.truhoster.school_management_system.classroom.dto.ClassroomRequest;
import com.truhoster.school_management_system.classroom.dto.ClassroomResponse;
import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.classroom.service.ClassroomService;
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
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
@Tag(name = "Classrooms", description = "Gestion des classes (CRUD et recherche par section)")
public class ClassroomController {

    private final ClassroomService classroomService;

    @Operation(summary = "Créer une classe", description = "Crée une nouvelle classe et génère automatiquement son nom.")
    @PostMapping
    public ResponseEntity<ClassroomResponse> create(@Valid @RequestBody ClassroomRequest request) {
        log.info("POST /classrooms - création d'une classe avec la requête: {}", request);
        ClassroomResponse response = classroomService.create(request);
        log.info("Classe créée avec succès: {}", response.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Mettre à jour une classe", description = "Met à jour une classe existante et régénère son nom.")
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResponse> update( @PathVariable Integer id,
            @Valid @RequestBody ClassroomRequest request) {
        log.info("PUT /classrooms/{} - mise à jour avec la requête: {}", id, request);
        ClassroomResponse response = classroomService.update(id, request);
        log.info("Classe {} mise à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une classe par son id")
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getById(@PathVariable Integer id) {
        log.info("GET /classrooms/{}", id);
        ClassroomResponse response = classroomService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister toutes les classes")
    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> getAll() {
        log.info("GET /classrooms - récupération de toutes les classes");
        List<ClassroomResponse> responses = classroomService.getAll();
        log.info("{} classe(s) récupérée(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Lister les classes par section")
    @GetMapping("/section/{section}")
    public ResponseEntity<List<ClassroomResponse>> getBySection(@PathVariable Section section) {
        log.info("GET /classrooms/section/{}", section);
        List<ClassroomResponse> responses = classroomService.getBySection(section);
        log.info("{} classe(s) trouvée(s) pour la section {}", responses.size(), section);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Supprimer une classe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /classrooms/{}", id);
        classroomService.delete(id);
        log.info("Classe {} supprimée avec succès", id);
        return ResponseEntity.noContent().build();
    }
}
package com.truhoster.school_management_system.note.controller;

import com.truhoster.school_management_system.note.dto.NoteRequest;
import com.truhoster.school_management_system.note.dto.NoteResponse;
import com.truhoster.school_management_system.note.entity.BulkNoteRequest;
import com.truhoster.school_management_system.note.enums.Evaluation;
import com.truhoster.school_management_system.note.service.NoteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notes", description = "Gestion des notes des élèves et répercussion automatique sur les bulletins")
public class NoteController {
    private final NoteService noteService;

    /**
     * Enregistre une nouvelle note pour un élève.
     * Crée ou met à jour automatiquement le bulletin (ReportCard) et la ligne de matière
     * (NoteLine) correspondants.
     *
     * @param request les données de la note à enregistrer
     * @return le DTO de la note créée, avec statut 201
     */
    @Operation(summary = "Enregistrer une note", description = "Enregistre une note et répercute automatiquement sur le bulletin de l'élève.")
    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request) {
        log.info("POST /notes - création d'une note avec la requête: {}", request);
        NoteResponse response = noteService.create(request);
        log.info("Note créée avec succès pour l'étudiant id={}", request.getStudentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Met à jour une note existante.
     * Si l'évaluation change, l'ancienne valeur est effacée de son ancienne NoteLine
     * et la nouvelle valeur appliquée à la NoteLine correcte.
     *
     * @param id l'identifiant de la note à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de la note mise à jour
     */
    @Operation(summary = "Modifier une note", description = "Modifie une note et répercute le changement sur la NoteLine associée.")
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(
            @Parameter(description = "Identifiant de la note") @PathVariable Integer id,
            @Valid @RequestBody NoteRequest request) {
        log.info("PUT /notes/{} - mise à jour avec la requête: {}", id, request);
        NoteResponse response = noteService.update(id, request);
        log.info("Note {} mise à jour avec succès", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère une note par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO de la note trouvée
     */
    @Operation(summary = "Récupérer une note par son id")
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(
            @Parameter(description = "Identifiant de la note") @PathVariable Integer id) {
        log.info("GET /notes/{}", id);
        NoteResponse response = noteService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère la liste de toutes les notes.
     *
     * @return la liste des DTOs de toutes les notes
     */
    @Operation(summary = "Lister toutes les notes")
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAll() {
        log.info("GET /notes - récupération de toutes les notes");
        List<NoteResponse> responses = noteService.getAll();
        log.info("{} note(s) récupérée(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * Récupère les notes d'une classe, pour une matière et une évaluation données.
     *
     * @param classroomId l'identifiant de la classe
     * @param subjectId l'identifiant de la matière
     * @param evaluation le type d'évaluation recherché
     * @return la liste des DTOs des notes correspondantes
     */
    @Operation(summary = "Lister les notes par classe, matière et évaluation")
    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> getByClassroomSubjectEvaluation(
            @Parameter(description = "Identifiant de la classe") @RequestParam Integer classroomId,
            @Parameter(description = "Identifiant de la matière") @RequestParam Integer subjectId,
            @Parameter(description = "Type d'évaluation") @RequestParam Evaluation evaluation) {
        log.info("GET /notes/search?classroomId={}&subjectId={}&evaluation={}",
                classroomId, subjectId, evaluation);
        List<NoteResponse> responses = noteService.getByClassroomSubjectEvaluation(
                classroomId, subjectId, evaluation);
        log.info("{} note(s) trouvée(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * Supprime une note par son id.
     * Efface également la valeur correspondante (note1 ou note2) dans la NoteLine associée.
     *
     * @param id l'identifiant de la note à supprimer
     * @return réponse vide avec statut 204
     */
    @Operation(summary = "Supprimer une note", description = "Supprime la note et efface la valeur correspondante dans le bulletin.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de la note") @PathVariable Integer id) {
        log.info("DELETE /notes/{}", id);
        noteService.delete(id);
        log.info("Note {} supprimée avec succès", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Enregistre plusieurs notes en une fois pour une même matière/évaluation/classe.
     *
     * @param request les données communes + la liste des notes par élève
     * @return la liste des DTOs des notes créées, avec statut 201
     */
    @Operation(summary = "Enregistrer plusieurs notes en masse",
            description = "Enregistre les notes de plusieurs élèves pour une même matière, évaluation et classe.")
    @PostMapping("/bulk")
    public ResponseEntity<List<NoteResponse>> createBulk(@Valid @RequestBody BulkNoteRequest request) {
        log.info("POST /notes/bulk - enregistrement en masse pour subjectId={}, classroomId={}, evaluation={}, {} élève(s)",
                request.getSubjectId(), request.getClassroomId(), request.getEvaluation(), request.getNotes().size());
        List<NoteResponse> responses = noteService.createBulk(request);
        log.info("{} note(s) enregistrée(s) avec succès en masse", responses.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}

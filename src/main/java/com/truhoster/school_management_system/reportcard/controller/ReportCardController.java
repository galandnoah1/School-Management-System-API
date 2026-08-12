package com.truhoster.school_management_system.reportcard.controller;

import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.reportcard.dto.ReportCardResponse;
import com.truhoster.school_management_system.reportcard.service.ReportCardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reportcards")
@RequiredArgsConstructor
@Tag(name = "Report Cards", description = "Génération et consultation des bulletins")
public class ReportCardController {

    private final ReportCardService reportCardService;

    /**
     * Génère (calcule) les bulletins de toute une classe pour un trimestre donné :
     * moyennes individuelles, classement et statistiques de classe.
     *
     * @param classroomId l'identifiant de la classe
     * @param trimester le trimestre concerné
     * @return la liste des DTOs des bulletins générés, triés par rang
     */
    @Operation(summary = "Générer les bulletins d'une classe", description = "Calcule les moyennes, le classement et les statistiques de classe pour un trimestre.")
    @PostMapping("/generate")
    public ResponseEntity<List<ReportCardResponse>> generate(@RequestParam Integer classroomId, @RequestParam Trimester trimester) {
        log.info("POST /reportcards/generate?classroomId={}&trimester={}", classroomId, trimester);
        List<ReportCardResponse> responses = reportCardService.generate(classroomId, trimester);
        log.info("{} bulletin(s) généré(s) pour la classe {} - trimestre {}", responses.size(), classroomId, trimester);
        return ResponseEntity.ok(responses);
    }

    /**
     * Récupère un bulletin par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO du bulletin trouvé
     */
    @Operation(summary = "Récupérer un bulletin par son id")
    @GetMapping("/{id}")
    public ResponseEntity<ReportCardResponse> getById(
            @Parameter(description = "Identifiant du bulletin") @PathVariable Integer id) {
        log.info("GET /reportcards/{}", id);
        return ResponseEntity.ok(reportCardService.getById(id));
    }

    /**
     * Récupère le bulletin d'un élève pour un trimestre donné.
     *
     * @param studentId l'identifiant de l'élève
     * @param trimester le trimestre concerné
     * @return le DTO du bulletin trouvé
     */
    @Operation(summary = "Récupérer le bulletin d'un élève par trimestre")
    @GetMapping("/student/{studentId}/trimester/{trimester}")
    public ResponseEntity<ReportCardResponse> getByStudentAndTrimester(
            @Parameter(description = "Identifiant de l'élève") @PathVariable Integer studentId,
            @Parameter(description = "Trimestre concerné") @PathVariable Trimester trimester) {
        log.info("GET /reportcards/student/{}/trimester/{}", studentId, trimester);
        return ResponseEntity.ok(reportCardService.getByStudentAndTrimester(studentId, trimester));
    }
}
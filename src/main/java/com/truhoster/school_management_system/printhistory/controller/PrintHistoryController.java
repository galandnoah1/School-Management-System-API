package com.truhoster.school_management_system.printhistory.controller;

import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.printhistory.dto.PrintHistoryResponse;
import com.truhoster.school_management_system.printhistory.enums.PrintAction;
import com.truhoster.school_management_system.printhistory.service.PrintHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/print-history")
@RequiredArgsConstructor
@Tag(name = "Print History", description = "Suivi des générations, impressions et téléchargements de bulletins")
public class PrintHistoryController {

    private final PrintHistoryService printHistoryService;

    /**
     * Enregistre l'impression d'un bulletin (appelé au clic sur le bouton Imprimer côté front).
     *
     * @param reportCardId l'identifiant du bulletin imprimé
     * @return le DTO de la ligne d'historique mise à jour ou créée
     */
    @Operation(summary = "Imprimer un bulletin", description = "Incrémente le compteur d'impressions de ce bulletin.")
    @PostMapping("/reportcards/{reportCardId}/print")
    public ResponseEntity<PrintHistoryResponse> print(
            @Parameter(description = "Identifiant du bulletin") @PathVariable Integer reportCardId) {
        log.info("POST /print-history/reportcards/{}/print", reportCardId);
        PrintHistoryResponse response = printHistoryService.printReportCard(reportCardId);
        log.info("Impression du bulletin {} enregistrée (total: {})", reportCardId, response.getCount());
        return ResponseEntity.ok(response);
    }

    /**
     * Enregistre le téléchargement d'un bulletin (appelé au clic sur le bouton Télécharger côté front).
     *
     * @param reportCardId l'identifiant du bulletin téléchargé
     * @return le DTO de la ligne d'historique mise à jour ou créée
     */
    @Operation(summary = "Télécharger un bulletin", description = "Incrémente le compteur de téléchargements de ce bulletin.")
    @PostMapping("/reportcards/{reportCardId}/download")
    public ResponseEntity<PrintHistoryResponse> download(
            @Parameter(description = "Identifiant du bulletin") @PathVariable Integer reportCardId) {
        log.info("POST /print-history/reportcards/{}/download", reportCardId);
        PrintHistoryResponse response = printHistoryService.downloadReportCard(reportCardId);
        log.info("Téléchargement du bulletin {} enregistré (total: {})", reportCardId, response.getCount());
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère tout l'historique (générations + impressions + téléchargements).
     *
     * @return la liste des DTOs de tout l'historique
     */
    @Operation(summary = "Lister tout l'historique")
    @GetMapping
    public ResponseEntity<List<PrintHistoryResponse>> getAll() {
        log.info("GET /print-history - récupération de tout l'historique");
        return ResponseEntity.ok(printHistoryService.getAll());
    }

    /**
     * Récupère l'historique d'une classe pour un trimestre donné.
     *
     * @param classroomId l'identifiant de la classe
     * @param trimester le trimestre concerné
     * @return la liste des DTOs des entrées correspondantes
     */
    @Operation(summary = "Lister l'historique d'une classe par trimestre")
    @GetMapping("/classroom/{classroomId}/trimester/{trimester}")
    public ResponseEntity<List<PrintHistoryResponse>> getByClassroomAndTrimester(
            @Parameter(description = "Identifiant de la classe") @PathVariable Integer classroomId,
            @Parameter(description = "Trimestre concerné") @PathVariable Trimester trimester) {
        log.info("GET /print-history/classroom/{}/trimester/{}", classroomId, trimester);
        return ResponseEntity.ok(printHistoryService.getByClassroomAndTrimester(classroomId, trimester));
    }

    /**
     * Filtre l'historique par plage de dates.
     *
     * @param start date/heure de début (ISO)
     * @param end date/heure de fin (ISO)
     * @return la liste des DTOs des entrées dans cette plage
     */
    @Operation(summary = "Filtrer l'historique par date")
    @GetMapping("/search")
    public ResponseEntity<List<PrintHistoryResponse>> search(
            @Parameter(description = "Date de début (ISO)") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Date de fin (ISO)") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("GET /print-history/search?start={}&end={}", start, end);
        return ResponseEntity.ok(printHistoryService.getByDateRange(
                Timestamp.valueOf(start), Timestamp.valueOf(end)));
    }

    /**
     * Récupère le total cumulé (somme des count) d'impressions ou de téléchargements
     * pour toute une classe/trimestre.
     *
     * @param classroomId l'identifiant de la classe
     * @param trimester le trimestre concerné
     * @param action l'action concernée (PRINTED ou DOWNLOADED)
     * @return le total cumulé
     */
    @Operation(summary = "Total des impressions/téléchargements d'une classe", description = "Somme cumulée de l'action demandée pour tous les bulletins de la classe/trimestre.")
    @GetMapping("/classroom/{classroomId}/trimester/{trimester}/total")
    public ResponseEntity<Integer> getTotal(@PathVariable Integer classroomId, @PathVariable Trimester trimester, @RequestParam PrintAction action) {
        log.info("GET /print-history/classroom/{}/trimester/{}/total?action={}", classroomId, trimester, action);
        int total = printHistoryService.getTotalByClassroomTrimesterAndAction(classroomId, trimester, action);
        return ResponseEntity.ok(total);
    }
}
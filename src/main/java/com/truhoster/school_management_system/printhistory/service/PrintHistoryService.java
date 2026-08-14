package com.truhoster.school_management_system.printhistory.service;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.printhistory.dto.PrintHistoryResponse;
import com.truhoster.school_management_system.printhistory.entity.PrintHistory;
import com.truhoster.school_management_system.printhistory.enums.PrintAction;
import com.truhoster.school_management_system.printhistory.mapper.PrintHistoryMapper;
import com.truhoster.school_management_system.printhistory.repository.PrintHistoryRepository;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import com.truhoster.school_management_system.reportcard.repository.ReportCardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrintHistoryService {

    private final PrintHistoryRepository printHistoryRepository;
    private final PrintHistoryMapper printHistoryMapper;
    private final ReportCardRepository reportCardRepository;

    /**
     * Enregistre/actualise l'événement de génération pour une classe/trimestre.
     * Si une ligne GENERATED existe déjà pour ce couple, son count est écrasé avec la nouvelle
     * valeur (reflète la dernière génération). Sinon, une nouvelle ligne est créée.
     * Appelé automatiquement depuis ReportCardService.generate(), jamais depuis un endpoint direct.
     *
     * @param classroom la classe concernée
     * @param trimester le trimestre concerné
     * @param count le nombre de bulletins générés lors de cet appel
     */
    @Transactional
    public void logGeneration(Classroom classroom, Trimester trimester, int count) {
        PrintHistory printHistory = printHistoryRepository
                .findByClassroomIdAndTrimesterAndActionAndReportCardIsNull(
                        classroom.getId(), trimester, PrintAction.GENERATED)
                .orElseGet(() -> PrintHistory.builder()
                        .action(PrintAction.GENERATED)
                        .classroom(classroom)
                        .trimester(trimester)
                        .reportCard(null)
                        .build());

        printHistory.setCount(count);
        printHistoryRepository.save(printHistory);
    }

    /**
     * Enregistre l'impression d'un bulletin individuel.
     * Si le bulletin a déjà été imprimé au moins une fois, incrémente le compteur existant
     * et écrase performedBy avec la dernière personne. Sinon, crée une nouvelle ligne (count=1).
     *
     * @param reportCardId l'identifiant du bulletin imprimé
     * @return le DTO de la ligne d'historique mise à jour ou créée
     * @throws EntityNotFoundException si le bulletin n'existe pas
     */
    @Transactional
    public PrintHistoryResponse printReportCard(Integer reportCardId) {
        return logPrintOrDownload(reportCardId, PrintAction.PRINTED);
    }

    /**
     * Enregistre le téléchargement d'un bulletin individuel.
     * Même logique d'incrémentation que printReportCard.
     *
     * @param reportCardId l'identifiant du bulletin téléchargé
     * @return le DTO de la ligne d'historique mise à jour ou créée
     * @throws EntityNotFoundException si le bulletin n'existe pas
     */
    @Transactional
    public PrintHistoryResponse downloadReportCard(Integer reportCardId) {
        return logPrintOrDownload(reportCardId, PrintAction.DOWNLOADED);
    }

    /**
     * Logique commune à l'impression et au téléchargement : recherche la ligne existante
     * pour ce bulletin et cette action, l'incrémente si trouvée, sinon en crée une nouvelle.
     * performedBy reste null tant que l'authentification n'est pas en place ;
     * à remplir plus tard avec l'utilisateur courant à chaque appel (écrase la valeur précédente).
     *
     * @param reportCardId l'identifiant du bulletin concerné
     * @param action PRINTED ou DOWNLOADED
     * @return le DTO de la ligne d'historique mise à jour ou créée
     * @throws EntityNotFoundException si le bulletin n'existe pas
     */
    private PrintHistoryResponse logPrintOrDownload(Integer reportCardId, PrintAction action) {
        ReportCard reportCard = reportCardRepository.findById(reportCardId)
                .orElseThrow(() -> new EntityNotFoundException("ReportCard not found with id: " + reportCardId));

        PrintHistory printHistory = printHistoryRepository
                .findByReportCardIdAndAction(reportCardId, action)
                .orElseGet(() -> PrintHistory.builder()
                        .action(action)
                        .reportCard(reportCard)
                        .classroom(reportCard.getClassroom())
                        .trimester(reportCard.getTrimester())
                        .count(0)
                        .build());

        printHistory.setCount(printHistory.getCount() + 1);
        printHistory.setPerformedBy(null); // à remplir avec l'utilisateur courant une fois l'auth en place

        PrintHistory saved = printHistoryRepository.save(printHistory);
        return printHistoryMapper.toDTO(saved);
    }

    /**
     * Récupère tout l'historique (générations + impressions + téléchargements).
     *
     * @return la liste des DTOs de tout l'historique
     */
    @Transactional()
    public List<PrintHistoryResponse> getAll() {
        return printHistoryMapper.toDTOList(printHistoryRepository.findAll());
    }

    /**
     * Récupère l'historique complet d'une classe pour un trimestre donné.
     *
     * @param classroomId l'identifiant de la classe
     * @param trimester le trimestre concerné
     * @return la liste des DTOs des entrées correspondantes
     */
    @Transactional()
    public List<PrintHistoryResponse> getByClassroomAndTrimester(Integer classroomId, Trimester trimester) {
        return printHistoryMapper.toDTOList(
                printHistoryRepository.findByClassroomIdAndTrimester(classroomId, trimester));
    }

    /**
     * Récupère l'historique sur une plage de dates.
     *
     * @param start date/heure de début (incluse)
     * @param end date/heure de fin (incluse)
     * @return la liste des DTOs des entrées dans cette plage
     */
    @Transactional()
    public List<PrintHistoryResponse> getByDateRange(Timestamp start, Timestamp end) {
        return printHistoryMapper.toDTOList(printHistoryRepository.findByPerformedAtBetween(start, end));
    }

    /**
     * Calcule le total cumulé (somme des count) d'une action pour une classe/trimestre donnés.
     * Ex: nombre total d'impressions de tous les bulletins d'une classe, toutes répétitions incluses.
     *
     * @param classroomId l'identifiant de la classe
     * @param trimester le trimestre concerné
     * @param action l'action concernée (typiquement PRINTED ou DOWNLOADED)
     * @return la somme des count pour cette classe/trimestre/action
     */
    @Transactional()
    public int getTotalByClassroomTrimesterAndAction(Integer classroomId, Trimester trimester, PrintAction action) {
        return printHistoryRepository
                .findByClassroomIdAndTrimesterAndAction(classroomId, trimester, action).stream()
                .mapToInt(PrintHistory::getCount)
                .sum();
    }
}
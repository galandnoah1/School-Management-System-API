package com.truhoster.school_management_system.reportcard.service;

import com.truhoster.school_management_system.note.entity.NoteLine;
import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.note.repository.NoteLineRepository;
import com.truhoster.school_management_system.printhistory.service.PrintHistoryService;
import com.truhoster.school_management_system.reportcard.dto.ReportCardResponse;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import com.truhoster.school_management_system.reportcard.mapper.ReportCardMapper;
import com.truhoster.school_management_system.reportcard.repository.ReportCardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportCardService {

    private final ReportCardRepository reportCardRepository;
    private final NoteLineRepository noteLineRepository;
    private final ReportCardMapper reportCardMapper;
    private final PrintHistoryService printHistoryService;

    /**
     * Génère les bulletins de toute une classe pour un trimestre donné.
     * Étape 1 : pour chaque élève, calcule average1 (moyenne du 1er CC du trimestre),
     * average2 (idem pour le 2e CC), average (moyenne des deux) et l'appréciation.
     * Étape 2 : classe les élèves par average décroissante, attribue un rang distinct à chacun
     * (pas de gestion d'ex aquo, ordre stable en cas d'égalité).
     * Étape 3 : calcule les statistiques de classe (moyenne de classe, moyenne du 1er, moyenne
     * du dernier) et les répercute sur le bulletin de CHAQUE élève de la classe.
     *
     * @param classroomId l'identifiant de la classe
     * @param trimester le trimestre concerné
     * @return la liste des DTOs des bulletins générés, triés par rang
     * @throws IllegalStateException si aucun bulletin n'existe pour cette classe/trimestre
     *         (signifie qu'aucune note n'a encore été saisie)
     */
    @Transactional
    public List<ReportCardResponse> generate(Integer classroomId, Trimester trimester) {
        List<ReportCard> reportCards = reportCardRepository.findByClassroomIdAndTrimester(classroomId, trimester);

        if (reportCards.isEmpty()) {
            throw new IllegalStateException(
                    "Aucun bulletin trouvé pour cette classe et ce trimestre. Vérifiez que des notes ont été saisies.");
        }

        // Étape 1 : moyennes individuelles
        for (ReportCard reportCard : reportCards) {
            List<NoteLine> noteLines = noteLineRepository.findByReportCardId(reportCard.getId());
            computeIndividualAverages(reportCard, noteLines);
        }

        // Étape 2 : classement (rangs distincts, ordre stable en cas d'égalité)
        List<ReportCard> ranked = reportCards.stream()
                .sorted(Comparator.comparing(ReportCard::getAverage, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setRanking((i + 1));
        }

        // Étape 3 : statistiques de classe, répercutées sur tous les bulletins
        double classAverage = reportCards.stream()
                .mapToDouble(ReportCard::getAverage)
                .average()
                .orElse(0.0);

        double firstAverage = ranked.get(0).getAverage();
        double lastAverage = ranked.get(ranked.size() - 1).getAverage();

        for (ReportCard reportCard : reportCards) {
            reportCard.setOverallaverage(classAverage);
            reportCard.setFirstaverage(firstAverage);
            reportCard.setLastaverage(lastAverage);
        }

        List<ReportCard> saved = reportCardRepository.saveAll(ranked);

        printHistoryService.logGeneration(saved.get(0).getClassroom(), trimester, saved.size());

        return reportCardMapper.toDTOList(saved);
    }

    /**
     * Récupère un bulletin par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO du bulletin trouvé
     * @throws EntityNotFoundException si aucun bulletin ne correspond à l'id
     */
    @Transactional()
    public ReportCardResponse getById(Integer id) {
        ReportCard reportCard = reportCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ReportCard not found with id: " + id));
        return reportCardMapper.toDTO(reportCard);
    }

    /**
     * Récupère le bulletin d'un élève pour un trimestre donné.
     *
     * @param studentId l'identifiant de l'élève
     * @param trimester le trimestre concerné
     * @return le DTO du bulletin trouvé
     * @throws EntityNotFoundException si aucun bulletin n'existe pour cet élève à ce trimestre
     */
    @Transactional()
    public ReportCardResponse getByStudentAndTrimester(Integer studentId, Trimester trimester) {
        ReportCard reportCard = reportCardRepository.findByStudentIdAndTrimester(studentId, trimester)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun bulletin trouvé pour l'élève id=" + studentId + " au trimestre " + trimester));
        return reportCardMapper.toDTO(reportCard);
    }

    /**
     * Calcule average1, average2, average et appreciation pour un bulletin,
     * à partir de ses lignes de matière (NoteLine).
     * average1 = somme(note1 x coefficient) / somme(coefficient), sur les lignes où note1 existe.
     * average2 = idem pour note2. average = (average1 + average2) / 2.
     *
     * @param reportCard le bulletin à mettre à jour
     * @param noteLines les lignes de matière de ce bulletin
     */
    private void computeIndividualAverages(ReportCard reportCard, List<NoteLine> noteLines) {
        double sumCoef1 = 0;
        double sumNoteCoef1 = 0;
        double sumCoef2 = 0;
        double sumNoteCoef2 = 0;

        for (NoteLine line : noteLines) {
            if (line.getNote1() != null) {
                sumNoteCoef1 += line.getNote1() * line.getCoefficient();
                sumCoef1 += line.getCoefficient();
            }
            if (line.getNote2() != null) {
                sumNoteCoef2 += line.getNote2() * line.getCoefficient();
                sumCoef2 += line.getCoefficient();
            }
        }

        double average1 = sumCoef1 == 0 ? 0.0 : sumNoteCoef1 / sumCoef1;
        double average2 = sumCoef2 == 0 ? 0.0 : sumNoteCoef2 / sumCoef2;
        double average = (average1 + average2) / 2;

        reportCard.setAverage1(average1);
        reportCard.setAverage2(average2);
        reportCard.setAverage(average);
        reportCard.setAppreciation(resolveAppreciation(average));
    }

    /**
     * Détermine l'appréciation textuelle en fonction de la moyenne trimestrielle.
     * &lt;10 = Non acquis, &lt;14 = En cours d'acquisition, &lt;17 = Acquis, &lt;=20 = Excellent.
     *
     * @param average la moyenne trimestrielle de l'élève
     * @return l'appréciation correspondante
     */
    private String resolveAppreciation(double average) {
        if (average < 10) {
            return "Non acquis";
        } else if (average < 14) {
            return "En cours d'acquisition";
        } else if (average < 17) {
            return "Acquis";
        } else {
            return "Excellent";
        }
    }
}
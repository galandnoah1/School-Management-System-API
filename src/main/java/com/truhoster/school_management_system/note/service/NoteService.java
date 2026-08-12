package com.truhoster.school_management_system.note.service;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.note.dto.NoteRequest;
import com.truhoster.school_management_system.note.dto.NoteResponse;
import com.truhoster.school_management_system.note.entity.BulkNoteRequest;
import com.truhoster.school_management_system.note.entity.Note;
import com.truhoster.school_management_system.note.entity.NoteLine;
import com.truhoster.school_management_system.note.entity.StudentNoteEntry;
import com.truhoster.school_management_system.note.enums.Evaluation;
import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.note.mapper.NoteMapper;
import com.truhoster.school_management_system.note.repository.NoteLineRepository;
import com.truhoster.school_management_system.note.repository.NoteRepository;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import com.truhoster.school_management_system.reportcard.repository.ReportCardRepository;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.student.repository.StudentRepository;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.entity.SubjectByClassroom;
import com.truhoster.school_management_system.subject.repository.SubjectByClassroomRepository;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import com.truhoster.school_management_system.teacher.entity.Affectation;
import com.truhoster.school_management_system.teacher.repository.AffectationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final ReportCardRepository reportCardRepository;
    private final NoteLineRepository noteLineRepository;
    private final AffectationRepository affectationRepository;
    private final SubjectByClassroomRepository subjectByClassroomRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ClassroomRepository classroomRepository;

    /**
     * Enregistre une nouvelle note pour un élève.
     * Déclenche en cascade : recherche/création du ReportCard (student + classroom + trimestre
     * déduit de l'évaluation), puis recherche/création/mise à jour de la NoteLine correspondante
     * (note1 pour CC1/CC3/CC5, note2 pour CC2/CC4/CC6), avec recalcul de la moyenne.
     *
     * @param request les données de la note à enregistrer
     * @return le DTO de la note créée
     */
    @Transactional
    public NoteResponse create(NoteRequest request) {
        Note note = noteMapper.toEntity(request);
        Note savedNote = noteRepository.save(note);

        applyNoteToReportCard(savedNote);

        return noteMapper.toDTO(savedNote);
    }

    /**
     * Met à jour la note d'un élève (valeur et/ou évaluation).
     * Répercute le changement sur la NoteLine associée : si l'évaluation change de trimestre
     * ou de position (note1/note2), l'ancienne valeur est effacée de son ancienne NoteLine
     * et la nouvelle valeur appliquée à la NoteLine correcte.
     *
     * @param id l'identifiant de la note à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de la note mise à jour
     * @throws EntityNotFoundException si aucune note ne correspond à l'id
     */
    @Transactional
    public NoteResponse update(Integer id, NoteRequest request) {
        Note existing = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));

        boolean evaluationChanged = existing.getEvaluation() != request.getEvaluation();
        Evaluation oldEvaluation = existing.getEvaluation();
        Student oldStudent = existing.getStudent();
        Classroom oldClassroom = existing.getClassroom();
        Subject oldSubject = existing.getSubject();

        // Si l'évaluation change, on efface l'ancienne valeur de son ancienne NoteLine
        if (evaluationChanged) {
            clearNoteFromLine(oldStudent, oldClassroom, oldSubject, oldEvaluation);
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with id: " + request.getStudentId()));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found with id: " + request.getSubjectId()));
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Classroom not found with id: " + request.getClassroomId()));

        existing.setNote(request.getNote());
        existing.setEvaluation(request.getEvaluation());
        existing.setStudent(student);
        existing.setSubject(subject);
        existing.setClassroom(classroom);

        Note saved = noteRepository.save(existing);
        applyNoteToReportCard(saved);

        return noteMapper.toDTO(saved);
    }

    /**
     * Récupère une note par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO de la note trouvée
     * @throws EntityNotFoundException si aucune note ne correspond à l'id
     */
    @Transactional()
    public NoteResponse getById(Integer id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));
        return noteMapper.toDTO(note);
    }

    /**
     * Récupère la liste de toutes les notes.
     *
     * @return la liste des DTOs de toutes les notes
     */
    @Transactional()
    public List<NoteResponse> getAll() {
        return noteMapper.toDTOList(noteRepository.findAll());
    }

    /**
     * Récupère les notes d'une classe, pour une matière et une évaluation données.
     *
     * @param classroomId l'identifiant de la classe
     * @param subjectId l'identifiant de la matière
     * @param evaluation le type d'évaluation recherché
     * @return la liste des DTOs des notes correspondantes
     */
    @Transactional()
    public List<NoteResponse> getByClassroomSubjectEvaluation(
            Integer classroomId, Integer subjectId, Evaluation evaluation) {
        List<Note> notes = noteRepository.findByClassroomIdAndSubjectIdAndEvaluation(
                classroomId, subjectId, evaluation);
        return noteMapper.toDTOList(notes);
    }

    /**
     * Supprime une note par son id.
     * Répercute la suppression sur la NoteLine associée : efface note1 ou note2
     * (selon la position de l'évaluation) et recalcule la moyenne. La NoteLine elle-même
     * n'est pas supprimée (elle peut encore contenir l'autre note du trimestre).
     *
     * @param id l'identifiant de la note à supprimer
     * @throws EntityNotFoundException si aucune note ne correspond à l'id
     */
    @Transactional
    public void delete(Integer id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));

        clearNoteFromLine(note.getStudent(), note.getClassroom(), note.getSubject(), note.getEvaluation());

        noteRepository.delete(note);
    }

    /**
     * Applique une note enregistrée à la NoteLine correspondante :
     * recherche/crée le ReportCard (student + classroom + trimestre déduit de l'évaluation),
     * recherche/crée la NoteLine (reportCard + nom de matière), positionne la valeur
     * dans note1 ou note2 selon l'évaluation, puis recalcule la moyenne.
     *
     * @param note la note fraîchement enregistrée
     */
    private void applyNoteToReportCard(Note note) {
        Trimester trimester = resolveTrimester(note.getEvaluation());
        boolean isFirstOfTrimester = isFirstEvaluationOfTrimester(note.getEvaluation());

        ReportCard reportCard = findOrCreateReportCard(note.getStudent(), note.getClassroom(), trimester);

        NoteLine noteLine = noteLineRepository
                .findByReportCardIdAndSubject(reportCard.getId(), note.getSubject().getName())
                .orElseGet(() -> createNoteLine(reportCard, note.getClassroom(), note.getSubject(), trimester));

        if (isFirstOfTrimester) {
            noteLine.setNote1(note.getNote());
        } else {
            noteLine.setNote2(note.getNote());
        }

        recalcAverage(noteLine);
        noteLineRepository.save(noteLine);
    }

    /**
     * Efface la valeur d'une note (note1 ou note2) de la NoteLine correspondante,
     * typiquement lors d'une suppression ou d'un changement d'évaluation.
     * Ne fait rien si le ReportCard ou la NoteLine n'existent pas (rien à effacer).
     *
     * @param student l'élève concerné
     * @param classroom la classe concernée
     * @param subject la matière concernée
     * @param evaluation l'évaluation dont la valeur doit être effacée
     */
    private void clearNoteFromLine(Student student, Classroom classroom, Subject subject, Evaluation evaluation) {
        Trimester trimester = resolveTrimester(evaluation);
        boolean isFirstOfTrimester = isFirstEvaluationOfTrimester(evaluation);

        reportCardRepository.findByStudentIdAndClassroomIdAndTrimester(
                        student.getId(), classroom.getId(), trimester)
                .flatMap(reportCard -> noteLineRepository.findByReportCardIdAndSubject(
                        reportCard.getId(), subject.getName()))
                .ifPresent(noteLine -> {
                    if (isFirstOfTrimester) {
                        noteLine.setNote1(null);
                    } else {
                        noteLine.setNote2(null);
                    }
                    recalcAverage(noteLine);
                    noteLineRepository.save(noteLine);
                });
    }

    /**
     * Recherche le ReportCard d'un élève pour une classe et un trimestre donnés,
     * ou le crée s'il n'existe pas encore.
     *
     * @param student l'élève concerné
     * @param classroom la classe concernée
     * @param trimester le trimestre concerné
     * @return le ReportCard existant ou nouvellement créé
     */
    private ReportCard findOrCreateReportCard(Student student, Classroom classroom, Trimester trimester) {
        return reportCardRepository
                .findByStudentIdAndClassroomIdAndTrimester(student.getId(), classroom.getId(), trimester)
                .orElseGet(() -> reportCardRepository.save(
                        ReportCard.builder()
                                .student(student)
                                .classroom(classroom)
                                .trimester(trimester)
                                .build()));
    }

    /**
     * Crée une nouvelle NoteLine pour une matière dans un bulletin donné.
     * Récupère le nom du professeur (via Affectation) et le coefficient (via SubjectByClassroom)
     * pour la classe et la matière concernées.
     *
     * @param reportCard le bulletin auquel rattacher la ligne
     * @param classroom la classe concernée (pour retrouver prof et coefficient)
     * @param subject la matière concernée
     * @param trimester le trimestre concerné
     * @return la NoteLine créée et persistée
     * @throws EntityNotFoundException si aucune affectation ou coefficient n'est défini
     *         pour ce couple classe/matière
     */
    private NoteLine createNoteLine(ReportCard reportCard, Classroom classroom, Subject subject, Trimester trimester) {
       Affectation affectation = affectationRepository
                .findByClassroomIdAndSubjectId(classroom.getId(), subject.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun professeur affecté à cette matière pour cette classe"));

        SubjectByClassroom subjectByClassroom = subjectByClassroomRepository
                .findByClassroomIdAndSubjectId(classroom.getId(), subject.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun coefficient défini pour cette matière dans cette classe"));

        NoteLine noteLine = NoteLine.builder()
                .subject(subject.getName())
                .teacher(affectation.getTeacher().getName())
                .coefficient(subjectByClassroom.getCoefficient())
                .trimester(trimester)
                .reportCard(reportCard)
                .build();

        return noteLineRepository.save(noteLine);
    }

    /**
     * Recalcule la moyenne (average) et la note pondérée (notecoefficie) d'une NoteLine
     * à partir des notes déjà renseignées (note1 et/ou note2).
     * Si aucune note n'est renseignée, les deux valeurs sont remises à 0.
     *
     * @param noteLine la ligne dont il faut recalculer les moyennes
     */
    private void recalcAverage(NoteLine noteLine) {
        List<Double> grades = Stream.of(noteLine.getNote1(), noteLine.getNote2())
                .filter(Objects::nonNull)
                .toList();

        double average = grades.isEmpty()
                ? 0.0
                : grades.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        noteLine.setAverage(average);
        noteLine.setNotecoefficie(average * noteLine.getCoefficient());
    }

    /**
     * Détermine le trimestre correspondant à une évaluation.
     * CC1/CC2 -> T1, CC3/CC4 -> T2, CC5/CC6 -> T3.
     *
     * @param evaluation l'évaluation concernée
     * @return le trimestre correspondant
     */
    private Trimester resolveTrimester(Evaluation evaluation) {
        return switch (evaluation) {
            case CC1, CC2 -> Trimester.T1;
            case CC3, CC4 -> Trimester.T2;
            case CC5, CC6 -> Trimester.T3;
        };
    }

    /**
     * Détermine si une évaluation correspond au premier ou au second contrôle continu
     * du trimestre (CC1/CC3/CC5 = premier -> note1, CC2/CC4/CC6 = second -> note2).
     *
     * @param evaluation l'évaluation concernée
     * @return true si c'est le premier CC du trimestre, false si c'est le second
     */
    private boolean isFirstEvaluationOfTrimester(Evaluation evaluation) {
        return evaluation == Evaluation.CC1 || evaluation == Evaluation.CC3 || evaluation == Evaluation.CC5;
    }

    /**
     * Enregistre plusieurs notes en une fois pour une même matière, évaluation et classe,
     * chaque entrée précisant l'élève concerné et sa note.
     * Subject et Classroom sont résolus une seule fois (pas à chaque itération) pour l'efficacité.
     * Chaque note déclenche la même logique que create() (répercussion sur ReportCard/NoteLine).
     * Si un élève de la liste n'existe pas, une EntityNotFoundException interrompt tout le batch
     * (aucune note n'est partiellement enregistrée grâce à @Transactional).
     *
     * @param request les données communes (matière, évaluation, classe) + la liste des notes par élève
     * @return la liste des DTOs des notes créées
     * @throws EntityNotFoundException si la matière, la classe, ou un des élèves n'existe pas
     */
    @Transactional
    public List<NoteResponse> createBulk(BulkNoteRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found with id: " + request.getSubjectId()));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Classroom not found with id: " + request.getClassroomId()));

        List<NoteResponse> responses = new ArrayList<>();

        for (StudentNoteEntry entry : request.getNotes()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Student not found with id: " + entry.getStudentId()));

            Note note = Note.builder()
                    .note(entry.getNote())
                    .evaluation(request.getEvaluation())
                    .student(student)
                    .subject(subject)
                    .classroom(classroom)
                    .build();

            Note saved = noteRepository.save(note);
            applyNoteToReportCard(saved);

            responses.add(noteMapper.toDTO(saved));
        }

        return responses;
    }
}
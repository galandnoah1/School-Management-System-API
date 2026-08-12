package com.truhoster.school_management_system.teacher.service;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import com.truhoster.school_management_system.teacher.dto.TeacherRequest;
import com.truhoster.school_management_system.teacher.dto.TeacherResponse;
import com.truhoster.school_management_system.teacher.entity.Affectation;
import com.truhoster.school_management_system.teacher.entity.Teacher;
import com.truhoster.school_management_system.teacher.mapper.AffectationMapper;
import com.truhoster.school_management_system.teacher.mapper.TeacherMapper;
import com.truhoster.school_management_system.teacher.repository.AffectationRepository;
import com.truhoster.school_management_system.teacher.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final AffectationRepository affectationRepository;
    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Crée un nouveau professeur ainsi que sa première affectation
     * (classe + matière) à partir des ids fournis dans la requête.
     *
     * @param request les données du professeur (infos + classroomId + subjectId)
     * @return le DTO du professeur créé, avec son affectation initiale
     */
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        Teacher teacher = teacherMapper.toEntity(request);
        Teacher savedTeacher = teacherRepository.save(teacher);

        createAffectation(savedTeacher, request.getClassroomId(), request.getSubjectId());

        Teacher reloaded = teacherRepository.findByIdWithAffectations(savedTeacher.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Teacher not found with id: " + savedTeacher.getId()));
        return teacherMapper.toDTO(reloaded);
    }

    /**
     * Met à jour les informations d'un professeur existant.
     * Si classroomId/subjectId sont fournis, une nouvelle affectation est créée pour ce couple.
     * Si cette affectation existe déjà, l'opération est refusée (409).
     *
     * @param id l'identifiant du professeur à mettre à jour
     * @param request les nouvelles données
     * @return le DTO du professeur mis à jour
     * @throws EntityNotFoundException si aucun professeur ne correspond à l'id
     * @throws IllegalStateException si l'affectation (teacher/classroom/subject) existe déjà
     */
    @Transactional
    public TeacherResponse update(Integer id, TeacherRequest request) {
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));

        existing.setName(request.getName());
        existing.setPhone(request.getPhone());
        existing.setSex(request.getSex());
        teacherRepository.save(existing);

        createAffectation(existing, request.getClassroomId(), request.getSubjectId());

        Teacher reloaded = teacherRepository.findByIdWithAffectations(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
        return teacherMapper.toDTO(reloaded);
    }

    /**
     * Récupère un professeur par son id, avec ses affectations chargées.
     *
     * @param id l'identifiant recherché
     * @return le DTO du professeur trouvé
     * @throws EntityNotFoundException si aucun professeur ne correspond à l'id
     */
    @Transactional()
    public TeacherResponse getById(Integer id) {
        Teacher teacher = teacherRepository.findByIdWithAffectations(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
        return teacherMapper.toDTO(teacher);
    }

    /**
     * Récupère la liste de tous les professeurs, avec leurs affectations chargées.
     *
     * @return la liste des DTOs de tous les professeurs
     */
    @Transactional()
    public List<TeacherResponse> getAll() {
        return teacherMapper.toDTOList(teacherRepository.findAllWithAffectations());
    }

    /**
     * Récupère tous les enseignants intervenant dans une classe donnée,
     * en passant par les affectations liées à cette classe.
     * Un professeur affecté plusieurs fois à la même classe (matières différentes)
     * n'apparaît qu'une seule fois dans le résultat (déduplication par id).
     *
     * @param classroomId l'identifiant de la classe
     * @return la liste des DTOs des professeurs de cette classe
     */
    @Transactional()
    public List<TeacherResponse> getByClassroom(Integer classroomId) {
        List<Teacher> teachers = affectationRepository.findByClassroomId(classroomId).stream()
                .map(Affectation::getTeacher)
                .distinct()
                .collect(Collectors.toList());
        return teacherMapper.toDTOList(teachers);
    }

    /**
     * Supprime un professeur par son id.
     * Grâce à cascade = ALL + orphanRemoval sur Teacher.affectations,
     * toutes les affectations liées à ce professeur sont supprimées automatiquement.
     * L'entité est chargée avant suppression pour garantir le déclenchement de la cascade.
     *
     * @param id l'identifiant du professeur à supprimer
     * @throws EntityNotFoundException si aucun professeur ne correspond à l'id
     */
    @Transactional
    public void delete(Integer id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
        teacherRepository.delete(teacher);
    }

    /**
     * Crée une affectation pour le professeur donné.
     * Ne fait rien si classroomId ou subjectId est null (pas d'affectation demandée).
     *
     * @param teacher le professeur concerné
     * @param classroomId l'identifiant de la classe
     * @param subjectId l'identifiant de la matière
     * @throws EntityNotFoundException si la classe ou la matière n'existe pas
     * @throws IllegalStateException si l'affectation existe déjà pour ce trio teacher/classroom/subject
     */
    private void createAffectation(Teacher teacher, Integer classroomId, Integer subjectId) {
        if (classroomId == null || subjectId == null) {
            return;
        }

        boolean alreadyExists = affectationRepository.existsByTeacherIdAndClassroomIdAndSubjectId(
                teacher.getId(), classroomId, subjectId);
        if (alreadyExists) {
            throw new IllegalStateException(
                    "Ce professeur est déjà affecté à cette classe pour cette matière.");
        }

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found with id: " + classroomId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + subjectId));

        Affectation affectation = Affectation.builder()
                .teacher(teacher)
                .classroom(classroom)
                .subject(subject)
                .build();
        affectationRepository.save(affectation);
    }
}
package com.truhoster.school_management_system.subject.service;

import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.subject.dto.SubjectByClassroomRequest;
import com.truhoster.school_management_system.subject.dto.SubjectByClassroomResponse;
import com.truhoster.school_management_system.subject.dto.SubjectRequest;
import com.truhoster.school_management_system.subject.dto.SubjectResponse;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.entity.SubjectByClassroom;
import com.truhoster.school_management_system.subject.mapper.SubjectByClassroomMapper;
import com.truhoster.school_management_system.subject.mapper.SubjectMapper;
import com.truhoster.school_management_system.subject.repository.SubjectByClassroomRepository;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final SubjectByClassroomRepository subjectByClassroomRepository;
    private final SubjectByClassroomMapper subjectByClassroomMapper;

    /**
     * Crée une nouvelle matière.
     *
     * @param request les données de la matière à créer
     * @return le DTO de la matière créée
     */
    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        Subject subject = subjectMapper.toEntity(request);
        Subject saved = subjectRepository.save(subject);
        return subjectMapper.toDTO(saved);
    }

    /**
     * Met à jour une matière existante.
     *
     * @param id l'identifiant de la matière à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de la matière mise à jour
     * @throws EntityNotFoundException si aucune matière ne correspond à l'id
     */
    @Transactional
    public SubjectResponse update(Integer id, SubjectRequest request) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        existing.setName(request.getName());
        existing.setSection(request.getSection());
        existing.setSubjectGroup(request.getSubjectGroup());

        Subject saved = subjectRepository.save(existing);
        return subjectMapper.toDTO(saved);
    }

    /**
     * Récupère une matière par son id, avec la liste de ses classes associées chargée.
     * Transactionnel (lecture seule) pour garder la session ouverte le temps du mapping,
     * même si findByIdWithClassrooms charge déjà tout en une requête (sécurité supplémentaire).
     *
     * @param id l'identifiant recherché
     * @return le DTO de la matière trouvée
     * @throws EntityNotFoundException si aucune matière ne correspond à l'id
     */
    @Transactional()
    public SubjectResponse getById(Integer id) {
        Subject subject = subjectRepository.findByIdWithClassrooms(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
        return subjectMapper.toDTO(subject);
    }

    /**
     * Récupère la liste de toutes les matières, avec leurs classes associées chargées.
     *
     * @return la liste des DTOs de toutes les matières
     */
    @Transactional()
    public List<SubjectResponse> getAll() {
        return subjectMapper.toDTOList(subjectRepository.findAllWithClassrooms());
    }

    /**
     * Récupère toutes les matières d'une section donnée, avec leurs classes associées chargées.
     *
     * @param section la section recherchée
     * @return la liste des DTOs des matières de cette section
     */
    @Transactional()
    public List<SubjectResponse> getBySection(Section section) {
        return subjectMapper.toDTOList(subjectRepository.findBySectionWithClassrooms(section));
    }

    /**
     * Supprime une matière par son id.
     *
     * @param id l'identifiant de la matière à supprimer
     * @throws EntityNotFoundException si aucune matière ne correspond à l'id
     */
    public void delete(Integer id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    /**
     * Associe une matière à une classe en définissant son coefficient (création d'un SubjectByClassroom).
     * Vérifie qu'aucune association n'existe déjà pour ce couple classe/matière.
     *
     * @param request les données (coefficient, classroomId, subjectId)
     * @return le DTO de l'association créée
     * @throws IllegalStateException si l'association existe déjà pour ce couple classe/matière
     */
    @Transactional
    public SubjectByClassroomResponse setCoefficient(SubjectByClassroomRequest request) {
        boolean alreadyExists = subjectByClassroomRepository.existsByClassroomIdAndSubjectId(
                request.getClassroomId(), request.getSubjectId());

        if (alreadyExists) {
            throw new IllegalStateException(
                    "Cette matière est déjà associée à cette classe. Utilisez updateCoefficient pour la modifier.");
        }

        SubjectByClassroom subjectByClassroom = subjectByClassroomMapper.toEntity(request);
        SubjectByClassroom saved = subjectByClassroomRepository.save(subjectByClassroom);
        return subjectByClassroomMapper.toDTO(saved);
    }

    /**
     * Met à jour le coefficient d'une association matière/classe existante.
     *
     * @param id l'identifiant de l'association SubjectByClassroom
     * @param coefficient le nouveau coefficient
     * @return le DTO de l'association mise à jour
     * @throws EntityNotFoundException si aucune association ne correspond à l'id
     */
    @Transactional
    public SubjectByClassroomResponse updateCoefficient(Integer id, int coefficient) {
        SubjectByClassroom existing = subjectByClassroomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "SubjectByClassroom not found with id: " + id));

        existing.setCoefficient(coefficient);
        SubjectByClassroom saved = subjectByClassroomRepository.save(existing);
        return subjectByClassroomMapper.toDTO(saved);
    }
}
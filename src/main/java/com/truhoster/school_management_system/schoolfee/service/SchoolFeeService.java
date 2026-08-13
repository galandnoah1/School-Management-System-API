package com.truhoster.school_management_system.schoolfee.service;
import com.truhoster.school_management_system.schoolfee.dto.SchoolFeeRequest;
import com.truhoster.school_management_system.schoolfee.dto.SchoolFeeResponse;
import com.truhoster.school_management_system.schoolfee.entity.SchoolFee;
import com.truhoster.school_management_system.schoolfee.mapper.SchoolFeeMapper;
import com.truhoster.school_management_system.schoolfee.repository.SchoolFeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolFeeService {

    private final SchoolFeeRepository schoolFeeRepository;
    private final SchoolFeeMapper schoolFeeMapper;

    /**
     * Crée une nouvelle grille de frais.
     * Vérifie qu'aucune des classes sélectionnées n'appartient déjà à une autre grille.
     *
     * @param request les données de la grille à créer
     * @return le DTO de la grille créée
     * @throws IllegalStateException si une classe est déjà assignée à une grille existante
     */
    @Transactional
    public SchoolFeeResponse create(SchoolFeeRequest request) {
        checkClassroomsNotAlreadyAssigned(request.getClassroomIds(), null);

        SchoolFee schoolFee = schoolFeeMapper.toEntity(request);
        SchoolFee saved = schoolFeeRepository.save(schoolFee);
        return schoolFeeMapper.toDTO(saved);
    }

    /**
     * Met à jour une grille de frais existante.
     * Vérifie qu'aucune des nouvelles classes sélectionnées n'appartient déjà
     * à une AUTRE grille (celle en cours de modification est exclue de la vérification).
     *
     * @param id l'identifiant de la grille à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de la grille mise à jour
     * @throws EntityNotFoundException si aucune grille ne correspond à l'id
     * @throws IllegalStateException si une classe est déjà assignée à une autre grille
     */
    @Transactional
    public SchoolFeeResponse update(Integer id, SchoolFeeRequest request) {
        SchoolFee existing = schoolFeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SchoolFee not found with id: " + id));

        checkClassroomsNotAlreadyAssigned(request.getClassroomIds(), id);

        SchoolFee updatedData = schoolFeeMapper.toEntity(request);

        existing.setLabel(updatedData.getLabel());
        existing.setInscriptionAmount(updatedData.getInscriptionAmount());
        existing.setTranche1Amount(updatedData.getTranche1Amount());
        existing.setTranche1Deadline(updatedData.getTranche1Deadline());
        existing.setTranche2Amount(updatedData.getTranche2Amount());
        existing.setTranche2Deadline(updatedData.getTranche2Deadline());
        existing.setClassrooms(updatedData.getClassrooms());

        SchoolFee saved = schoolFeeRepository.save(existing);
        return schoolFeeMapper.toDTO(saved);
    }

    /**
     * Récupère une grille de frais par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO de la grille trouvée
     * @throws EntityNotFoundException si aucune grille ne correspond à l'id
     */
    @Transactional()
    public SchoolFeeResponse getById(Integer id) {
        SchoolFee schoolFee = schoolFeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SchoolFee not found with id: " + id));
        return schoolFeeMapper.toDTO(schoolFee);
    }

    /**
     * Récupère la liste de toutes les grilles de frais.
     *
     * @return la liste des DTOs de toutes les grilles
     */
    @Transactional()
    public List<SchoolFeeResponse> getAll() {
        return schoolFeeMapper.toDTOList(schoolFeeRepository.findAll());
    }

    /**
     * Récupère la grille de frais applicable à une classe donnée.
     *
     * @param classroomId l'identifiant de la classe
     * @return le DTO de la grille trouvée
     * @throws EntityNotFoundException si aucune grille n'est définie pour cette classe
     */
    @Transactional()
    public SchoolFeeResponse getByClassroom(Integer classroomId) {
        SchoolFee schoolFee = schoolFeeRepository.findByClassroomId(classroomId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune grille de frais définie pour la classe id: " + classroomId));
        return schoolFeeMapper.toDTO(schoolFee);
    }

    /**
     * Supprime une grille de frais par son id.
     *
     * @param id l'identifiant de la grille à supprimer
     * @throws EntityNotFoundException si aucune grille ne correspond à l'id
     */
    public void delete(Integer id) {
        if (!schoolFeeRepository.existsById(id)) {
            throw new EntityNotFoundException("SchoolFee not found with id: " + id);
        }
        schoolFeeRepository.deleteById(id);
    }

    /**
     * Vérifie qu'aucune des classes données n'est déjà assignée à une autre grille de frais.
     *
     * @param classroomIds les identifiants des classes à vérifier
     * @param currentSchoolFeeId l'id de la grille en cours de modification (null en création,
     *                            pour exclure la grille elle-même de la vérification)
     * @throws IllegalStateException si une classe est déjà assignée à une autre grille
     */
    private void checkClassroomsNotAlreadyAssigned(List<Integer> classroomIds, Integer currentSchoolFeeId) {
        for (Integer classroomId : classroomIds) {
            schoolFeeRepository.findByClassroomId(classroomId).ifPresent(existingFee -> {
                if (!existingFee.getId().equals(currentSchoolFeeId)) {
                    throw new IllegalStateException(
                            "La classe id=" + classroomId + " est déjà assignée à une autre grille de frais (id="
                                    + existingFee.getId() + ")");
                }
            });
        }
    }
}
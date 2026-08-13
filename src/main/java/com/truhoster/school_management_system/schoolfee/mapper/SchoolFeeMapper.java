package com.truhoster.school_management_system.schoolfee.mapper;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.schoolfee.dto.SchoolFeeRequest;
import com.truhoster.school_management_system.schoolfee.dto.SchoolFeeResponse;
import com.truhoster.school_management_system.schoolfee.entity.SchoolFee;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SchoolFeeMapper {

    private final ClassroomRepository classroomRepository;

    /**
     * Convertit un SchoolFeeRequest en entité SchoolFee.
     * Résout la liste des Classroom à partir des ids fournis.
     * Ne vérifie PAS la contrainte d'unicité (classe déjà assignée ailleurs) —
     * cette vérification métier est faite dans SchoolFeeService.
     *
     * @param request les données envoyées par le client
     * @return l'entité SchoolFee prête à être persistée
     * @throws EntityNotFoundException si une des classes n'existe pas
     */
    public SchoolFee toEntity(SchoolFeeRequest request) {
        if (request == null) {
            return null;
        }

        List<Classroom> classrooms = classroomRepository.findAllById(request.getClassroomIds());
        if (classrooms.size() != request.getClassroomIds().size()) {
            throw new EntityNotFoundException("Une ou plusieurs classes spécifiées n'existent pas");
        }

        return SchoolFee.builder()
                .label(request.getLabel())
                .inscriptionAmount(request.getInscriptionAmount())
                .tranche1Amount(request.getTranche1Amount())
                .tranche1Deadline(request.getTranche1Deadline())
                .tranche2Amount(request.getTranche2Amount())
                .tranche2Deadline(request.getTranche2Deadline())
                .classrooms(classrooms)
                .build();
    }

    /**
     * Convertit une entité SchoolFee en SchoolFeeResponse.
     * Le champ "classrooms" reprend les noms des classes concernées.
     *
     * @param schoolFee l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public SchoolFeeResponse toDTO(SchoolFee schoolFee) {
        if (schoolFee == null) {
            return null;
        }

        List<String> classroomNames = schoolFee.getClassrooms() != null
                ? schoolFee.getClassrooms().stream().map(Classroom::getName).collect(Collectors.toList())
                : Collections.emptyList();

        return SchoolFeeResponse.builder()
                .id(schoolFee.getId())
                .label(schoolFee.getLabel())
                .inscriptionAmount(schoolFee.getInscriptionAmount())
                .tranche1Amount(schoolFee.getTranche1Amount())
                .tranche1Deadline(schoolFee.getTranche1Deadline())
                .tranche2Amount(schoolFee.getTranche2Amount())
                .tranche2Deadline(schoolFee.getTranche2Deadline())
                .classrooms(classroomNames)
                .build();
    }

    /**
     * Convertit une liste d'entités SchoolFee en liste de SchoolFeeResponse.
     *
     * @param schoolFees la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<SchoolFeeResponse> toDTOList(List<SchoolFee> schoolFees) {
        if (schoolFees == null) {
            return Collections.emptyList();
        }
        return schoolFees.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
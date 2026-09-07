package com.truhoster.school_management_system.student.mapper;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.student.dto.StudentRequest;
import com.truhoster.school_management_system.student.dto.StudentResponse;
import com.truhoster.school_management_system.student.entity.Student;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final ClassroomRepository classroomRepository;

    /**
     * Convertit un StudentRequest en entité Student.
     * Récupère la Classroom correspondante à partir du classroomId fourni.
     * Ne définit pas le matricule (généré séparément côté service).
     *
     * @param request les données envoyées par le client
     * @return l'entité Student prête à être persistée
     */
    public Student toEntity(StudentRequest request) {
        if (request == null) {
            return null;
        }

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Classroom not found with id: " + request.getClassroomId()));

        return Student.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .isRepeating(request.getIsRepeating())
                .classroom(classroom)
                .build();
    }

    /**
     * Convertit une entité Student en StudentResponse.
     * Le champ "classroom" du DTO reprend le nom de la classe associée.
     *
     * @param student l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public StudentResponse toDTO(Student student) {
        if (student == null) {
            return null;
        }

        return StudentResponse.builder()
                .id(student.getId())
                .firstname(student.getFirstname())
                .lastname(student.getLastname())
                .matricule(student.getMatricule() != null ? student.getMatricule() : null)
                .classroom(student.getClassroom() != null ? student.getClassroom().getName() : null)
                .photoUrlLink(student.getPhotoUrlLink() != null ? student.getPhotoUrlLink() : null)
                .build();
    }

    /**
     * Convertit une liste d'entités Student en liste de StudentResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param students la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<StudentResponse> toDTOList(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
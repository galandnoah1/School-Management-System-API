package com.truhoster.school_management_system.student.dto;



import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentRequest {
    @NotNull(message = "Le prenom est obligatoire")
    private String firstname;

    @NotNull(message = "Le nom est obligatoire")
    private String lastname;

    @NotNull(message = "On doit savoir si l'eleve reprend la classe")
    private Boolean isRepeating;

    @NotNull(message = "Il faut renseigner la salle de classe de l'eleve")
    private Integer classroomId;
}


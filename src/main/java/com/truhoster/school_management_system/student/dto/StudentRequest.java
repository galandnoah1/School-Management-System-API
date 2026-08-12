package com.truhoster.school_management_system.student.dto;



import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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


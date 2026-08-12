package com.truhoster.school_management_system.subject.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectByClassroomRequest {
    @NotNull(message = "Veuillez definir le coefficient")
    private Integer coefficient;

    @NotNull(message = "Veullez selectionner la salle de classe")
    private Integer classroomId;

    @NotNull(message = "Veuillez selectionner la matiere")
    private Integer subjectId;
}

package com.truhoster.school_management_system.note.dto;

import com.truhoster.school_management_system.note.enums.Evaluation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequest {
    @NotNull(message = "Veuillez entrer la note de l'eleve")
    private Double note;

    @NotNull(message = "Veuillez selectionner l'eleve")
    private Integer studentId;

    @NotNull(message = "Veuillez selectionner la matiere")
    private Integer subjectId;

    @NotNull(message = "Veuillez selectionner l'evaluation")
    private Evaluation evaluation;

    @NotNull(message = "Veuillez selectionner la salle de classe")
    private Integer classroomId;
}

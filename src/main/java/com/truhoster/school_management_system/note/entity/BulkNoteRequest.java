package com.truhoster.school_management_system.note.entity;

import com.truhoster.school_management_system.note.enums.Evaluation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkNoteRequest {

    @NotNull(message = "La matière est obligatoire")
    private Integer subjectId;

    @NotNull(message = "L'évaluation est obligatoire")
    private Evaluation evaluation;

    @NotNull(message = "La classe est obligatoire")
    private Integer classroomId;

    @NotEmpty(message = "La liste des notes ne peut pas être vide")
    @Valid
    private List<StudentNoteEntry> notes;
}
package com.truhoster.school_management_system.note.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentNoteEntry {

    @NotNull(message = "L'identifiant de l'élève est obligatoire")
    private Integer studentId;

    @NotNull(message = "La note est obligatoire")
    private Double note;
}
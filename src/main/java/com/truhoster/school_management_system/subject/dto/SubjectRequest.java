package com.truhoster.school_management_system.subject.dto;

import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.subject.enums.SubjectGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectRequest {
    @NotNull(message = "Le nom de la matiere est obligatoire")
    private String name;

    @NotNull(message = "La section est obligatoire")
    private Section section;

    @NotNull(message = "Le groupe de la matiere est obligatoire")
    private SubjectGroup subjectGroup;
}

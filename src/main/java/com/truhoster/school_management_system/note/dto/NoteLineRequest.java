package com.truhoster.school_management_system.note.dto;

import com.truhoster.school_management_system.note.enums.Trimester;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteLineRequest {
    private String subject;
    private Double note1;
    private Double note2;
    private Double average;
    private Integer coefficient;
    private Double notecoefficie;
    private String appreciation;
    private String teacher;
    @Enumerated(EnumType.STRING)
    private Trimester trimester;
}

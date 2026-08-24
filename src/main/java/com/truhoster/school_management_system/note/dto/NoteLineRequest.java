package com.truhoster.school_management_system.note.dto;

import com.truhoster.school_management_system.note.enums.Trimester;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

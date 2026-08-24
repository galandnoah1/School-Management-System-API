package com.truhoster.school_management_system.reportcard.dto;

import com.truhoster.school_management_system.note.dto.NoteLineResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardResponse {
    private Integer id;
    private Integer ranking;
    private Double average;
    private Double average1;
    private Double average2;
    private Double firstaverage;
    private Double lastaverage;
    private Double overallaverage;
    private String appreciation;
    private String trimester;
    private String student;
    private String classroom;
    private List<NoteLineResponse> noteLines;
}
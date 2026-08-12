package com.truhoster.school_management_system.note.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NoteResponse {
    private Integer id;
    private String subject;
    private Double note;
    private String classroom;
    private String student;
    private String evaluation;
}

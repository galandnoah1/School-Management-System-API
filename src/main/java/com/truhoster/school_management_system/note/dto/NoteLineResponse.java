package com.truhoster.school_management_system.note.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteLineResponse {
    private Integer id;
    private String subject;
    private String group;
    private Double note1;
    private Double note2;
    private Double note3;
    private Double note4;
    private Double note5;
    private Double note6;
    private double average;
    private int coefficient;
    private double notecoefficie;
    private String appreciation;
    private String teacher;
    private String trimester;
}

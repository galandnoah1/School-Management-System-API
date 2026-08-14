package com.truhoster.school_management_system.printhistory.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintHistoryResponse {
    private Integer id;
    private String action;
    private Integer reportCardId;
    private String student;
    private String classroom;
    private String trimester;
    private Integer count;
    private String performedBy;
    private Timestamp performedAt;
    private Timestamp updatedAt;
}
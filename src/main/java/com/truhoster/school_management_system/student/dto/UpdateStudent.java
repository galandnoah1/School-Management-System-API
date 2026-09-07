package com.truhoster.school_management_system.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateStudent {
    private String matricule;
    private String firstname;
    private String lastname;
}

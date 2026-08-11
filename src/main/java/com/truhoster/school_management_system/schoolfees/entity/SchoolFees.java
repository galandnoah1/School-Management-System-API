package com.truhoster.school_management_system.schoolfees.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "schoolfees")
@AllArgsConstructor
@NoArgsConstructor
public class SchoolFees {
}

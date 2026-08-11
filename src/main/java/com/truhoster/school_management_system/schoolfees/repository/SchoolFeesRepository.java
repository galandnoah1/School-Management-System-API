package com.truhoster.school_management_system.schoolfees.repository;

import com.truhoster.school_management_system.schoolfees.entity.SchoolFees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolFeesRepository extends JpaRepository<SchoolFees, Integer> {
}

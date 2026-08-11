package com.truhoster.school_management_system.classroom.mapper;

import com.truhoster.school_management_system.classroom.dto.ClassroomRequest;
import com.truhoster.school_management_system.classroom.dto.ClassroomResponse;
import com.truhoster.school_management_system.classroom.entity.Classroom;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClassroomMapper {

    public Classroom toEntity(ClassroomRequest request) {
        if (request == null) {
            return null;
        }
        return Classroom.builder()
                .section(request.getSection())
                .level(request.getLevel())
                .speciality(request.getSpeciality())
                .lv2(request.getLv2())
                .repartition(request.getRepartition())
                .build();
    }

    public ClassroomResponse toDTO(Classroom classroom) {
        if (classroom == null) {
            return null;
        }
        return ClassroomResponse.builder()
                .name(classroom.getName())
                .size(classroom.getStudentList() != null ? classroom.getStudentList().size() : 0)
                .build();
    }

    public List<ClassroomResponse> toDTOList(List<Classroom> classrooms) {
        if (classrooms == null) {
            return Collections.emptyList();
        }
        return classrooms.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
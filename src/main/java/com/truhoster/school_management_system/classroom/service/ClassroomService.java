package com.truhoster.school_management_system.classroom.service;

import com.truhoster.school_management_system.classroom.dto.ClassroomRequest;
import com.truhoster.school_management_system.classroom.dto.ClassroomResponse;
import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.classroom.mapper.ClassroomMapper;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;

    @Transactional
    public ClassroomResponse create(ClassroomRequest request) {
        Classroom classroom = classroomMapper.toEntity(request);
        if (classroomRepository.existsByNameIgnoreCase(generateName(request)))
        {
            throw new RuntimeException("Classroom with this name already exist");
        }
        classroom.setName(generateName(request));
        Classroom saved = classroomRepository.save(classroom);
        return classroomMapper.toDTO(saved);
    }

    @Transactional
    public ClassroomResponse update(Integer id, ClassroomRequest request) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found with id: " + id));

        classroom.setSection(request.getSection());
        classroom.setLevel(request.getLevel());
        classroom.setSpeciality(request.getSpeciality());
        classroom.setLv2(request.getLv2());
        classroom.setRepartition(request.getRepartition());
        classroom.setName(generateName(request));

        Classroom updated = classroomRepository.save(classroom);
        return classroomMapper.toDTO(updated);
    }


    @Transactional
    public ClassroomResponse getById(Integer id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found with id: " + id));
        return classroomMapper.toDTO(classroom);
    }

    @Transactional
    public List<ClassroomResponse> getAll() {
        return classroomMapper.toDTOList(classroomRepository.findAll());
    }

    @Transactional
    public List<ClassroomResponse> getBySection(Section section) {
        return classroomMapper.toDTOList(classroomRepository.findBySection(section));
    }

    @Transactional
    public void delete(Integer id) {
        if (!classroomRepository.existsById(id)) {
            throw new EntityNotFoundException("Classroom not found with id: " + id);
        }
        classroomRepository.deleteById(id);
    }


    private String generateName(ClassroomRequest request) {
        StringBuilder sb = new StringBuilder();

        if (request.getLevel() != null) {
            sb.append(request.getLevel());
        }

        if (request.getSpeciality() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(request.getSpeciality());
        }

        if (request.getLv2() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(request.getLv2());
        }

        if (request.getRepartition() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(request.getRepartition());
        }

        return sb.length() > 0 ? sb.toString() : "Unnamed Classroom";
    }
}
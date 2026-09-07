package com.truhoster.school_management_system.timetable.service;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import com.truhoster.school_management_system.teacher.entity.Teacher;
import com.truhoster.school_management_system.teacher.repository.TeacherRepository;
import com.truhoster.school_management_system.timetable.dto.CreateLine;
import com.truhoster.school_management_system.timetable.dto.CreateTable;
import com.truhoster.school_management_system.timetable.dto.LineDTO;
import com.truhoster.school_management_system.timetable.dto.TableDTO;
import com.truhoster.school_management_system.timetable.entity.TimeTable;
import com.truhoster.school_management_system.timetable.entity.TimeTableLine;
import com.truhoster.school_management_system.timetable.mapper.TimeTableMapper;
import com.truhoster.school_management_system.timetable.repository.LineRepo;
import com.truhoster.school_management_system.timetable.repository.TableRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeTableService {
   private final LineRepo lineRepo;
   private final TableRepo tableRepo;
   private final TimeTableMapper timeTableMapper;
   private final ClassroomRepository roomRepo;
   private final TeacherRepository teacherRepository;
   private final SubjectRepository subjectRepository;

   /**
    * Cree un emploi du temps sans ligne
    * @param createTable les informations pour créer un emploi du temps
    * @return l'emploi du temps créé
    * */
   @Transactional
    public TableDTO createTable(CreateTable createTable){
        log.info("Creation d'un nouvel emploi du temps");
        TimeTable timeTable = timeTableMapper.toTable(createTable);

        Classroom classroom = roomRepo.findById(createTable.classroomId())
                .orElseThrow(()-> new RuntimeException("Salle de classe introuvable"));

        timeTable.setClassroom(classroom);

        log.info("Emploi du temps créé");
        return timeTableMapper.toTableDTO(
                tableRepo.save(timeTable)
        );
    }


    /**
     * Associe une ligne à un emploi du temps vide ou pas
     * @param createLine les informations de la nouvelle ligne associée à l'emploi du temps vide
     * @return les informations concernant la nouvelle ligne nouvellement créée
     * */
    @Transactional
    public LineDTO addLine(CreateLine createLine)
    {
        TimeTable table = tableRepo.findById(createLine.timeTableId())
                .orElseThrow(()-> new RuntimeException("Emploi du temps introuvable"));

        Teacher teacher = teacherRepository.findById(createLine.teacherId())
                .orElseThrow(()-> new RuntimeException("Enseignant introuvable"));

        Subject subject = subjectRepository.findById(createLine.subjectId())
                .orElseThrow(()-> new RuntimeException("Sujet introuvable"));

        TimeTableLine line = timeTableMapper.toLine(createLine);

        line.setTimetable(table);
        line.setTeacher(teacher);
        line.setSubject(subject);

        log.info("Nouvelle ligne d'emploi du temps créée ");

        return timeTableMapper.toLineDTO(
                lineRepo.save(line)
        );
    }


    /**
     * Retourne la liste des emplois du temps créés
     * */
    @Transactional
    public List<TableDTO> findAllTable()
    {
        return tableRepo.findAll()
                .stream()
                .map(timeTableMapper::toTableDTO)
                .toList();
    }

    /**
     * Retourne la liste des emplois du temps par salle de classe
     * */
    @Transactional
    public List<TableDTO> findTableByClassroom(Integer classroomId)
    {
        return tableRepo.findByClassroomId(classroomId)
                .stream()
                .map(timeTableMapper::toTableDTO)
                .toList();
    }

    /**
     * Retourne la fiche de programme par enseignant
     * */
    @Transactional
    public List<LineDTO> findLineByTeacher(Integer teacherId)
    {
        return lineRepo.findByTeacherId(teacherId)
                .stream()
                .map(timeTableMapper::toLineDTO)
                .toList();
    }
}

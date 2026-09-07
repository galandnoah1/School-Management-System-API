package com.truhoster.school_management_system.timetable.controller;

import com.truhoster.school_management_system.timetable.dto.CreateLine;
import com.truhoster.school_management_system.timetable.dto.CreateTable;
import com.truhoster.school_management_system.timetable.dto.LineDTO;
import com.truhoster.school_management_system.timetable.dto.TableDTO;
import com.truhoster.school_management_system.timetable.service.TimeTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
@Slf4j
public class TableController {
    private final TimeTableService tableService;

    /**
     * POST /api/v1/timetables
     * */
    @PostMapping
    public ResponseEntity<TableDTO> createTable(@Valid @RequestBody CreateTable createTable)
    {
        log.info("POST /api/v1/timetables - creation d'un nouvel emploi du temps");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        tableService.createTable(createTable)
                );
    }

    /**
     * POST /api/v1/timetables/lines
     * */
    @PostMapping("/lines")
    public ResponseEntity<LineDTO> createLine(@Valid @RequestBody CreateLine createLine)
    {
        log.info("POST /api/v1/timetables/lines - creation d'une nouvelle ligne d'emploi du temps");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        tableService.addLine(createLine)
                );
    }

    /**
     * GET /api/v1/timetables
     * */
    @GetMapping
    public ResponseEntity<List<TableDTO>> getTables()
    {
        log.info("GET /api/v1/timetables - Retourne la liste des emplois du temps créé");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        tableService.findAllTable()
                );
    }

    /**
     * GET /api/v1/timetables/teachers/{id}
     * */
    @GetMapping("/teachers/{id}")
    public ResponseEntity<List<LineDTO>> getLinesByTeacher(Integer id)
    {
        log.info("GET /api/v1/timetables/teachers/{id}  - Retourne le programme d'enseignement d'un enseignant");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        tableService.findLineByTeacher(id)
                );
    }


    /**
     * GET /api/v1/timetables/classrooms/{id}
     * */
    @GetMapping("/classrooms/{id}")
    public ResponseEntity<List<TableDTO>> getTablesByClassroom(@PathVariable Integer id)
    {
        log.info("GET /api/v1/timetables/classrooms/{id} - Retourne la liste des emplois du temps par salle de classe");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        tableService.findTableByClassroom(id)
                );
    }
}

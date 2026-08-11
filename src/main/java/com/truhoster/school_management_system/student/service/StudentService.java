package com.truhoster.school_management_system.student.service;

import com.truhoster.school_management_system.student.dto.StudentRequest;
import com.truhoster.school_management_system.student.dto.StudentResponse;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.student.mapper.StudentMapper;
import com.truhoster.school_management_system.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Crée un nouvel étudiant.
     * Le matricule est généré automatiquement au format MD + année + nombre aléatoire (1000-3000).
     *
     * @param request les données de l'étudiant à créer
     * @return le DTO de l'étudiant créé, avec son matricule généré
     */
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        student.setMatricule(generateMatricule());

        Student saved = studentRepository.save(student);
        return studentMapper.toDTO(saved);
    }

    /**
     * Met à jour un étudiant existant.
     * Le matricule n'est PAS régénéré lors d'une mise à jour (il reste fixe une fois attribué).
     *
     * @param id l'identifiant de l'étudiant à mettre à jour
     * @param request les nouvelles données
     * @return le DTO de l'étudiant mis à jour
     * @throws EntityNotFoundException si aucun étudiant ne correspond à l'id
     */
    public StudentResponse update(Integer id, StudentRequest request) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        Student updatedData = studentMapper.toEntity(request);

        existing.setFirstname(updatedData.getFirstname());
        existing.setLastname(updatedData.getLastname());
        existing.setRepeating(updatedData.isRepeating());
        existing.setClassroom(updatedData.getClassroom());


        Student saved = studentRepository.save(existing);
        return studentMapper.toDTO(saved);
    }

    /**
     * Récupère un étudiant par son id.
     *
     * @param id l'identifiant recherché
     * @return le DTO de l'étudiant trouvé
     * @throws EntityNotFoundException si aucun étudiant ne correspond à l'id
     */
    public StudentResponse getById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        return studentMapper.toDTO(student);
    }

    /**
     * Récupère la liste de tous les étudiants.
     *
     * @return la liste des DTOs de tous les étudiants
     */
    public List<StudentResponse> getAll() {
        return studentMapper.toDTOList(studentRepository.findAll());
    }

    /**
     * Récupère tous les étudiants appartenant à une classe donnée.
     *
     * @param classroomId l'identifiant de la classe
     * @return la liste des DTOs des étudiants de cette classe
     */
    public List<StudentResponse> getByClassroom(Integer classroomId) {
        return studentMapper.toDTOList(studentRepository.findByClassroomId(classroomId));
    }

    /**
     * Supprime un étudiant par son id.
     *
     * @param id l'identifiant de l'étudiant à supprimer
     * @throws EntityNotFoundException si aucun étudiant ne correspond à l'id
     */
    public void delete(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    /**
     * Génère un matricule unique au format : MD + année courante + nombre aléatoire entre 1000 et 3000.
     * Exemple : MD20261842
     * Vérifie l'unicité en base et regénère en cas de collision (rare mais possible).
     *
     * @return le matricule généré
     */
    private String generateMatricule() {
        String matricule;
        int currentYear = Year.now().getValue();

        do {
            int randomNumber = ThreadLocalRandom.current().nextInt(1000, 3001);
            matricule = "MD" + currentYear + randomNumber;
        } while (studentRepository.existsByMatricule(matricule));

        return matricule;
    }
}
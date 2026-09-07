package com.truhoster.school_management_system.student.service;

import com.truhoster.school_management_system.student.dto.StudentRequest;
import com.truhoster.school_management_system.student.dto.StudentResponse;
import com.truhoster.school_management_system.student.dto.UpdateStudent;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.student.mapper.StudentMapper;
import com.truhoster.school_management_system.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Crée un nouvel étudiant.
     *
     * @param request les données de l'étudiant à créer
     * @return le DTO de l'étudiant créé, avec son matricule généré
     */

    public StudentResponse create(StudentRequest request) {
        log.info("Enregistrement d'un nouvel eleve, matricule {}", request.getMatricule());
        Student student = studentMapper.toEntity(request);

        if (studentRepository.existsByMatricule(request.getMatricule()))
        {
            log.warn("Le matricule {} a deja ete attribue a un autre eleve", request.getMatricule());
            throw new RuntimeException("Ce matricule est deja pris");
        }

        Student saved = studentRepository.save(student);
        log.info("Eleve matricule {} enregistre avec succès ",request.getMatricule());
        return studentMapper.toDTO(saved);
    }

    /**
     * Met à jour un étudiant existant.
     * Le matricule n'est PAS régénéré lors d'une mise à jour (il reste fixe une fois attribué).
     *
     * @param id l'identifiant de l'étudiant à mettre à jour
     * @param update les nouvelles données
     * @return le DTO de l'étudiant mis à jour
     * @throws EntityNotFoundException si aucun étudiant ne correspond à l'id
     */
    public StudentResponse update(Integer id, UpdateStudent update) throws IOException {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        if (update.getMatricule() != null)
        {
            log.info("Modification du matricule de l'eleve matricule {}", existing.getMatricule() != null ? existing.getMatricule() : "Non assigne");
            existing.setMatricule(update.getMatricule());
        }

        if (update.getLastname() != null)
        {
            log.info("Modification du nom de l'eleve {}", existing.getLastname());
            existing.setLastname(update.getLastname());
        }

        if (update.getFirstname() != null)
        {
            log.info("Modification du prenom de l'eleve {}", existing.getLastname());
            existing.setFirstname(update.getFirstname());
        }


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
     * Ajoute ou modifie la photo de profil d'un élève
     * @param id l'identifiant de l'eleve
     * @param file l'image envoyée
     * */
    public StudentResponse addStudentProfilePicture(Integer id, MultipartFile file)
    {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        StudentResponse studentResponse = null;

        if(file != null)
        {
            Path path = Path.of("photos");

            try{
                if (!Files.exists(path))
                {
                    Files.createDirectory(path);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String[] fileTab = file.getOriginalFilename() != null ? file.getOriginalFilename().split("\\.") : new String[2];
            String fileName = "photo_de_"+student.getFirstname()+"_"+student.getLastname()+student.getClassroom().getName()+"."+fileTab[1];

            Path finalPath = path.resolve(fileName);

            try(OutputStream outputStream = Files.newOutputStream(finalPath))
            {
                outputStream.write(file.getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            student.setPhotoUrlLink(finalPath.getFileName().toString());

            log.info("Photo de profil de {} ajouté avec succès", student.getLastname());

            Student updatedStudent = studentRepository.save(student);;

            studentResponse =  studentMapper.toDTO(updatedStudent);
        }

        return studentResponse;
    }
}
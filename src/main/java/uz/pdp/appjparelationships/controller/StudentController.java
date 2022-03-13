package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SubjectRepository subjectRepository;


    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentsForFaculty(@PathVariable Integer facultyId,
                                               @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentsForGroup(@PathVariable Integer groupId,
                                             @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupId, pageable);
    }


    @PostMapping("/save")
    public String save(@RequestBody StudentDto studentDto) {
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
        if (optionalAddress.isPresent()) {
            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()) {
                boolean haveAllSubject = false;
                for (Integer subjectId : studentDto.getSubjectIds()) {
                    Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                    if (optionalSubject.isPresent()) {
                        haveAllSubject = true;
                    } else {
                        haveAllSubject = false;
                        break;
                    }
                }
                if (haveAllSubject) {
                    List<Subject> subjects = new ArrayList<>();
                    for (Integer subjectId : studentDto.getSubjectIds()) {
                        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                        optionalSubject.ifPresent(subjects::add);
                    }
                    Student student = new Student();
                    student.setSubjects(subjects);
                    student.setAddress(optionalAddress.get());
                    student.setGroup(optionalGroup.get());
                    student.setFirstName(studentDto.getFirstName());
                    student.setLastName(studentDto.getLastName());
                    studentRepository.save(student);
                    return "Saved";
                }
            } else {
                return "Such group not found";
            }
        }
        return "Such address not found";
    }

    @PutMapping("/edit/{id}")
    public String editById(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
            if (optionalAddress.isPresent()) {
                Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
                if (optionalGroup.isPresent()) {
                    boolean haveAllSubject = false;
                    for (Integer subjectId : studentDto.getSubjectIds()) {
                        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                        if (optionalSubject.isPresent()) {
                            haveAllSubject = true;
                        } else {
                            haveAllSubject = false;
                            break;
                        }
                    }
                    if (haveAllSubject) {
                        List<Subject> subjects = new ArrayList<>();
                        for (Integer subjectId : studentDto.getSubjectIds()) {
                            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                            optionalSubject.ifPresent(subjects::add);
                        }
                        Student student = optionalStudent.get();
                        student.setSubjects(subjects);
                        student.setAddress(optionalAddress.get());
                        student.setGroup(optionalGroup.get());
                        student.setFirstName(studentDto.getFirstName());
                        student.setLastName(studentDto.getLastName());
                        studentRepository.save(student);
                        return "Edited";
                    }
                } else {
                    return "Such group not found";
                }
            }
            return "Such address not found";
        }
        return "Such student not found";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepository.deleteById(id);
            return "Deleted";
        }
        return "Such student not found";
    }

}

package test.controller;

import main.controller.project.ProjectManager;
import main.controller.request.CoordinatorManager;
import main.controller.request.RequestManager;
import main.controller.request.StudentManager;
import main.model.project.Project;
import main.model.project.ProjectStatus;
import main.model.request.Request;
import main.model.request.RequestStatus;
import main.model.user.Student;
import main.model.user.StudentStatus;
import main.model.user.Supervisor;
import main.repository.project.ProjectRepository;
import main.repository.request.RequestRepository;
import main.repository.user.CoordinatorRepository;
import main.repository.user.FacultyRepository;
import main.repository.user.StudentRepository;
import main.utils.exception.ModelAlreadyExistsException;
import main.utils.exception.ModelNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**

 The StudentRegistrationManagerTest class is used to test the methods of the StudentManager class that are related to
 registering and deregistering a student to a project. This class tests the following methods of the StudentManager class:
 */
public class StudentRegistrationMangerTest {
    /**
     * This method sets up the test data by clearing all data from the repositories and creating a student and a project.
     * This method is run before each test method.
     *
     * @throws ModelAlreadyExistsException if the model already exists in the repository.
     */
    @BeforeEach
    public void setUp() throws ModelAlreadyExistsException {
        StudentRepository.getInstance().clear();
        CoordinatorRepository.getInstance().clear();
        FacultyRepository.getInstance().clear();
        RequestRepository.getInstance().clear();
        ProjectRepository.getInstance().clear();
        Student student1 = new Student("FPU001", "Pu Fanyi", "pufanyi@gmail.com");
        Student student2 = new Student("JQY001", "Jin Qingyang", "jinqingyang@gmail.com");
        Supervisor supervisor1 = new Supervisor("BOAN001", "BO AN", "boan@ntu.edu.sg");
        Project project1 = new Project("1", "Blockchain technology", "BOAN001");
        StudentRepository.getInstance().add(student1);
        StudentRepository.getInstance().add(student2);
        ProjectRepository.getInstance().add(project1);
        FacultyRepository.getInstance().add(supervisor1);
        ProjectManager.updateProjectsStatus();
    }

    /**
     * This method tests the registration of a student to a project.
     * It tests the following:
     * - The initial status of the student and the project before registration
     * - The status of the student and the project after registration
     *
     * @throws ModelNotFoundException if the model is not found in the repository.
     * @throws ModelAlreadyExistsException if the model already exists in the repository.
     */
    @Test
    @DisplayName("Register Student")
    public void testRegisterStudent() throws ModelNotFoundException, ModelAlreadyExistsException {
        String studentID = "JQY001";
        String projectID = "1";
        Student student = StudentRepository.getInstance().getByID(studentID);
        Project project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(student.getStatus(), StudentStatus.UNREGISTERED);
        assertEquals(project.getStatus(), ProjectStatus.AVAILABLE);
        String supervisorID = project.getSupervisorID();
        String requestID = StudentManager.registerStudent(projectID, studentID);
        Request request = RequestRepository.getInstance().getByID(requestID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.PENDING);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.RESERVED);
    }

    /**
     * This method tests the deregistration of a student from a project.
     * @throws ModelAlreadyExistsException if the model already exists in the repository.
     * @throws ModelNotFoundException if the model is not found in the repository.
     */
    @Test
    @DisplayName("Approve Student Registration Request")
    public void ApproveStudentRegistration() throws ModelAlreadyExistsException, ModelNotFoundException {
        String studentID = "JQY001";
        String projectID = "1";
        Student student = StudentRepository.getInstance().getByID(studentID);
        Project project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(student.getStatus(), StudentStatus.UNREGISTERED);
        assertEquals(project.getStatus(), ProjectStatus.AVAILABLE);
        String supervisorID = project.getSupervisorID();
        String requestID = StudentManager.registerStudent(projectID, studentID);
        Request request = RequestRepository.getInstance().getByID(requestID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.PENDING);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.RESERVED);
        RequestManager.approveRequestForStatus(requestID);
        request = RequestRepository.getInstance().getByID(requestID);
        assertEquals(request.getStatus(), RequestStatus.APPROVED);
        CoordinatorManager.approveRegisterStudent(studentID, projectID, supervisorID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.REGISTERED);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.ALLOCATED);
    }

    /**
     * This method tests the rejection of a student registration request.
     * @throws ModelAlreadyExistsException if the model already exists in the repository.
     * @throws ModelNotFoundException if the model is not found in the repository.
     */
    @Test
    @DisplayName("Reject Student Registration Request")
    public void RejectStudentRegistration() throws ModelAlreadyExistsException, ModelNotFoundException {
        String studentID = "JQY001";
        String projectID = "1";
        Student student = StudentRepository.getInstance().getByID(studentID);
        Project project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(student.getStatus(), StudentStatus.UNREGISTERED);
        assertEquals(project.getStatus(), ProjectStatus.AVAILABLE);
        String supervisorID = project.getSupervisorID();
        String requestID = StudentManager.registerStudent(projectID, studentID);
        Request request = RequestRepository.getInstance().getByID(requestID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.PENDING);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.RESERVED);
        RequestManager.rejectRequestForStatus(requestID);
        request = RequestRepository.getInstance().getByID(requestID);
        assertEquals(request.getStatus(), RequestStatus.DENIED);
        CoordinatorManager.rejectRegisterStudent(studentID, projectID, supervisorID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.UNREGISTERED);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.AVAILABLE);
    }

    /**
     * Test the deregistration of student from the project
     *
     * @throws ModelAlreadyExistsException if the model already exists in the repository
     * @throws ModelNotFoundException if the model is not found in the repository
     */
    @Test
    @DisplayName("Deregister Student")
    public void deregisterStudentTest() throws ModelAlreadyExistsException, ModelNotFoundException {
        String studentID = "JQY001";
        String projectID = "1";
        Student student = StudentRepository.getInstance().getByID(studentID);
        Project project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(student.getStatus(), StudentStatus.UNREGISTERED);
        assertEquals(project.getStatus(), ProjectStatus.AVAILABLE);
        String supervisorID = project.getSupervisorID();
        String requestID = StudentManager.registerStudent(projectID, studentID);
        Request request = RequestRepository.getInstance().getByID(requestID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.PENDING);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.RESERVED);
        RequestManager.approveRequestForStatus(requestID);
        request = RequestRepository.getInstance().getByID(requestID);
        assertEquals(request.getStatus(), RequestStatus.APPROVED);
        CoordinatorManager.approveRegisterStudent(studentID, projectID, supervisorID);
        student = StudentRepository.getInstance().getByID(studentID);
        assertEquals(student.getStatus(), StudentStatus.REGISTERED);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(project.getStatus(), ProjectStatus.ALLOCATED);
        String request2ID = StudentManager.deregisterStudent(projectID, studentID);
        Request request2 = RequestRepository.getInstance().getByID(request2ID);
        assertEquals(request2.getStatus(), RequestStatus.PENDING);
        RequestManager.approveRequestForStatus(request2ID);
        request2 = RequestRepository.getInstance().getByID(request2ID);
        assertEquals(request2.getStatus(), RequestStatus.APPROVED);
        CoordinatorManager.approveDeregisterStudent(studentID, projectID, supervisorID);
        student = StudentRepository.getInstance().getByID(studentID);
        project = ProjectRepository.getInstance().getByID(projectID);
        assertEquals(student.getStatus(), StudentStatus.DEREGISTERED);
        assertEquals(project.getStatus(), ProjectStatus.AVAILABLE);
    }
}

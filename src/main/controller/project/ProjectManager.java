package main.controller.project;

import main.controller.request.SupervisorManager;
import main.model.project.Project;
import main.model.project.ProjectStatus;
import main.model.user.Student;
import main.model.user.StudentStatus;
import main.model.user.Supervisor;
import main.repository.project.ProjectRepository;
import main.repository.user.FacultyRepository;
import main.repository.user.StudentRepository;
import main.utils.config.Location;
import main.utils.exception.ModelAlreadyExistsException;
import main.utils.exception.ModelNotFoundException;
import main.utils.iocontrol.CSVReader;
import main.utils.parameters.EmptyID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class manages the project
 */
public class ProjectManager {

    /**
     * Change the title of a project
     *
     * @param projectID the ID of the project
     * @param newTitle  the new title of the project
     * @throws ModelNotFoundException if the project is not found
     */
    public static void changeProjectTitle(String projectID, String newTitle) throws ModelNotFoundException {
        Project p1 = ProjectRepository.getInstance().getByID(projectID);
        p1.setProjectTitle(newTitle);
        ProjectRepository.getInstance().update(p1);
        ProjectManager.updateProjectsStatus();
    }

    /**
     * Change the supervisor of a project
     *
     * @return the new supervisor
     */
    public static List<Project> viewAllProject() {
        return ProjectRepository.getInstance().getList();
    }

    /**
     * View all the projects that are available
     *
     * @return the list of available projects
     */
    public static List<Project> viewAvailableProjects() {
        return ProjectRepository.getInstance().findByRules(p -> p.getStatus() == ProjectStatus.AVAILABLE);
    }

    /**
     * create a new project
     *
     * @param projectID    the ID of the project
     * @param projectTitle the title of the project
     * @param supervisorID the ID of the supervisor
     * @throws ModelAlreadyExistsException if the project already exists
     */
    public static void createProject(String projectID, String projectTitle, String supervisorID) throws ModelAlreadyExistsException {
        Project p1 = new Project(projectID, projectTitle, supervisorID);
        ProjectRepository.getInstance().add(p1);
        ProjectManager.updateProjectsStatus();
    }

    /**
     * create a new project
     *
     * @param projectTitle the title of the project
     * @param supervisorID the ID of the supervisor
     * @throws ModelAlreadyExistsException if the project already exists
     *
     * @return the new project
     */
    public static Project createProject(String projectTitle, String supervisorID) throws ModelAlreadyExistsException {
        Project p1 = new Project(getNewProjectID(), projectTitle, supervisorID);
        ProjectRepository.getInstance().add(p1);
        ProjectManager.updateProjectsStatus();
        return p1;
    }

    /**
     * get the list of all projects
     *
     * @return the list of all projects
     */
    public static List<Project> getAllProject() {
        return ProjectRepository.getInstance().getList();
    }

    /**
     * get the list of all projects by status
     *
     * @param projectStatus the status of the project
     * @return the list of all projects
     */
    public static List<Project> getAllProjectByStatus(ProjectStatus projectStatus) {
        return ProjectRepository.getInstance().findByRules(project -> project.getStatus().equals(projectStatus));
    }

    /**
     * get the list of all projects by supervisor
     *
     * @return the list of all projects
     */
    public static String getNewProjectID() {
        int max = 0;
        for (Project p : ProjectRepository.getInstance()) {
            int id = Integer.parseInt(p.getID().substring(1));
            if (id > max) {
                max = id;
            }
        }
        return "P" + (max + 1);
    }

    /**
     * transfer a student to a new supervisor
     *
     * @param projectID    the ID of the project
     * @param supervisorID the ID of the supervisor
     * @throws ModelNotFoundException if the project is not found
     */
    public static void transferToNewSupervisor(String projectID, String supervisorID) throws ModelNotFoundException {
        Project p1 = ProjectRepository.getInstance().getByID(projectID);
        if (!FacultyRepository.getInstance().contains(supervisorID)) {
            throw new IllegalStateException("Supervisor Not Found!");
        }
        Supervisor oldsupervisor = FacultyRepository.getInstance().getByID(p1.getSupervisorID());
        Supervisor newsupervisor = FacultyRepository.getInstance().getByID(supervisorID);
        Student student = StudentRepository.getInstance().getByID(p1.getStudentID());
        student.setSupervisorID(supervisorID);
        p1.setSupervisorID(supervisorID);
        ProjectRepository.getInstance().update(p1);
        FacultyRepository.getInstance().update(oldsupervisor);
        FacultyRepository.getInstance().update(newsupervisor);
        StudentRepository.getInstance().update(student);
        ProjectManager.updateProjectsStatus();
    }


    /**
     * deallocate a project
     *
     * @param projectID the ID of the project
     * @throws ModelNotFoundException if the project is not found
     */
    public static void deallocateProject(String projectID) throws ModelNotFoundException {
        Project p1 = ProjectRepository.getInstance().getByID(projectID);
        if (p1.getStatus() != ProjectStatus.ALLOCATED) {
            throw new IllegalStateException("The project status is not ALLOCATED");
        }
        Student student;
        try {
            student = StudentRepository.getInstance().getByID(p1.getStudentID());
        } catch (ModelNotFoundException e) {
            throw new IllegalStateException("Student not found");
        }
        String supervisorID = p1.getSupervisorID();
        Supervisor supervisor = FacultyRepository.getInstance().getByID(supervisorID);
        student.setProjectID(EmptyID.EMPTY_ID);
        student.setSupervisorID(EmptyID.EMPTY_ID);
        student.setStatus(StudentStatus.DEREGISTERED);
        p1.setStudentID(EmptyID.EMPTY_ID);
        p1.setStatus(ProjectStatus.AVAILABLE);
        ProjectRepository.getInstance().update(p1);
        StudentRepository.getInstance().update(student);
        FacultyRepository.getInstance().update(supervisor);
        ProjectManager.updateProjectsStatus();
    }

    /**
     * allocate a project
     *
     * @param projectID the ID of the project
     * @param studentID the ID of the student
     * @throws ModelNotFoundException if the project is not found
     */
    public static void allocateProject(String projectID, String studentID) throws ModelNotFoundException {
        Project p1 = ProjectRepository.getInstance().getByID(projectID);
        Student student;
        try {
            student = StudentRepository.getInstance().getByID(studentID);
        } catch (ModelNotFoundException e) {
            throw new IllegalStateException("Student not found");
        }
        if (p1.getStatus() == ProjectStatus.ALLOCATED) {
            throw new IllegalStateException("Project is already allocated");
        }
        if (student.getStatus() == StudentStatus.REGISTERED) {
            throw new IllegalStateException("Student is already registered");
        }
        p1.setStatus(ProjectStatus.ALLOCATED);
        p1.setStudentID(studentID);
        student.setProjectID(projectID);
        student.setSupervisorID(p1.getSupervisorID());
        student.setStatus(StudentStatus.REGISTERED);
        String supervisorID = p1.getSupervisorID();
        Supervisor supervisor = FacultyRepository.getInstance().getByID(supervisorID);
        ProjectRepository.getInstance().update(p1);
        StudentRepository.getInstance().update(student);
        FacultyRepository.getInstance().update(supervisor);
        ProjectManager.updateProjectsStatus();
    }

    /**
     * load projects from csv resource file
     */
    public static void loadProjects() {
        List<List<String>> projects = CSVReader.read(Location.RESOURCE_LOCATION + "/resources/ProjectList.csv", true);
        for (List<String> project : projects) {
            try {
                String supervisorName = project.get(0);
                String projectName = project.get(1);
                List<Supervisor> supervisors = FacultyRepository.getInstance().findByRules(s -> s.checkUsername(supervisorName));
                if (supervisors.size() == 0) {
                    System.out.println("Load project " + projectName + " failed: supervisor " + supervisorName + " not found");
                } else if (supervisors.size() == 1) {
                    ProjectManager.createProject(projectName, supervisors.get(0).getID());
                } else {
                    System.out.println("Load project " + projectName + " failed: multiple supervisors found");
                }
            } catch (ModelAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * check if the repository is empty
     *
     * @return true if the repository is empty
     */
    public static boolean repositoryIsEmpty() {
        return ProjectRepository.getInstance().isEmpty();
    }

    /**
     * Check if the project is not in the repository
     *
     * @param projectID the ID of the project
     * @return true if the project is not in the repository
     */
    public static boolean notContainsProjectByID(String projectID) {
        return !ProjectRepository.getInstance().contains(projectID);
    }

    /**
     * Check if the project is in the repository
     *
     * @param projectID the ID of the project
     * @return true if the project is in the repository
     */
    public static boolean containsProjectByID(String projectID) {
        return ProjectRepository.getInstance().contains(projectID);
    }

    /**
     * get the project of a student
     *
     * @param student the student
     * @return the project of the student
     */
    public static Project getStudentProject(Student student) {
        if (EmptyID.isEmptyID(student.getProjectID())) {
            return null;
        } else {
            try {
                return ProjectRepository.getInstance().getByID(student.getProjectID());
            } catch (ModelNotFoundException e) {
                throw new IllegalStateException("Project " + student.getProjectID() + " not found");
            }
        }
    }

    /**
     * get the project of a supervisor
     *
     * @param projectID the ID of the project
     * @return the project of the supervisor
     * @throws ModelNotFoundException if the project is not found
     */
    public static Project getByID(String projectID) throws ModelNotFoundException {
        return ProjectRepository.getInstance().getByID(projectID);
    }

    /**
     * get all available projects
     *
     * @return all available projects
     */
    public static List<Project> getAllAvailableProject() {
        return ProjectRepository.getInstance().findByRules(p -> p.getStatus() == ProjectStatus.AVAILABLE);
    }

    /**
     * get project by the project ID
     * @param projectID the ID of the project
     * @return the project
     * @throws ModelNotFoundException if the project is not found
     */
    public static Project getProjectByID(String projectID) throws ModelNotFoundException {
        return ProjectRepository.getInstance().getByID(projectID);
    }

    /**
     * get all projects by supervisor
     *
     * @param supervisorID the ID of the supervisor
     * @return all projects by supervisor
     */
    public static List<Project> getAllProjectsBySupervisor(String supervisorID) {
        return ProjectRepository.getInstance().findByRules(p -> p.getSupervisorID().equalsIgnoreCase(supervisorID));
    }

    /**
     * update the status of all projects
     */
    public static void updateProjectsStatus() {
        List<Supervisor> supervisors = SupervisorManager.getAllUnavailableSupervisors();
        Set<String> supervisorIDs = new HashSet<>();
        for (Supervisor supervisor : supervisors) {
            supervisorIDs.add(supervisor.getID());
        }
        List<Project> projects = ProjectRepository.getInstance().getList();
        for (Project project : projects) {
            if (supervisorIDs.contains(project.getSupervisorID()) && project.getStatus() == ProjectStatus.AVAILABLE) {
                project.setStatus(ProjectStatus.UNAVAILABLE);
            }
            if (!supervisorIDs.contains(project.getSupervisorID()) && project.getStatus() == ProjectStatus.UNAVAILABLE) {
                project.setStatus(ProjectStatus.AVAILABLE);
            }
        }
        ProjectRepository.getInstance().updateAll(projects);
    }
}

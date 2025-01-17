package main.model.user;

import main.utils.parameters.EmptyID;
import main.utils.parameters.NotNull;

import java.util.Map;

/**
 * This class represents a student, which is a type of user.
 * It extends the User class and includes a student ID field.
 */
public class Student implements User {
    /**
     * The ID of the student.
     */
    private String studentID;
    /**
     * The name of a student
     */
    private String studentName;
    /**
     * The email of a student
     */
    private String email;
    /**
     * The status of a student
     */
    private StudentStatus status;
    /**
     * The ID of the supervisor
     */
    private String supervisorID;
    /**
     * The ID of the project
     */
    private String projectID;
    private String hashedPassword;

    /**
     * Constructs a new Student object with the specified student ID and default password.
     *
     * @param studentID   the ID of the student.
     * @param studentName the name of the student.
     * @param email       the email of the student.
     */
    public Student(String studentID, String studentName, String email) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.email = email;
        this.status = StudentStatus.UNREGISTERED;
        supervisorID = EmptyID.EMPTY_ID;
        projectID = EmptyID.EMPTY_ID;
    }

    /**
     * Constructs a new Student object with the specified student ID and password.
     *
     * @param studentID      the ID of the student.
     * @param studentName    the name of the student.
     * @param email          the email of the student.
     * @param hashedPassword the password of the student.
     */
    public Student(String studentID, String studentName, String email, @NotNull String hashedPassword) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.email = email;
        this.status = StudentStatus.UNREGISTERED;
        supervisorID = EmptyID.EMPTY_ID;
        projectID = EmptyID.EMPTY_ID;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Constructs a new Student object with the specified student ID and password.
     *
     * @param informationMap the map
     */
    public Student(Map<String, String> informationMap) {
        fromMap(informationMap);
    }

    /**
     * default constructor for Student class
     */
    public Student() {
        super();
        this.email = EmptyID.EMPTY_ID;
        this.studentID = EmptyID.EMPTY_ID;
        this.studentName = EmptyID.EMPTY_ID;
        this.status = StudentStatus.UNREGISTERED;
    }

    /**

     Creates a new Student object based on the information in the provided map.
     The map should contain the necessary information to construct the Student object,
     such as the student's name, email, and ID.
     @param informationMap a map containing the information required to create a new Student object
     @return a new Student object with the information provided in the map
     */
    public static User getUser(Map<String, String> informationMap) {
        return new Student(informationMap);
    }

    /**
     * Gets the email of the user
     *
     * @return the email of the user
     */
    @Override
    public String getID() {
        return this.studentID;
    }

    /**
     * Gets the username of the user
     *
     * @return the name of the user
     */
    @Override
    public String getUserName() {
        return this.studentName;
    }

    /**
     * Gets the email of the user
     *
     * @return the email of the user
     */
    @Override
    public String getEmail() {
        return this.email;
    }

    /**
     * Gets the status of the student
     *
     * @return the status of the student
     */
    public StudentStatus getStatus() {
        return this.status;
    }

    /**
     * Sets the status of the student
     *
     * @param status the new status of the student
     */
    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    /**
     * Gets the ID of the supervisor
     *
     * @return the ID of the supervisor
     */
    public String getSupervisorID() {
        return supervisorID;
    }

    /**
     * Sets the ID of the supervisor
     *
     * @param supervisorID the ID of the supervisor
     */
    public void setSupervisorID(String supervisorID) {
        this.supervisorID = supervisorID;
    }

    /**
     * Gets the ID of the project
     *
     * @return the ID of the project
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * Sets the ID of the project
     *
     * @param projectID the ID of the project
     */
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    /**
     * getter for the password
     *
     * @return hashedPassword
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * setter for the password
     *
     * @param hashedPassword the password that to be set
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}

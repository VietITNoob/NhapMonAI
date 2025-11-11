package models;

import java.util.List;

/**
 * Student.java
 * Đại diện cho Sinh viên và các môn họ muốn đăng ký.
 */
public class Student {
    private final String studentId;
    // Danh sách các môn học muốn đăng ký (đã sắp xếp theo NV1, NV2, ...)
    private final List<Course> desiredCourses; 

    public Student(String studentId, List<Course> desiredCourses) {
        this.studentId = studentId;
        this.desiredCourses = desiredCourses;
    }

    public String getStudentId() { return studentId; }
    public List<Course> getDesiredCourses() { return desiredCourses; }
    
    @Override
    public String toString() { return studentId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return studentId.equals(((Student) o).studentId);
    }
    
    @Override
    public int hashCode() { return studentId.hashCode(); }
}
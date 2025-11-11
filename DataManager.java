package Manage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import models.Course;
import models.CourseClass;
import models.Student;
import models.TimeSlot;

/**
 * DataManager.java
 * Lớp giả lập cơ sở dữ liệu, chứa và cung cấp dữ liệu.
 */
public class DataManager {
    private final List<Student> students = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<CourseClass> courseClasses = new ArrayList<>();

    /**
     * Lấy danh sách các Lớp học phần thuộc một Môn học
     */
    public List<CourseClass> getClassesForCourse(Course course) {
        return courseClasses.stream()
            .filter(c -> c.getCourse().equals(course))
            .collect(Collectors.toList());
    }

    public List<Student> getAllStudents() { return students; }
    public List<CourseClass> getAllCourseClasses() { return courseClasses; }

    /**
     * Tạo dữ liệu giả (mock data)
     */
    public void loadMockData() {
        // 1. Tạo các Môn học (Course)
        Course math = new Course("C1", "Toan Cao Cap");
        Course physics = new Course("C2", "Vat Ly Dai Cuong");
        Course history = new Course("C3", "Lich Su Dang");
        courses.addAll(Arrays.asList(math, physics, history));

        // 2. Tạo các Lớp học phần (CourseClass)
        // LƯU Ý: Toán A và Lý A cố tình bị TRÙNG LỊCH
        courseClasses.add(new CourseClass("Math_A", math, new TimeSlot("Mon", 7, 9), 25));
        courseClasses.add(new CourseClass("Math_B", math, new TimeSlot("Tue", 9, 11), 30));
        
        courseClasses.add(new CourseClass("Phys_A", physics, new TimeSlot("Mon", 7, 9), 25)); // TRÙNG VỚI TOÁN A
        courseClasses.add(new CourseClass("Phys_B", physics, new TimeSlot("Wed", 13, 15), 30));
        
        courseClasses.add(new CourseClass("Hist_A", history, new TimeSlot("Mon", 13, 15), 30));
        courseClasses.add(new CourseClass("Hist_B", history, new TimeSlot("Fri", 9, 11), 30));

        // 3. Tạo 50 Sinh viên
        for (int i = 0; i < 20; i++) {
            students.add(new Student("S_ML" + i, Arrays.asList(math, physics))); // NV1: Toán, NV2: Lý
        }
        for (int i = 0; i < 20; i++) {
            students.add(new Student("S_MH" + i, Arrays.asList(math, history))); // NV1: Toán, NV2: Sử
        }
        for (int i = 0; i < 10; i++) {
            students.add(new Student("S_LH" + i, Arrays.asList(physics, history))); // NV1: Lý, NV2: Sử
        }
        
        System.out.println("--- Dữ liệu đã tải ---");
        System.out.println("Tổng số SV: " + students.size());
        System.out.println("Tổng số Lớp học phần: " + courseClasses.size());
        System.out.println("----------------------");
    }
}
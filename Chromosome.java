package ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Config.GAConfig;
import Manage.DataManager;
import models.Course;
import models.CourseClass;
import models.Student;

/**
 * Chromosome.java
 * Đây là "Nhiễm sắc thể" (Chromosome).
 * Nó đại diện cho một giải pháp hoàn chỉnh (một lịch học đầy đủ).
 */
public class Chromosome implements Cloneable {
    
    private static final Random random = new Random();

    // "Gen" của chúng ta: Map mỗi SV với danh sách các Lớp học phần họ được gán
    private final Map<Student, List<CourseClass>> schedule;
    private double fitness = -1; // -1 nghĩa là chưa được tính
    private final DataManager data;

    /**
     * Constructor để KHỞI TẠO ngẫu nhiên
     */
    public Chromosome(DataManager data) {
        this.data = data;
        this.schedule = new HashMap<>();
        
        // Khởi tạo ngẫu nhiên
        for (Student s : data.getAllStudents()) {
            List<CourseClass> studentClasses = new ArrayList<>();
            // Duyệt qua các môn SV muốn học
            for (Course desiredCourse : s.getDesiredCourses()) {
                // Lấy tất cả các lớp học phần có thể cho môn đó
                List<CourseClass> options = data.getClassesForCourse(desiredCourse);
                if (!options.isEmpty()) {
                    // Chọn NGẪU NHIÊN 1 lớp
                    CourseClass randomClass = options.get(random.nextInt(options.size()));
                    studentClasses.add(randomClass);
                }
            }
            this.schedule.put(s, studentClasses);
        }
    }

    /**
     * Constructor để tạo "con" (dùng cho lai ghép)
     */
    public Chromosome(DataManager data, boolean empty) {
        this.data = data;
        this.schedule = new HashMap<>();
        // Để trống, chờ lai ghép điền vào
    }

    public double getFitness() {
        if (fitness == -1) {
            this.fitness = this.calculateFitness();
        }
        return fitness;
    }

    public Map<Student, List<CourseClass>> getSchedule() {
        return schedule;
    }
    
    // Hàm này cho phép GeneticAlgorithm đặt "gen" cho con
    public void setStudentSchedule(Student student, List<CourseClass> classes) {
        this.schedule.put(student, classes);
    }
    
    // Hàm này cho phép GeneticAlgorithm đột biến "gen"
    public List<CourseClass> getStudentSchedule(Student student) {
        return this.schedule.get(student);
    }

    /**
     * Hàm quan trọng nhất: HÀM THÍCH NGHI (FITNESS FUNCTION)
     * Tính điểm cho lịch học này. Điểm càng cao càng tốt.
     */
    private double calculateFitness() {
        int currentFitness = 0;

        // 1. RÀNG BUỘC CỨNG: TRÙNG LỊCH SINH VIÊN
        for (Student s : schedule.keySet()) {
            List<CourseClass> studentClasses = schedule.get(s);
            for (int i = 0; i < studentClasses.size(); i++) {
                for (int j = i + 1; j < studentClasses.size(); j++) {
                    if (studentClasses.get(i).getTimeSlot().overlaps(studentClasses.get(j).getTimeSlot())) {
                        currentFitness += GAConfig.HARD_CONSTRAINT_PENALTY; // Phạt nặng
                    }
                }
            }
        }

        // 2. RÀNG BUỘC CỨNG: VƯỢT SĨ SỐ LỚP
        Map<String, Integer> classCounts = new HashMap<>();
        // Đếm số lượng SV trong mỗi lớp
        for (List<CourseClass> studentClasses : schedule.values()) {
            for (CourseClass cc : studentClasses) {
                classCounts.put(cc.getClassId(), classCounts.getOrDefault(cc.getClassId(), 0) + 1);
            }
        }
        
        // So sánh với sĩ số tối đa
        for (CourseClass cc : data.getAllCourseClasses()) {
            int count = classCounts.getOrDefault(cc.getClassId(), 0);
            if (count > cc.getCapacity()) {
                // Phạt nặng cho mỗi SV vượt quá
                currentFitness += (count - cc.getCapacity()) * GAConfig.HARD_CONSTRAINT_PENALTY; 
            }
        }

        // 3. RÀNG BUỘC MỀM: ƯU TIÊN NGUYỆN VỌNG
        for (Student s : schedule.keySet()) {
            List<Course> desired = s.getDesiredCourses();
            List<CourseClass> assignedClasses = schedule.get(s);

            for (CourseClass assigned : assignedClasses) {
                Course assignedCourse = assigned.getCourse();
                if (desired.size() > 0 && desired.get(0).equals(assignedCourse)) {
                    currentFitness += GAConfig.SOFT_CONSTRAINT_REWARD * 2; // Thưởng x2 cho NV1
                } else if (desired.size() > 1 && desired.get(1).equals(assignedCourse)) {
                    currentFitness += GAConfig.SOFT_CONSTRAINT_REWARD; // Thưởng x1 cho NV2
                }
            }
        }

        return currentFitness;
    }

    /**
     * Tạo một bản sao sâu (deep copy) của NST này (dùng cho Elitism)
     */
    @Override
    public Chromosome clone() {
        Chromosome clone = new Chromosome(this.data, true); // Tạo NST rỗng
        // Sao chép sâu cái schedule
        for (Map.Entry<Student, List<CourseClass>> entry : this.schedule.entrySet()) {
            clone.schedule.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        clone.fitness = this.fitness;
        return clone;
    }
}
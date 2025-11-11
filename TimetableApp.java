package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Manage.DataManager;
import ga.Chromosome;
import ga.GeneticAlgorithm;
import models.CourseClass;
import models.Student;

/**
 * TimetableApp.java
 * Lớp chính chứa hàm main() để khởi chạy toàn bộ chương trình.
 */
public class TimetableApp {

    public static void main(String[] args) {
        // 1. Chuẩn bị dữ liệu
        DataManager data = new DataManager();
        data.loadMockData();

        // 2. Tạo đối tượng GA
        GeneticAlgorithm ga = new GeneticAlgorithm(data);

        // 3. Chạy thuật toán
        Chromosome bestSolution = ga.run();

        // 4. In kết quả
        System.out.println("\n--- Giải pháp tốt nhất tìm được ---");
        System.out.println("Fitness cuối cùng: " + bestSolution.getFitness());
        printSolutionDetails(bestSolution, data);
    }
    
    /**
     * Hàm tiện ích để in kết quả chi tiết
     */
    private static void printSolutionDetails(Chromosome solution, DataManager data) {
        System.out.println("\n--- Chi tiết vi phạm ---");
        int studentConflicts = 0;
        int capacityViolations = 0;

        // Kiểm tra trùng lịch SV
        for (Student s : solution.getSchedule().keySet()) {
            List<CourseClass> studentClasses = solution.getSchedule().get(s);
            for (int i = 0; i < studentClasses.size(); i++) {
                for (int j = i + 1; j < studentClasses.size(); j++) {
                    if (studentClasses.get(i).getTimeSlot().overlaps(studentClasses.get(j).getTimeSlot())) {
                        studentConflicts++;
                    }
                }
            }
        }
        System.out.println("Tổng số lần trùng lịch SV: " + studentConflicts);

        // Kiểm tra sĩ số
        Map<String, Integer> classCounts = new HashMap<>();
        for (List<CourseClass> studentClasses : solution.getSchedule().values()) {
            for (CourseClass cc : studentClasses) {
                classCounts.put(cc.getClassId(), classCounts.getOrDefault(cc.getClassId(), 0) + 1);
            }
        }

        System.out.println("\n--- Thống kê sĩ số lớp ---");
        for (CourseClass cc : data.getAllCourseClasses()) {
            int count = classCounts.getOrDefault(cc.getClassId(), 0);
            System.out.print("Lớp " + cc.getClassId() + " (Max: " + cc.getCapacity() + "): Đăng ký " + count);
            if (count > cc.getCapacity()) {
                System.out.println(" <-- VI PHẠM (" + (count - cc.getCapacity()) + " SV)");
                capacityViolations += (count - cc.getCapacity());
            } else {
                System.out.println(" (OK)");
            }
        }
        System.out.println("Tổng số SV bị vượt sĩ số: " + capacityViolations);

        if (studentConflicts == 0 && capacityViolations == 0) {
            System.out.println("\n==> KẾT LUẬN: LỊCH HỌC HỢP LỆ!");
        } else {
            System.out.println("\n==> KẾT LUẬN: LỊCH HỌC KHÔNG HỢP LỆ!");
        }
    }
}
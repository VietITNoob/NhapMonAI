package ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import Config.GAConfig;
import Manage.DataManager;
import models.Course;
import models.CourseClass;
import models.Student;

/**
 * GeneticAlgorithm.java
 * Lớp điều khiển chính, thực thi các vòng lặp tiến hóa,
 * chọn lọc, lai ghép và đột biến.
 */
public class GeneticAlgorithm {
    
    private static final Random random = new Random();
    private final DataManager data;
    private List<Chromosome> population;

    public GeneticAlgorithm(DataManager data) {
        this.data = data;
        this.population = new ArrayList<>(GAConfig.POPULATION_SIZE);
    }

    /**
     * Chạy toàn bộ quá trình tiến hóa
     */
    public Chromosome run() {
        System.out.println("Bắt đầu tiến hóa...");
        
        // 1. Khởi tạo quần thể
        initializePopulation();

        Chromosome bestChromosome = null;

        for (int gen = 0; gen < GAConfig.MAX_GENERATIONS; gen++) {
            // 2. Đánh giá quần thể (Tính Fitness)
            calculatePopulationFitness();
            
            // Lấy cá thể tốt nhất của thế hệ này
            bestChromosome = getBestChromosome();

            // In ra thông báo
            if (gen % 50 == 0) {
                System.out.println("Thế hệ " + gen + " | Fitness tốt nhất: " + bestChromosome.getFitness());
            }

            // Điều kiện dừng: Nếu tìm thấy giải pháp hoàn hảo (không vi phạm ràng buộc cứng)
            if (bestChromosome.getFitness() >= 0) {
                System.out.println("!!! Đã tìm thấy giải pháp hợp lệ ở thế hệ " + gen + " !!!");
                break;
            }

            // 3. Tạo thế hệ tiếp theo
            population = evolvePopulation();
        }
        
        System.out.println("Kết thúc tiến hóa.");
        // Tính lại fitness lần cuối cho quần thể cuối cùng
        calculatePopulationFitness();
        return getBestChromosome();
    }

    /**
     * Bước 1: Khởi tạo quần thể ban đầu
     */
    private void initializePopulation() {
        for (int i = 0; i < GAConfig.POPULATION_SIZE; i++) {
            population.add(new Chromosome(data));
        }
    }

    /**
     * Tính toán Fitness cho tất cả cá thể
     */
    private void calculatePopulationFitness() {
        for (Chromosome c : population) {
            c.getFitness(); // Hàm get sẽ tự tính nếu fitness = -1
        }
    }

    /**
     * Lấy cá thể có Fitness cao nhất
     */
    public Chromosome getBestChromosome() {
        return Collections.max(population, Comparator.comparing(Chromosome::getFitness));
    }

    /**
     * Quá trình tạo ra thế hệ mới
     */
    private List<Chromosome> evolvePopulation() {
        List<Chromosome> newPopulation = new ArrayList<>(GAConfig.POPULATION_SIZE);

        // 1. Elitism: Giữ lại 1 cá thể tốt nhất
        newPopulation.add(getBestChromosome().clone());

        // 2. Điền đầy quần thể mới
        while (newPopulation.size() < GAConfig.POPULATION_SIZE) {
            // 2.1 Chọn lọc (Selection)
            Chromosome parent1 = tournamentSelection();
            Chromosome parent2 = tournamentSelection();

            // 2.2 Lai ghép (Crossover)
            Chromosome child = uniformCrossover(parent1, parent2);

            // 2.3 Đột biến (Mutation)
            mutate(child);

            newPopulation.add(child);
        }
        return newPopulation;
    }

    /**
     * Bước 4: CHỌN LỌC GIẢI ĐẤU (Tournament Selection)
     */
    private Chromosome tournamentSelection() {
        List<Chromosome> tournament = new ArrayList<>(GAConfig.TOURNAMENT_SIZE);
        // Chọn ngẫu nhiên K cá thể
        for (int i = 0; i < GAConfig.TOURNAMENT_SIZE; i++) {
            int randomIndex = random.nextInt(GAConfig.POPULATION_SIZE);
            tournament.add(population.get(randomIndex));
        }
        // Trả về cá thể tốt nhất trong giải đấu
        return Collections.max(tournament, Comparator.comparing(Chromosome::getFitness));
    }

    /**
     * Bước 5: LAI GHÉP ĐỒNG ĐỀU (Uniform Crossover)
     */
    private Chromosome uniformCrossover(Chromosome parent1, Chromosome parent2) {
        Chromosome child = new Chromosome(data, true); // Tạo NST rỗng

        // Duyệt qua từng "gen" (lịch của từng SV)
        for (Student s : data.getAllStudents()) {
            // Tung đồng xu 50%
            if (Math.random() > 0.5) {
                // Lấy gen (lịch học) từ Bố
                child.setStudentSchedule(s, new ArrayList<>(parent1.getStudentSchedule(s)));
            } else {
                // Lấy gen (lịch học) từ Mẹ
                child.setStudentSchedule(s, new ArrayList<>(parent2.getStudentSchedule(s)));
            }
        }
        return child;
    }

    /**
     * Bước 6: ĐỘT BIẾN (Mutation)
     */
    private void mutate(Chromosome chromosome) {
        // Duyệt qua lịch của từng sinh viên
        for (Student s : data.getAllStudents()) {
            // Chỉ đột biến với một xác suất nhỏ
            if (Math.random() < GAConfig.MUTATION_RATE) {
                
                List<CourseClass> studentClasses = chromosome.getStudentSchedule(s);
                if (studentClasses.isEmpty()) continue;

                // Chọn ngẫu nhiên 1 môn học trong lịch để đột biến
                int classIndexToMutate = random.nextInt(studentClasses.size());
                CourseClass oldClass = studentClasses.get(classIndexToMutate);
                Course courseToMutate = oldClass.getCourse();

                // Lấy các lựa chọn lớp học phần khác cho môn đó
                List<CourseClass> options = data.getClassesForCourse(courseToMutate);
                if (options.size() > 1) { // Chỉ đột biến nếu có lựa chọn khác
                    // Chọn 1 lớp mới ngẫu nhiên
                    CourseClass newClass = options.get(random.nextInt(options.size()));
                    
                    if (!newClass.equals(oldClass)) {
                        studentClasses.set(classIndexToMutate, newClass);
                    }
                }
            }
        }
    }
}
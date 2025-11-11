package Config;

/**
 * GAConfig.java Chứa tất cả các hằng số cấu hình cho Thuật toán Di truyền.
 */
public final class GAConfig {

	/** Kích thước quần thể (số lượng lịch học trong mỗi thế hệ) */
	public static final int POPULATION_SIZE = 100;

	/** Số thế hệ tối đa */
	public static final int MAX_GENERATIONS = 1000;

	/** Tỷ lệ đột biến (1%) */
	public static final double MUTATION_RATE = 0.01;

	/** Kích thước của "giải đấu" để chọn lọc (Tournament Selection) */
	public static final int TOURNAMENT_SIZE = 5;

	/** Trọng số phạt cho vi phạm RÀNG BUỘC CỨNG (rất lớn) */
	public static final int HARD_CONSTRAINT_PENALTY = -1000;

	/** Trọng số thưởng cho RÀNG BUỘC MỀM (nhỏ) */
	public static final int SOFT_CONSTRAINT_REWARD = 10;
}

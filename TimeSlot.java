package models;

/**
 * TimeSlot.java Đại diện cho một Khung giờ học (ví dụ: Thứ 2, 7:00 - 9:00)
 */
public class TimeSlot {
	private final String day; // "Mon", "Tue", "Wed", ...
	private final int startHour; // 7, 9, 13, 15
	private final int endHour;

	public TimeSlot(String day, int startHour, int endHour) {
		this.day = day;
		this.startHour = startHour;
		this.endHour = endHour;
	}

	/**
	 * Hàm kiểm tra xem 2 khung giờ có bị trùng nhau hay không
	 */
	public boolean overlaps(TimeSlot other) {
		if (!this.day.equals(other.day)) {
			return false; // Khác ngày thì không trùng
		}
		// Trùng nếu (StartA < EndB) và (EndA > StartB)
		return this.startHour < other.endHour && this.endHour > other.startHour;
	}

	@Override
	public String toString() {
		return day + " [" + startHour + "h-" + endHour + "h]";
	}
}
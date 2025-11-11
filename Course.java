package models;

/**
 * Course.java Đại diện cho một Môn học (ví dụ: "Toán cao cấp 1")
 */
public class Course {
	private final String courseId;
	private final String name;

	public Course(String courseId, String name) {
		this.courseId = courseId;
		this.name = name;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return courseId.equals(((Course) o).courseId);
	}

	@Override
	public int hashCode() {
		return courseId.hashCode();
	}
}
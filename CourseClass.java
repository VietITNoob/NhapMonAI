package models;

/**
 * CourseClass.java
 * Đại diện cho một Lớp học phần cụ thể (ví dụ: "Toán_A")
 */
public class CourseClass {
    private final String classId;
    private final Course course; // Thuộc môn học nào
    private final TimeSlot timeSlot;
    private final int capacity; // Sĩ số tối đa

    public CourseClass(String classId, Course course, TimeSlot timeSlot, int capacity) {
        this.classId = classId;
        this.course = course;
        this.timeSlot = timeSlot;
        this.capacity = capacity;
    }

    public String getClassId() { return classId; }
    public Course getCourse() { return course; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public int getCapacity() { return capacity; }
    
    @Override
    public String toString() { return classId + " (" + course.getName() + " @ " + timeSlot + ")"; }
}
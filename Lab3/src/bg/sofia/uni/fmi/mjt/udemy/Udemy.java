package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

import java.util.Arrays;
import java.util.Objects;

public class Udemy implements LearningPlatform{

    private final Account[] accounts;
    private final Course[] courses;
    public Udemy(Account[] accounts, Course[] courses){
        this.accounts = accounts;
        this.courses = courses;
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        if(name == null || name.isBlank())
            throw new IllegalArgumentException();
        for(Course c:courses){
            if(name.equals(c.getName()))
                return c;
        }
        throw new CourseNotFoundException("The searched course was not found!");
    }

    @Override
    public Course[] findByKeyword(String keyword) {
        if(keyword == null || keyword.isBlank() || !isKeyword(keyword))
            throw new IllegalArgumentException();

        Course[] result = new Course[courses.length];
        int iter = 0;
        for (Course c:courses){
            if(c.getName().contains(keyword) || c.getDescription().contains(keyword)){
                result[iter] = c;
                iter++;
            }
        }
        return Arrays.stream(result).filter(Objects::nonNull).toArray(Course[]::new);
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        if(category == null)
            throw new IllegalArgumentException();
        Course[] result = new Course[courses.length];
        int iter = 0;
        for(Course c: courses){
            if(c.getCategory().equals(category))
                result[iter++] = c;
        }
        return Arrays.stream(result).filter(Objects::nonNull).toArray(Course[]::new);
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        if(name == null || name.isBlank())
            throw new IllegalArgumentException();
        for(Account a:accounts){
            if(a.getUsername().equals(name)){
                return a;
            }
        }
        throw new AccountNotFoundException("The searched account was not found!");
    }

    @Override
    public Course getLongestCourse() {
        if(courses.length == 0)
            return null;
        int resIndex = 0;
        CourseDuration longest = courses[0].getTotalTime();
        int iter = 0;
        for (Course c:courses){
            if(CourseDuration.compare(c.getTotalTime(),longest) == 1) {
                resIndex = iter;
                longest = c.getTotalTime();
            }
            iter++;
        }
        return courses[resIndex];
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        if(courses.length == 0)
            return null;
        if(category == null)
            throw new IllegalArgumentException();

        int resIndex = 0;
        double cheapest = courses[0].getPrice();
        int iter = 0;
        for (Course c:courses){
            if(Double.compare(c.getPrice(),cheapest) < 0) {
                resIndex = iter;
                cheapest = c.getPrice();
            }
            iter++;
        }
        return courses[resIndex];
    }

    private boolean isKeyword(String keyword){
        for(char c:keyword.toCharArray()){
            if(!Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }
}
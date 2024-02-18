package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

import java.util.Arrays;

public class EducationalAccount extends AccountBase{

    static final int CONSECUTIVE_GRADES_FOR_PROMOTION = 5;

    private double[] grades;
    private int gradesCount = 0;

    private int completedSinceDiscount = 0;
    public EducationalAccount(String username, double balance) {
        super(username, balance);
        grades = new double[MAX_COURSES_COUNT];
        type = AccountType.EDUCATION;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if(coursesCount == MAX_COURSES_COUNT)
            throw new MaxCourseCapacityReachedException("Max course capacity reached!");


        for (Course c:ownedCourses){
            if(c == null)
                break;
            if (c.equals(course))
                throw new CourseAlreadyPurchasedException("Course was already purchased!");
        }

        double cost = 0;
        boolean isTherePromo = areGradesEnoughForDiscount();
        if(isTherePromo){
            cost = course.getPrice() - course.getPrice()* type.getDiscount();
        }else{
            cost = course.getPrice();
        }



        if(balance < cost)
            throw new InsufficientBalanceException("Not enough money in the balance!");

        if (isTherePromo){
            completedSinceDiscount = 0;
        }

        balance-=cost;
        course.purchase();
        ownedCourses[coursesCount++] = course;

    }

    public void completeCourse(Course course, double grade)
        throws CourseNotPurchasedException, CourseNotCompletedException{
        if(grade > 6.00 || grade < 2.00 || course == null)
            throw new IllegalArgumentException();

        int courseIndex = -1;
        for(int i = 0;i<coursesCount;i++){
            if(ownedCourses[i].equals(course)){
                courseIndex = i;
            }
        }
        if(courseIndex == -1)
            throw new CourseNotPurchasedException("Not purchased course!");

        if(!ownedCourses[courseIndex].isCompleted())
            throw new CourseNotCompletedException("Not completed course!");

        grades[gradesCount++] = grade;
        completedSinceDiscount++;

    }

    private boolean areGradesEnoughForDiscount(){
        if(completedSinceDiscount < CONSECUTIVE_GRADES_FOR_PROMOTION)
            return false;

        double sum = 0;
        for(int i = gradesCount-1;i > gradesCount-CONSECUTIVE_GRADES_FOR_PROMOTION-1;i--){
            sum+=grades[i];
        }
        return Double.compare(sum/CONSECUTIVE_GRADES_FOR_PROMOTION,4.50) >= 0;
    }
}
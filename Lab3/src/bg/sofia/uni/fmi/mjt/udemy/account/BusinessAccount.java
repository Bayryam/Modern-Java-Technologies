package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

import java.util.Arrays;

public class BusinessAccount extends AccountBase{
    private final Category[] allowedCategories;
    public BusinessAccount(String username, double balance, Category[] allowedCategories){
        super(username,balance);
        this.allowedCategories = allowedCategories;
        type = AccountType.BUSINESS;
    }


    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException,
        CourseAlreadyPurchasedException,
        MaxCourseCapacityReachedException {
        if(coursesCount == MAX_COURSES_COUNT)
            throw new MaxCourseCapacityReachedException("Max capacity reached!");

        boolean isAllowed = false;
        for (Category allowedCategory : allowedCategories) {
            if (allowedCategory.equals(course.getCategory())) {
                isAllowed = true;
                break;
            }
        }
        if(!isAllowed)
            throw new IllegalArgumentException();
        if(balance < (course.getPrice()- course.getPrice()*type.getDiscount()))
            throw new InsufficientBalanceException("You do not nave enough money!");

        for (Course c:ownedCourses){
            if(c == null)
                break;
            if (c.equals(course))
                throw new CourseAlreadyPurchasedException("You have already purchased this course!");
        }

        balance-=(course.getPrice() - course.getPrice()*type.getDiscount());
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
            throw new CourseNotPurchasedException("You do not possess this course!");

        if(!ownedCourses[courseIndex].isCompleted())
            throw new CourseNotCompletedException("You have not completed this course!");

    }
}

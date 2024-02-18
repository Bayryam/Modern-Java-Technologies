package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public class StandardAccount extends AccountBase{
    public StandardAccount(String username, double balance) {
        super(username, balance);
        type = AccountType.STANDARD;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if(coursesCount == MAX_COURSES_COUNT)
            throw new MaxCourseCapacityReachedException("Max course capacity reached!");

        if(balance < course.getPrice())
            throw new InsufficientBalanceException("Not enough money in the balance!");

        for (Course c:ownedCourses){
            if(c == null)
                break;
            if (c.equals(course))
                throw new CourseAlreadyPurchasedException("Already purchased course!");
        }

        balance-=course.getPrice();
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
            throw new CourseNotPurchasedException("Course not purchased!");

        if(!ownedCourses[courseIndex].isCompleted())
            throw new CourseNotCompletedException("Not completed course!");

    }
}
package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public abstract class AccountBase implements Account{

    protected static final int MAX_COURSES_COUNT = 100;

    protected final String username;

    protected AccountType type;
    protected double balance;
    protected Course[] ownedCourses;
    protected int coursesCount = 0;

    public AccountBase(String username, double balance){
        this.username = username;
        this.balance = balance;
        ownedCourses = new Course[MAX_COURSES_COUNT];
    }

    public String getUsername(){
        return username;
    }
    public void addToBalance(double amount){
        if(amount < 0)
            throw new IllegalArgumentException();
        balance+=amount;
    }
    public double getBalance(){
        return balance;
    }
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete)
        throws CourseNotPurchasedException, ResourceNotFoundException{
        if(course == null || resourcesToComplete == null)
            throw new IllegalArgumentException();
        int courseIndex = -1;
        for(int i = 0;i<coursesCount;i++){
            if(ownedCourses[i].equals(course)){
                courseIndex = i;
                break;
            }
        }
        if(courseIndex == -1)
            throw new CourseNotPurchasedException("You do not possess this course!");
        for(Resource r:resourcesToComplete){
            ownedCourses[courseIndex].completeResource(r);
        }
    }
    public Course getLeastCompletedCourse(){
        if(coursesCount == 0)
            return null;
        int leastCompleted = 100;
        int resIndex = -1;
        for (int i = 0;i<coursesCount;i++){
            if(ownedCourses[i].getCompletionPercentage()<leastCompleted){
                resIndex = i;
                leastCompleted = ownedCourses[i].getCompletionPercentage();
            }
        }
        return ownedCourses[resIndex];
    }

}
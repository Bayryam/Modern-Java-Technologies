package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

public class Course implements Completable, Purchasable {

    private final String name;
    private final String description;
    private final double price;
    private final Resource[] content;
    private final Category category;
    private boolean isCoursePurchased = false;

    public Course(String name, String description, double price, Resource[] content, Category category){
        this.name = name;
        this.description = description;
        this.price = price;
        this.content = content;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public Resource[] getContent() {
        return content;
    }

    public CourseDuration getTotalTime() {
        return CourseDuration.of(content);
    }

    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if(resourceToComplete == null)
            throw new IllegalArgumentException();
        for (Resource r:content){
            if(r.equals(resourceToComplete)){
                r.complete();
                return;
            }
        }
        throw new ResourceNotFoundException("The wanted resource was not found!");
    }

    @Override
    public boolean isCompleted() {
        for (Resource r:content){
            if(!r.isCompleted()){
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCompletionPercentage() {
        double coursesCount = 0;
        double completedCourses = 0;
        for (Resource r:content){
            coursesCount++;
            if(r.isCompleted()){
                completedCourses++;
            }
        }
        return (int)Math.round((completedCourses/coursesCount)*100);
    }

    @Override
    public void purchase() {
        isCoursePurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isCoursePurchased;
    }

}

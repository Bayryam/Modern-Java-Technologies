package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

public class Resource implements Completable{

    private final String name;
    private final ResourceDuration duration;
    private boolean isResourceCompleted = false;
    public Resource(String name, ResourceDuration duration){
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public ResourceDuration getDuration() {
        return duration;
    }

    public void complete() {
        isResourceCompleted = true;
    }

    @Override
    public boolean isCompleted() {
        return isResourceCompleted;
    }

    @Override
    public int getCompletionPercentage() {
        return isResourceCompleted ? 100 : 0;
    }
}
 
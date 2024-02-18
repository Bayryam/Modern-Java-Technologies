package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {

    public static CourseDuration of(Resource[] content){

        int minutes = 0;
        for(Resource r: content){
            minutes += r.getDuration().minutes();
        }
        return new CourseDuration(minutes/60, minutes%60);
    }

    public CourseDuration{
        if(hours < 0 || hours > 24)
            throw new IllegalArgumentException();
        if(minutes < 0 || minutes > 60)
            throw new IllegalArgumentException();
    }

    public static int compare(CourseDuration lhs, CourseDuration rhs){
        if(lhs.hours > rhs.hours)
        {
            return 1;
        }
        else if(lhs.hours < rhs.hours) {
            return -1;
        }else{
            if(lhs.minutes >rhs.minutes) {
                return 1;
            }else if(lhs.minutes<rhs.minutes){
                return -1;
            }else{
                return 0;
            }
        }

    }
}
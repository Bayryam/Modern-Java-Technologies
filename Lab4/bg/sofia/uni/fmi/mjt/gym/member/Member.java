package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Member implements GymMember, Comparable<Member>  {

    private final Address address;
    private final String name;
    private final int age;
    private final String personalIdNumber;
    private final Gender gender;
    private final Map<DayOfWeek, Workout> trainingProgram;

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {

        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        trainingProgram = new HashMap<>();

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPersonalIdNumber() {
        return personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return Map.copyOf(trainingProgram);
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {

        if (day == null) {
            throw new IllegalArgumentException("Day argument is null!");
        }

        if (workout == null) {
            throw new IllegalArgumentException("Workout argument is null!");
        }

        trainingProgram.put(day, workout);

    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {

        if (exerciseName == null) {
            throw new IllegalArgumentException("ExerciseName argument is null!");
        }

        if (exerciseName.isEmpty()) {
            throw new IllegalArgumentException("ExerciseName argument is empty!");
        }

        Set<Map.Entry<DayOfWeek, Workout>> entries = trainingProgram.entrySet();
        Collection<DayOfWeek> result = new HashSet<>();

        for (Map.Entry<DayOfWeek, Workout> entry : entries) {
            String lastExerciseName = entry.getValue().exercises().getLast().name();

            if (lastExerciseName.equals(exerciseName)) {
                result.add(entry.getKey());
            }
        }

        return result;

    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {

        if (day == null) {
            throw new IllegalArgumentException("Day argument is null!");
        }

        if (exercise == null) {
            throw new IllegalArgumentException("Exercise argument is null!");
        }

        if (trainingProgram.get(day) == null) {
            throw new DayOffException("Unable to add exercise on day off!");
        }

        trainingProgram.get(day).exercises().add(exercise);

    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) {

        if (day == null) {
            throw new IllegalArgumentException("Day argument is null!");
        }

        if (exercises == null) {
            throw new IllegalArgumentException("Exercises argument is null!");
        }

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("Exercises is empty collection!");
        }

        if (trainingProgram.get(day) == null) {
            throw new DayOffException("Unable to add exercise on day off!");
        }

        trainingProgram.get(day).exercises().addAll(exercises);

    }

    @Override
    public int compareTo(Member o) {
        return personalIdNumber.compareTo(o.personalIdNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(personalIdNumber, member.personalIdNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }
}

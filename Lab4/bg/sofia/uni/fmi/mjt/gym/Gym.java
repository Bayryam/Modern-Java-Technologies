package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.comparators.MemberByNameComparator;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.SortedSet;

public class Gym implements GymAPI {

    private final int capacity;
    private final Address address;
    private SortedSet<GymMember> members;

    public Gym(int capacity, Address address) {

        this.capacity = capacity;
        this.address = address;
        members = new TreeSet<>();

    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return Collections.unmodifiableSortedSet(members);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {

        TreeSet<GymMember> result = new TreeSet<>(new MemberByNameComparator());
        result.addAll(members);
        return Collections.unmodifiableSortedSet(result);

    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {

        TreeSet<GymMember> result = new TreeSet<>(new MemberByProximityToGymComparator());
        result.addAll(members);
        return Collections.unmodifiableSortedSet(result);

    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {

        if (capacity == members.size()) {
            throw new GymCapacityExceededException("Gym capacity is not enough!");
        }

        if (member == null) {
            throw new IllegalArgumentException("Member argument is null!");
        }

        members.add(member);

    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {

        if (members == null) {
            throw new IllegalArgumentException("Members argument is null!");
        }

        if (members.isEmpty()) {
            throw new IllegalArgumentException("Members is empty!");
        }

        if (capacity < this.members.size() + members.size()) {
            throw new GymCapacityExceededException("Gym capacity is not enough!");
        }

        this.members.addAll(members);

    }

    @Override
    public boolean isMember(GymMember member) {

        if (member == null) {
            throw new IllegalArgumentException("Member argument is null!");
        }
        return members.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {

        if (day == null) {
            throw new IllegalArgumentException("Day argument is null!");
        }

        if (exerciseName == null) {
            throw new IllegalArgumentException("ExerciseName argument is null!");
        }

        if (exerciseName.isEmpty()) {
            throw new IllegalArgumentException("ExerciseName is empty!");
        }

        for (GymMember member : members) {
            if (doesMemberHasExerciseOnThisDay(member, exerciseName, day)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("ExerciseName argument is null!");
        }
        if (exerciseName.isEmpty()) {
            throw new IllegalArgumentException("ExerciseName is empty!");
        }

        Map<DayOfWeek, List<String>> result = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            List<String> names = new ArrayList<>();
            for (GymMember member : members) {
                if (member.getTrainingProgram().get(day) == null) {
                    continue;
                }
                if (doesMemberHasExerciseOnThisDay(member, exerciseName, day)) {
                    names.add(member.getName());
                }
            }
            if (names.isEmpty()) {
                continue;
            }
            result.put(day, names);
        }
        
        return Collections.unmodifiableMap(result);
    }

    private boolean doesMemberHasExerciseOnThisDay(GymMember member, String exerciseName, DayOfWeek day) {

        Collection<Exercise> exercisesInWorkout = member.getTrainingProgram().get(day).exercises();

        for (Exercise ex : exercisesInWorkout) {
            if (ex.name().equals(exerciseName)) {
                return true;
            }
        }

        return false;

    }

    private class MemberByProximityToGymComparator implements Comparator<GymMember> {

        @Override
        public int compare(GymMember o1, GymMember o2) {

            double firstDist = o1.getAddress().getDistanceTo(address);
            double secondDist = o2.getAddress().getDistanceTo(address);
            return Double.compare(firstDist, secondDist);

        }

    }

}




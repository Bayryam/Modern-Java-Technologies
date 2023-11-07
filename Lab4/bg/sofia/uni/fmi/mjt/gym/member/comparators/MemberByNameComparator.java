package bg.sofia.uni.fmi.mjt.gym.member.comparators;

import bg.sofia.uni.fmi.mjt.gym.member.GymMember;

import java.util.Comparator;

public class MemberByNameComparator implements Comparator<GymMember> {

    @Override
    public int compare(GymMember first, GymMember second) {

        String firstName = first.getName();
        String secondName = second.getName();

        return firstName.compareTo(secondName);

    }

}

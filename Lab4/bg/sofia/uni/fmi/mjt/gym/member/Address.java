package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {

    public double getDistanceTo(Address other) {
        return Math.sqrt((other.longitude - longitude) * (other.longitude - longitude)
                + (other.latitude - latitude) * (other.latitude - latitude));
    }

}

package bg.sofia.uni.fmi.mjt.cooking.dto.type;

public enum MealType {
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH("lunch"),
    DINNER("dinner"),
    SNACK("snack"),
    TEATIME("teatime"),
    UNKNOWN("");

    final String value;

    MealType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
package bg.sofia.uni.fmi.mjt.cooking.dto;

import java.util.List;

public record Recipe(String label, List<String> healthLabels, float totalWeight, List<String> cuisineType,
                     List<String> mealType, List<String> dishType, List<String> ingredientLines) {
}

package bg.sofia.uni.fmi.mjt.cooking.dto;

import java.util.List;

public record Recipes(List<Recipe> recipes, String nextPageUrl) {
}
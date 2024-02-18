package bg.sofia.uni.fmi.mjt.cooking.api;

import bg.sofia.uni.fmi.mjt.cooking.dto.Link;
import bg.sofia.uni.fmi.mjt.cooking.dto.RecipeWrapper;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public record RecipesResponse(@SerializedName("_links") Link link,
                              @SerializedName("hits") List<RecipeWrapper> wrappedRecipes) {
}
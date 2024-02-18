package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.api.RecipeRequest;
import bg.sofia.uni.fmi.mjt.cooking.exception.DeniedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipe;

import java.util.List;

public interface EdamamApiClient {
    List<Recipe> execute(RecipeRequest request) throws RecipesNotRetrievedException,
        DeniedAccessException;
}

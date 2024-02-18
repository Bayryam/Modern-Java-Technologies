package bg.sofia.uni.fmi.mjt.cooking.api;

import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.type.MealType;
import bg.sofia.uni.fmi.mjt.cooking.dto.type.VisibilityType;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecipeRequestTest {

    @Test
    void testUriNoKeywords() throws RecipesNotRetrievedException {
        RecipeRequest req = RecipeRequest.newRequest().setMealType(MealType.BREAKFAST).build();
        URI uri = req.uri();
        assertFalse(uri.toString().contains("q="), "Uri should not contain q= in it!");
    }

    @Test
    void testUriNoHealthLabels() throws RecipesNotRetrievedException {
        RecipeRequest req = RecipeRequest.newRequest("chicken").build();
        URI uri = req.uri();
        assertFalse(uri.toString().contains("health="), "Uri should not contain health= in it!");
    }

    @Test
    void testUriNoMealType() throws RecipesNotRetrievedException {
        RecipeRequest req =
            RecipeRequest.newRequest("chicken").setHealth("diet").setVisibilityType(VisibilityType.PUBLIC).build();
        URI uri = req.uri();
        assertFalse(uri.toString().contains("mealType="), "Uri should not contain mealType= in it!");
    }

    @Test
    void testUriMealTypeArgumentNull() {
        assertThrows(IllegalArgumentException.class,
            () -> RecipeRequest.newRequest().setMealType(null).build(),
            "IllegalArgumentException was not thrown!");
    }

    @Test
    void testUriKeywordsArgumentNull() {
        assertThrows(IllegalArgumentException.class,
            () -> RecipeRequest.newRequest(null).build(),
            "IllegalArgumentException was not thrown!");
    }

    @Test
    void testUriHealthArgumentNull() {
        assertThrows(IllegalArgumentException.class,
            () -> RecipeRequest.newRequest().setHealth(null).build(),
            "IllegalArgumentException was not thrown!");
    }

    @Test
    void testUriVisibilityTypeArgumentNull() {
        assertThrows(IllegalArgumentException.class,
            () -> RecipeRequest.newRequest().setVisibilityType(null).build(),
            "IllegalArgumentException was not thrown!");
    }

}

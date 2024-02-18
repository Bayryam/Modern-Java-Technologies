package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.api.RecipeHttpClient;
import bg.sofia.uni.fmi.mjt.cooking.api.RecipeRequest;
import bg.sofia.uni.fmi.mjt.cooking.collection.KeyValuePairCollection;
import bg.sofia.uni.fmi.mjt.cooking.exception.DeniedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipe;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EdamamClientTest {


    private final RecipeHttpClient recipeHttpClient = mock();
    private final KeyValuePairCollection<RecipeRequest, List<Recipe>> col = mock();
    private final EdamamClient client = new EdamamClient(recipeHttpClient, col);

    final Recipe recipe = new Recipe(
        "healthy",
        List.of("Vegan", "Vegetarian"),
        396.8933f,
        List.of("world"),
        List.of("lunch/dinner"),
        List.of("alcohol cocktail"),
        List.of("1 (12 ounce) can or bottle Bud Light Lime, chilled", "2 ounces St. Germain elderflower liqueur"));

    @Test
    void testExecuteAlreadyAnsweredRequest() throws RecipesNotRetrievedException, DeniedAccessException {
        when(col.get(any())).thenReturn(List.of(recipe));
        List<Recipe> result = client.execute(RecipeRequest.newRequest().build());
        assertEquals(1, result.size(),
            String.format("The list of recipes should have size 1, but it was: %d", result.size()));
        assertEquals(List.of(recipe), result,
            "There is a difference between the result and the expected recipes!");
        verify(recipeHttpClient, never()).getRecipes(any());
        verify(col, never()).put(any(), any());
    }

    @Test
    void testExecuteSinglePageAnswer() throws RecipesNotRetrievedException, DeniedAccessException {
        when(recipeHttpClient.getRecipes(any())).thenReturn(
            new Recipes(List.of(recipe), null));
        assertEquals(List.of(recipe), client.execute(RecipeRequest.newRequest().build()),
            "There is a difference between the result and the expected recipes!");
        verify(recipeHttpClient, times(1)).getRecipes(any());
        verify(col, times(1)).put(any(), any());
    }

    @Test
    void testExecuteSingleMultiplePageAnswer() throws RecipesNotRetrievedException, DeniedAccessException {
        when(recipeHttpClient.getRecipes(any())).thenReturn(
            new Recipes(List.of(recipe), "https://edamam.com?next-page"));
        assertEquals(List.of(recipe, recipe), client.execute(RecipeRequest.newRequest().build()),
            "There is a difference between the result and the expected recipes!");
        verify(recipeHttpClient, times(2)).getRecipes(any());
        verify(col, times(1)).put(any(), any());
    }

    @Test
    void testExecuteThrowsRecipesNotRetrievedExceptionUriSyntaxError()
        throws DeniedAccessException, RecipesNotRetrievedException {
        when(recipeHttpClient.getRecipes(any())).thenReturn(
            new Recipes(List.of(recipe), "This is not a valid URI"));
        assertThrows(RecipesNotRetrievedException.class, () -> client.execute(RecipeRequest.newRequest().build()),
            "RecipesNotRetrievedException was not thrown!");
        verify(recipeHttpClient, times(1)).getRecipes(any());
        verify(col, never()).put(any(), any());
    }

    @Test
    void testExecuteThrowsDeniedAccessException() throws RecipesNotRetrievedException, DeniedAccessException {
        when(recipeHttpClient.getRecipes(any())).thenThrow(DeniedAccessException.class);
        assertThrows(DeniedAccessException.class, () -> client.execute(RecipeRequest.newRequest().build()),
            "DeniedAccessException was not thrown!");
        verify(col, never()).put(any(), any());
    }

    @Test
    void testExecuteThrowsIllegalAccessException() throws RecipesNotRetrievedException, DeniedAccessException {
        assertThrows(IllegalArgumentException.class, () -> client.execute(null),
            "IllegalArgumentException was not thrown!");
        verify(col, never()).put(any(), any());
        verify(recipeHttpClient, never()).getRecipes(any());
    }
}

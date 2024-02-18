package bg.sofia.uni.fmi.mjt.cooking.api;

import bg.sofia.uni.fmi.mjt.cooking.exception.DeniedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecipeHttpClientTest {
    private static final String RECIPE_BODY = """
                {
                    "from": 1,
                    "to": 1,
                    "count": 1,
                    "_links": {
                        "next": {
                            "href": "https://api.edamam.com/api/recipes/v2?q=chicken&_cont=CHcVQBtNNQphDmgVQntAEX4BYldtBgEOQ2NDBWIVYVNxBgMAUXlSBGoTMVxyBwAGSmxGVzcRNwNwUVAFFzdJUmITNwchBAAVLnlSVSBMPkd5AANK&pageSize=20&page=1&type=any&app_id=a6b0e22f",
                            "title": "Next page"
                        }
                    },
                    "hits": [
                        {
                            "recipe": {
                                "label": "Slow Cooker Chicken Vesuvio",
                                "healthLabels": [
                                    "Sugar-Conscious",
                                    "Gluten-Free",
                                    "Wheat-Free",
                                    "Egg-Free",
                                    "Peanut-Free",
                                    "Tree-Nut-Free",
                                    "Soy-Free",
                                    "Fish-Free",
                                    "Shellfish-Free",
                                    "Pork-Free",
                                    "Red-Meat-Free",
                                    "Crustacean-Free",
                                    "Celery-Free",
                                    "Mustard-Free",
                                    "Sesame-Free",
                                    "Lupine-Free",
                                    "Mollusk-Free"
                                ],
                                "ingredientLines": [
                                    "2 tablespoons olive oil",
                                    "8 chicken thighs on the bone skin on",
                                    "salt and pepper",
                                    "1 1/2 pounds potatoes cubed",
                                    "1 onion diced",
                                    "6 garlic cloves minced",
                                    "1/2 cup white wine",
                                    "1/2 cup chicken broth",
                                    "1 1/2 teaspoons dried oregano",
                                    "1 teaspoon dried thyme",
                                    "1 cup frozen peas",
                                    "1 tablespoon butter optional, but oh so good",
                                    "parsley for serving"
                                ],
                                "totalWeight": 2808.610742305183,
                                "mealType": [
                                    "lunch/dinner"
                                ],
                                "dishType": [
                                    "main course"
                                ]
                            },
                            "_links": {
                                "self": {
                                    "title": "Self",
                                    "href": "https://api.edamam.com/api/recipes/v2/1189be4a8cc55bc31c6
                                    bdba3af157afe?type=public&app_id=a6b0e22f"
                                }
                            }
                        }
                    ]
                }
        """;
    private static final int OK_STATUS_CODE = 200;
    private static final int AUTHORIZATION_NEEDED_ERROR_CODE = 401;
    private static final int BAD_REQUEST_ERROR_CODE = 400;
    private static final int NOT_PERMITTED_ERROR_CODE = 403;
    private final HttpClient httpClient = mock(HttpClient.class);
    private final RecipeHttpClient client = new RecipeHttpClient(httpClient, "key", "id");

    @Test
    void testGetRecipesNotYetAuthorizedUri()
        throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = mock();
        when(response.statusCode()).thenReturn(OK_STATUS_CODE);
        when(response.body()).thenReturn(RECIPE_BODY);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        URI notAuthorizedUri = new URI("https://edamam.com?autorized=false");
        assertDoesNotThrow(() -> client.getRecipes(notAuthorizedUri),
            "This call should not throw an exception!");
    }

    @Test
    void testGetRecipesAuthorizedUri()
        throws URISyntaxException, IOException, InterruptedException, RecipesNotRetrievedException,
        DeniedAccessException {
        HttpResponse<String> response = mock();
        when(response.statusCode()).thenReturn(OK_STATUS_CODE);
        when(response.body()).thenReturn(RECIPE_BODY);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        URI notAuthorizedUri = new URI("https://edamam.com?app_key=key&app_id=id");
        Recipes result = client.getRecipes(notAuthorizedUri);
        String nextPageUri =
            "https://api.edamam.com/api/recipes/v2?q=chicken&_cont=CHcVQBtNNQphDmgVQntAEX4BYldtBgEOQ2NDBWIVYVNxBgMAUXlSBGoTMVxyBwAGSmxGVzcRNwNwUVAFFzdJUmITNwchBAAVLnlSVSBMPkd5AANK&pageSize=20&page=1&type=any&app_id=a6b0e22f";
        assertEquals(nextPageUri, result.nextPageUrl(),
            String.format("Next page url should be %s but it is: %s !", nextPageUri, result.nextPageUrl()));
        assertEquals(1, result.recipes().size(),
            String.format("Recipes count should be 1 but it is: %d", result.recipes().size()));
        assertEquals("Slow Cooker Chicken Vesuvio", result.recipes().getFirst().label(),
            String.format("The label of the first recipe should be Slow Cooker Chicken Vesuvio but it is: %s",
                result.recipes().getFirst().label()));
    }

    @Test
    void testGetRecipesThrowsRecipesNotRetrievedExceptionClientInterrupted()
        throws URISyntaxException, IOException, InterruptedException {
        when(httpClient.send(any(), any())).thenThrow(InterruptedException.class);
        URI notAuthorizedUri = new URI("https://edamam.com?app_key=key&app_id=id");
        assertThrows(RecipesNotRetrievedException.class, () -> client.getRecipes(notAuthorizedUri),
            "This call should throw RecipesNotRetrievedException!");
    }

    @Test
    void testGetRecipesThrowsRecipesNotRetrievedExceptionClientIOException()
        throws URISyntaxException, IOException, InterruptedException {
        when(httpClient.send(any(), any())).thenThrow(IOException.class);
        URI notAuthorizedUri = new URI("https://edamam.com?app_key=key&app_id=id");
        assertThrows(RecipesNotRetrievedException.class, () -> client.getRecipes(notAuthorizedUri),
            "This call should throw RecipesNotRetrievedException!");
    }

    @Test
    void testGetRecipesThrowsDeniedAccessExceptionNotPermittedUserCode()
        throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = mock();
        when(response.statusCode()).thenReturn(NOT_PERMITTED_ERROR_CODE);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        URI notAuthorizedUri = new URI("https://edamam.com?app_key=key&app_id=id");
        assertThrows(DeniedAccessException.class, () -> client.getRecipes(notAuthorizedUri),
            "This call should throw DeniedAccessException!");
    }

    @Test
    void testGetRecipesThrowsDeniedAccessExceptionNoAuthorizationCode()
        throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = mock();
        when(response.statusCode()).thenReturn(AUTHORIZATION_NEEDED_ERROR_CODE);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        URI notAuthorizedUri = new URI("https://edamam.com?app_key=key&app_id=id");
        assertThrows(DeniedAccessException.class, () -> client.getRecipes(notAuthorizedUri),
            "This call should throw DeniedAccessException!");
    }

    @Test
    void testGetRecipesThrowsDeniedAccessExceptionBadRequestCode()
        throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = mock();
        when(response.statusCode()).thenReturn(BAD_REQUEST_ERROR_CODE);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        URI notAuthorizedUri = new URI("https://edamam.com?app_key=key&app_id=id");
        assertThrows(RecipesNotRetrievedException.class, () -> client.getRecipes(notAuthorizedUri),
            "RecipesNotRetrievedException");
    }

    @Test
    void testGetRecipesThrowsIllegalArgumentExceptionNullUri() {
        assertThrows(IllegalArgumentException.class, () -> client.getRecipes(null),
            "RecipesNotRetrievedException");
    }
}

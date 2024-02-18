package bg.sofia.uni.fmi.mjt.cooking.api;

import bg.sofia.uni.fmi.mjt.cooking.exception.DeniedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipe;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipes;
import bg.sofia.uni.fmi.mjt.cooking.dto.RecipeWrapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RecipeHttpClient {
    private static final int AUTHORIZATION_NEEDED_ERROR_CODE = 401;
    private static final int BAD_REQUEST_ERROR_CODE = 400;
    private static final int NOT_PERMITTED_ERROR_CODE = 403;
    private static final char PARAMS_CONCAT = '&';
    private static final String APP_KEY_PARAM = "app_key=";
    private static final String APP_ID_PARAM = "app_id=";

    private static final Gson GSON = new Gson();
    private final HttpClient httpClient;
    private final String appKey;
    private final String appId;

    public RecipeHttpClient(HttpClient httpClient, String apiKey, String apiId) {
        this.httpClient = httpClient;
        this.appKey = apiKey;
        this.appId = apiId;
    }

    private boolean isUriAuthorized(URI uri) {
        String stringURI = uri.toString();
        return stringURI.contains(APP_ID_PARAM) && stringURI.contains(APP_KEY_PARAM);
    }

    private URI addValidationsToUri(URI uri) {
        if (!isUriAuthorized(uri)) {
            uri = URI.create(uri + APP_KEY_PARAM + appKey + PARAMS_CONCAT + APP_ID_PARAM + appId);
        }
        return uri;
    }

    private void checkValidationOfRequest(int statusCode) throws DeniedAccessException, RecipesNotRetrievedException {
        if (statusCode == NOT_PERMITTED_ERROR_CODE || statusCode == AUTHORIZATION_NEEDED_ERROR_CODE) {
            throw new DeniedAccessException("You are unauthorized!");
        }
        if (statusCode == BAD_REQUEST_ERROR_CODE) {
            throw new RecipesNotRetrievedException("Bad request sent!");
        }
    }

    public Recipes getRecipes(URI uri) throws RecipesNotRetrievedException, DeniedAccessException {
        if (uri == null) {
            throw new IllegalArgumentException("Uri argument is null!");
        }
        HttpResponse<String> recipeResponse;
        uri = addValidationsToUri(uri);

        try {
            HttpRequest httpRequest =
                HttpRequest.newBuilder()
                    .uri(uri)
                    .build();
            recipeResponse = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            throw new RecipesNotRetrievedException("Could not retrieve recipes, the operation was interrupted!!",
                exception);
        } catch (IOException exception) {
            throw new RecipesNotRetrievedException("Couldn't send or receive information!", exception);
        }
        checkValidationOfRequest(recipeResponse.statusCode());

        RecipesResponse parsedResponse = GSON.fromJson(recipeResponse.body(), RecipesResponse.class);
        List<Recipe> recipes = parsedResponse.wrappedRecipes().stream().map(RecipeWrapper::recipe).toList();

        String nextPage = null;
        if (parsedResponse.link().next() != null) {
            nextPage = parsedResponse.link().next().href();
        }
        return new Recipes(recipes, nextPage);
    }
}
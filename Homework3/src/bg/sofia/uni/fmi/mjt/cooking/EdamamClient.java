package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.api.PageIterator;
import bg.sofia.uni.fmi.mjt.cooking.api.RecipeHttpClient;
import bg.sofia.uni.fmi.mjt.cooking.api.RecipeRequest;
import bg.sofia.uni.fmi.mjt.cooking.collection.KeyValuePairCollection;
import bg.sofia.uni.fmi.mjt.cooking.exception.DeniedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipe;
import bg.sofia.uni.fmi.mjt.cooking.dto.Recipes;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;

public class EdamamClient implements EdamamApiClient {
    static final int MAX_PAGES_TO_LOAD = 1;
    private final RecipeHttpClient client;
    private final KeyValuePairCollection<RecipeRequest, List<Recipe>> storage;

    public EdamamClient(RecipeHttpClient client,
                        KeyValuePairCollection<RecipeRequest, List<Recipe>> storage) {
        this.client = client;
        this.storage = storage;
    }

    @Override
    public List<Recipe> execute(RecipeRequest request) throws RecipesNotRetrievedException,
        DeniedAccessException {
        if (request == null) {
            throw new IllegalArgumentException("Request argument is null!");
        }

        if (storage.get(request) != null) {
            return storage.get(request);
        }

        Recipes initialRecipes = client.getRecipes(request.uri());
        List<Recipe> result = new ArrayList<>(initialRecipes.recipes());

        var iterator = new RecipesIterator(initialRecipes.nextPageUrl());
        while (iterator.hasNext()) {
            result.addAll(iterator.next());
        }

        storage.put(request, result);
        return result;
    }

    private class RecipesIterator implements PageIterator<List<Recipe>> {
        private String nextPage;
        private int pagesToLoad = MAX_PAGES_TO_LOAD;

        private RecipesIterator(String nextPage) {
            this.nextPage = nextPage;
        }

        @Override
        public boolean hasNext() {
            return nextPage != null && pagesToLoad > 0;
        }

        @Override
        public List<Recipe> next() throws RecipesNotRetrievedException, DeniedAccessException {
            try {
                Recipes res = client.getRecipes(new URI(nextPage));
                nextPage = res.nextPageUrl();
                pagesToLoad--;
                return res.recipes();
            } catch (URISyntaxException exception) {
                throw new RecipesNotRetrievedException("There was a syntax problem while iterating the recipes",
                    exception);
            }
        }
    }
}

package bg.sofia.uni.fmi.mjt.cooking.api;

import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;
import bg.sofia.uni.fmi.mjt.cooking.dto.type.MealType;
import bg.sofia.uni.fmi.mjt.cooking.dto.type.VisibilityType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RecipeRequest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2";
    private static final String KEYWORDS_PARAM = "q";
    private static final String MEAL_PARAM = "mealType";
    private static final String HEALTH_PARAM = "health";
    private static final String VISIBILITY_PARAM = "type";
    private static final String EMPTY_STRING = "";

    private final String keywords;
    private final MealType mealType;
    private final List<String> health;
    private final VisibilityType visibilityType;

    private RecipeRequest(RecipeRequestBuilder builder) {
        this.keywords = builder.getKeywords();
        this.mealType = builder.getMeal();
        this.health = builder.getHealth();
        this.visibilityType = builder.getVisibilityType();
    }

    public static RecipeRequestBuilder newRequest(String keywords) {
        return new RecipeRequestBuilder(keywords);
    }

    public static RecipeRequestBuilder newRequest() {
        return new RecipeRequestBuilder();
    }

    public URI uri() throws RecipesNotRetrievedException {
        try {
            return new URI(
                API_ENDPOINT_SCHEME,
                API_ENDPOINT_HOST,
                API_ENDPOINT_PATH,
                buildEndpointQuery(),
                null);
        } catch (URISyntaxException e) {
            throw new RecipesNotRetrievedException("Problem with the generating of the URI!", e);
        }
    }

    private void addQueryParamToBuilder(StringBuilder queryParam, String parameter, String value) {
        queryParam.append(parameter).append("=").append(value).append("&");
    }

    private String buildEndpointQuery() {
        StringBuilder query = new StringBuilder();
        addQueryParamToBuilder(query, VISIBILITY_PARAM, visibilityType.toString());

        if (!keywords.isEmpty()) {
            addQueryParamToBuilder(query, KEYWORDS_PARAM, keywords);
        }

        if (mealType != MealType.UNKNOWN) {
            addQueryParamToBuilder(query, MEAL_PARAM, mealType.toString());
        }

        if (health != null && !health.isEmpty()) {
            for (String healthLabel : health) {
                addQueryParamToBuilder(query, HEALTH_PARAM, healthLabel);
            }
        }

        return query.toString();
    }

    public static class RecipeRequestBuilder {
        private final String keywords;
        private MealType mealType = MealType.UNKNOWN;
        private List<String> health;
        private VisibilityType visibilityType = VisibilityType.ANY;

        public String getKeywords() {
            return keywords;
        }

        public MealType getMeal() {
            return mealType;
        }

        public List<String> getHealth() {
            return health;
        }

        public VisibilityType getVisibilityType() {
            return visibilityType;
        }

        private RecipeRequestBuilder(String keywords) {
            if (keywords == null) {
                throw new IllegalArgumentException("Keywords argument is null!");
            }
            this.keywords = keywords;
        }

        private RecipeRequestBuilder() {
            this.keywords = EMPTY_STRING;
        }

        public RecipeRequestBuilder setMealType(MealType mealType) {
            if (mealType == null) {
                throw new IllegalArgumentException("Meal type argument is null!");
            }
            this.mealType = mealType;
            return this;
        }

        public RecipeRequestBuilder setHealth(String... health) {
            if (health == null) {
                throw new IllegalArgumentException("Health argument is null!");
            }
            this.health = new ArrayList<>();
            this.health.addAll(List.of(health));
            return this;
        }

        public RecipeRequestBuilder setVisibilityType(VisibilityType visibilityType) {
            if (visibilityType == null) {
                throw new IllegalArgumentException("Visibility type argument is null!");
            }
            this.visibilityType = visibilityType;
            return this;
        }

        public RecipeRequest build() {
            return new
                RecipeRequest(this);
        }
    }
}
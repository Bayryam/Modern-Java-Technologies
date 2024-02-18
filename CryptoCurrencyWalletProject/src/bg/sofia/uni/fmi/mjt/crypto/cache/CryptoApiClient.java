package bg.sofia.uni.fmi.mjt.crypto.cache;

import bg.sofia.uni.fmi.mjt.crypto.exception.ApiClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CryptoApiClient {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "rest.coinapi.io";
    private static final String API_ENDPOINT_PATH = "/v1/assets";
    private static final String API_ENDPOINT_QUERY = "apikey=%s";
    private final HttpClient cryptoHttpClient;
    private final String apiKey;

    public CryptoApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.cryptoHttpClient = HttpClient.newHttpClient();
    }

    public CryptoApiClient(HttpClient httpClient, String apiKey) {
        this.cryptoHttpClient = httpClient;
        this.apiKey = apiKey;
    }

    public HttpResponse<String> sendRequest() throws ApiClientException {
        HttpResponse<String> response;
        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST,
                API_ENDPOINT_PATH, API_ENDPOINT_QUERY.formatted(apiKey), null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

            response = cryptoHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            throw new ApiClientException("There was a problem with write/read operations!", exception);
        } catch (InterruptedException exception) {
            throw new ApiClientException("The cache update was interrupted!", exception);
        } catch (URISyntaxException exception) {
            throw new ApiClientException("There was a syntax error while building the request URI!", exception);
        }
        return response;
    }
}

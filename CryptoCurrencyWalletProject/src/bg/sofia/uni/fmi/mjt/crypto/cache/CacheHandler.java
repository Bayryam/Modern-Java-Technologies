package bg.sofia.uni.fmi.mjt.crypto.cache;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.dto.EquityType;
import bg.sofia.uni.fmi.mjt.crypto.exception.ApiClientException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Set;
import java.util.stream.Collectors;

public class CacheHandler {
    private static final long CRYPTO_COUNT_LIMIT = 15;
    private static final Gson GSON = new Gson();
    private final CryptoApiClient cryptoApiClient;
    private final Cache<Equity> cache;
    private final int updateInterval;
    private LocalDateTime lastUpdateTime;

    public CacheHandler(Cache<Equity> cache, int updateInterval, CryptoApiClient cryptoApiClient) {
        this.cache = cache;
        this.updateInterval = updateInterval;
        this.cryptoApiClient = cryptoApiClient;
    }

    public void updateCache() throws ApiClientException {
        LocalDateTime now = LocalDateTime.now();
        if (lastUpdateTime != null) {
            long differenceInMinutes = ChronoUnit.MINUTES.between(lastUpdateTime, now);
            if (differenceInMinutes < updateInterval) {
                return;
            }
        }
        lastUpdateTime = now;

        HttpResponse<String> response = cryptoApiClient.sendRequest();

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            Type type = new TypeToken<Set<Equity>>() {
            }.getType();
            Set<Equity> equities = GSON.fromJson(response.body(), type);
            equities = equities.stream()
                .filter(equity -> equity.priceInUSD() != null)
                .filter(equity -> equity.isCryptoCurrency() == EquityType.CRYPTO.getTypeCode())
                .limit(CRYPTO_COUNT_LIMIT).collect(Collectors.toSet());
            cache.updateCache(equities);
        } else {
            throw new ApiClientException("The server responded with status code: " + response.statusCode());
        }
    }
}
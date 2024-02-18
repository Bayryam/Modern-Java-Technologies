package bg.sofia.uni.fmi.mjt.crypto.cache;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.ApiClientException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CacheHandlerTest {
    private static final String RESPONSE_BODY = """
         [
                 {
                     "asset_id": "BTC",
                     "name": "Bitcoin",
                     "type_is_crypto": 1,
                     "data_quote_start": "2014-02-24T00:00:00.0000000Z",
                     "data_quote_end": "2024-02-12T00:00:00.0000000Z",
                     "data_orderbook_start": "2014-02-24T17:43:05.0000000Z",
                     "data_orderbook_end": "2023-07-07T00:00:00.0000000Z",
                     "data_trade_start": "2010-07-17T00:00:00.0000000Z",
                     "data_trade_end": "2024-02-12T00:00:00.0000000Z",
                     "data_symbols_count": 204687,
                     "volume_1hrs_usd": 570629419010858.42,
                     "volume_1day_usd": 42083054949232794.23,
                     "volume_1mth_usd": 2345407841117665863.10,
                     "price_usd": 49887.330921026561074535863323,
                     "id_icon": "4caf2b16-a017-4e26-a348-2cea69c34cba",
                     "data_start": "2010-07-17",
                     "data_end": "2024-02-12"
                 }
         ]
        """;
    private final HttpResponse<String> response = mock();
    private final HttpClient client = mock();
    private final CryptoApiClient apiClient = new CryptoApiClient(client, "apiKey");
    private final Cache<Equity> cache = new EquitiesCache();
    private final CacheHandler cacheHandler = new CacheHandler(cache, 100, apiClient);

    @Test
    void testUpdateCacheInvalidResponseCodeThrowsApiClientException() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(404);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        assertThrows(ApiClientException.class, cacheHandler::updateCache);
    }

    @Test
    void testUpdateCacheValidApiResponseUpdateCacheValues()
        throws IOException, InterruptedException, ApiClientException {
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(RESPONSE_BODY);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        cacheHandler.updateCache();
        assertEquals(1, cache.getCachedValues().size());
        assertEquals("BTC", cache.getCachedValues().stream().toList().getFirst().equityId());
    }
    @Test
    void testUpdateCacheNotPassedIntervalOnlyOnceUpdatedValues()
        throws IOException, InterruptedException, ApiClientException {
        when(response.body()).thenReturn(RESPONSE_BODY);
        when(response.statusCode()).thenReturn(200);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        cacheHandler.updateCache();
        cacheHandler.updateCache();
        verify(client, times(1)).send(any(), eq(HttpResponse.BodyHandlers.ofString()));
    }
}

package bg.sofia.uni.fmi.mjt.crypto.cache;

import bg.sofia.uni.fmi.mjt.crypto.exception.ApiClientException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CryptoApiClientTest {
    private final HttpClient client = mock(HttpClient.class);

    private final CryptoApiClient apiClient = new CryptoApiClient(client, "test-api-key");

    @Test
    public void testSendRequestSuccessful() {
        assertDoesNotThrow(apiClient::sendRequest);
    }

    @Test
    public void testSendRequestClientThrowsIOException() throws IOException, InterruptedException {
        when(client.send(any(), any())).thenThrow(IOException.class);
        assertEquals("There was a problem with write/read operations!",
            assertThrows(ApiClientException.class, apiClient::sendRequest).getMessage());
    }

    @Test
    public void testSendRequestClientThrowsInterruptedException() throws IOException, InterruptedException {
        when(client.send(any(), any())).thenThrow(InterruptedException.class);
        assertEquals("The cache update was interrupted!",
            assertThrows(ApiClientException.class, apiClient::sendRequest).getMessage());
    }
}

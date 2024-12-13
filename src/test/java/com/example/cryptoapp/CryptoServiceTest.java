package com.example.cryptoapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cryptoapp.model.ExchangeRequest;
import com.example.cryptoapp.service.CryptoService;

import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CryptoServiceTest {
	
    private CryptoService cryptoService;
    private WebClient webClientMock;
    private WebClient.Builder webClientBuilderMock;
    private WebClient.RequestHeadersUriSpec<?> uriSpecMock;
    private WebClient.ResponseSpec responseSpecMock;
    
    @BeforeEach
    void setUp() {
        webClientMock = mock(WebClient.class);
        uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);

        webClientBuilderMock = mock(WebClient.Builder.class);
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        cryptoService = new CryptoService(webClientBuilderMock);
    }

    @Test
    void testFetchCryptoRates() {
        String currency = "bitcoin";
        String[] filters = {"usd", "eth"};
        Map<String, Map<String, Double>> mockResponse = Map.of(
            "bitcoin", Map.of("usd", 20000.0, "eth", 0.7)
        );

        when(webClientMock.get()).thenAnswer(invocation -> uriSpecMock);
        when(uriSpecMock.uri(any(Function.class))).thenReturn(uriSpecMock);
        when(uriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Mockito.<Class<Map<String, Map<String, Double>>>>any()))
            .thenReturn(Mono.just(mockResponse));

        Map<String, Object> result = cryptoService.fetchCryptoRates(currency, filters).block();

        assertEquals(currency, result.get("source"));
        assertEquals(mockResponse.get(currency), result.get("rates"));

        verify(webClientMock, times(1)).get();
        verify(uriSpecMock, times(1)).uri(any(Function.class));
        verify(responseSpecMock, times(1)).bodyToMono(Mockito.<Class<Map<String, Map<String, Double>>>>any());
    }

    @Test
    void testCalculateExchange() {
        String currency = "bitcoin";
        String[] toCurrencies = {"usd", "eth"};
        double amount = 2.0;

        Map<String, Object> mockFetchResponse = Map.of(
            "source", currency,
            "rates", Map.of("usd", 20000.0, "eth", 0.7)
        );

        CryptoService spyCryptoService = spy(cryptoService);
        doReturn(Mono.just(mockFetchResponse))
            .when(spyCryptoService)
            .fetchCryptoRates(currency, toCurrencies);

        // Test
        Map<String, Object> response = spyCryptoService.calculateExchange(
            new ExchangeRequest(currency, List.of("usd", "eth"), amount)
        ).block();

        Map<String, Object> usdDetails = (Map<String, Object>) response.get("usd");
        assertEquals(20000.0, usdDetails.get("rate"));
        assertEquals(40000.0, usdDetails.get("result"));
        assertEquals(2.0 * 0.01, usdDetails.get("fee"));

        Map<String, Object> ethDetails = (Map<String, Object>) response.get("eth");
        assertEquals(0.7, ethDetails.get("rate"));
        assertEquals(1.4, ethDetails.get("result"));
        assertEquals(2.0 * 0.01, ethDetails.get("fee"));

        verify(spyCryptoService, times(1)).fetchCryptoRates(currency, toCurrencies);
    }
}

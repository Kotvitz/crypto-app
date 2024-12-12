package com.example.cryptoapp.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cryptoapp.model.ExchangeRequest;

import reactor.core.publisher.Mono;

@Service
public class CryptoService {
	private final WebClient webClient;

	public CryptoService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("https://api.coingecko.com/api/v3").build();
	}

	public Mono<Map<String, Object>> fetchCryptoRates(String currency, String[] filter) {
		Mono<List<String>> supportedCurrencies = fetchSupportedCurrencies();

		Mono<String[]> targetCurrencies = (filter == null || filter.length == 0)
				? supportedCurrencies.map(list -> list.toArray(new String[0]))
				: Mono.just(filter);

		return targetCurrencies.flatMap(currencies -> webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/simple/price").queryParam("ids", currency)
						.queryParam("vs_currencies", String.join(",", currencies)).build())
				.retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {
				}).map(apiResponse -> {
					Map<String, Object> response = new LinkedHashMap<>();
					response.put("source", currency);
					response.put("rates", apiResponse.get(currency));
					return response;
				}));
	}

	private Mono<List<String>> fetchSupportedCurrencies() {
		return webClient.get().uri("/simple/supported_vs_currencies").retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				});
	}

	public Mono<Map<String, Object>> calculateExchange(ExchangeRequest request) {
		return fetchCryptoRates(request.getFrom(), request.getTo().toArray(new String[0]))
				.map(response -> buildExchangeResponse(request, extractRates(response, request.getFrom())));
	}

	private Map<String, Double> extractRates(Map<String, Object> response, String currency) {
		Object ratesObject = response.get("rates");
		if (!(ratesObject instanceof Map<?, ?> rawMap)) {
			throw new IllegalArgumentException("Invalid rates data for currency: " + currency);
		}
		return rawMap.entrySet().stream()
				.filter(entry -> entry.getKey() instanceof String && entry.getValue() instanceof Double)
				.collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (Double) entry.getValue()));
	}

	private Map<String, Object> buildExchangeResponse(ExchangeRequest request, Map<String, Double> rates) {
		Map<String, Object> exchangeResponse = new LinkedHashMap<>();
		exchangeResponse.put("from", request.getFrom());

		rates.forEach((currency, rate) -> {
			Map<String, Object> currencyDetails = new LinkedHashMap<>();
			currencyDetails.put("rate", rate);
			currencyDetails.put("amount", request.getAmount());
			currencyDetails.put("result", rate * request.getAmount());
			currencyDetails.put("fee", request.getAmount() * 0.01);
			exchangeResponse.put(currency, currencyDetails);
		});

		return exchangeResponse;
	}
}

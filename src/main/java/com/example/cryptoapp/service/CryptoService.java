package com.example.cryptoapp.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cryptoapp.model.ExchangeRequest;
import com.example.cryptoapp.model.ExchangeResponse;

import reactor.core.publisher.Mono;

@Service
public class CryptoService {
	private final WebClient webClient;

	public CryptoService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("https://api.coingecko.com/api/v3").build();
	}

	public Mono<Map<String, Object>> fetchCryptoRates(String currency, String[] filter) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/simple/price").queryParam("ids", currency)
						.queryParamIfPresent("vs_currencies",
								filter != null ? Optional.of(String.join(",", filter)) : Optional.empty())
						.build())
				.retrieve().bodyToMono(new ParameterizedTypeReference<>() {
				});
	}

	public Mono<ExchangeResponse> calculateExchange(ExchangeRequest request) {
		return fetchCryptoRates(request.getFrom(), null).map(rates -> {
			ExchangeResponse response = new ExchangeResponse();
			response.setFrom(request.getFrom());
			request.getTo().forEach(targetCurrency -> {
				double rate = rates.containsKey(targetCurrency) ? (Double) rates.get(targetCurrency) : 0.0;
				double result = request.getAmount() * rate;
				double fee = request.getAmount() * 0.01;
				response.addExchange(targetCurrency, rate, result, fee);
			});
			return response;
		});
	}
}

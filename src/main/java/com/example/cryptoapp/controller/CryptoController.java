package com.example.cryptoapp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cryptoapp.model.ExchangeRequest;
import com.example.cryptoapp.service.CryptoService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/currencies")
public class CryptoController {
	
	@Autowired
    private CryptoService cryptoService;

	@GetMapping("/{currency}")
	public Mono<Map<String, Object>> getRates(@PathVariable String currency, @RequestParam(required = false) String[] filter) {
	    return cryptoService.fetchCryptoRates(currency, filter);
	}

    @PostMapping("/exchange")
    public Mono<Map<String, Object>> calculateExchange(@RequestBody ExchangeRequest request) {
        return cryptoService.calculateExchange(request);
    }
}

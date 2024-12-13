package com.example.cryptoapp.model;

import java.util.List;

public class ExchangeRequest {
	private String from;
	private List<String> to;
	private double amount;

	public ExchangeRequest(String from, List<String> to, double amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}

package com.example.cryptoapp.model;

import java.util.HashMap;
import java.util.Map;

public class ExchangeResponse {
	private String from;
	private Map<String, ExchangeDetail> exchanges = new HashMap<>();
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
    public void addExchange(String currency, double rate, double result, double fee) {
        exchanges.put(currency, new ExchangeDetail(rate, result, fee));
    }
	
    public static class ExchangeDetail {
        private double rate;
        private double result;
        private double fee;

        public ExchangeDetail(double rate, double result, double fee) {
            this.rate = rate;
            this.result = result;
            this.fee = fee;
        }

		public double getRate() {
			return rate;
		}

		public void setRate(double rate) {
			this.rate = rate;
		}

		public double getResult() {
			return result;
		}

		public void setResult(double result) {
			this.result = result;
		}

		public double getFee() {
			return fee;
		}

		public void setFee(double fee) {
			this.fee = fee;
		}
        
    }
}

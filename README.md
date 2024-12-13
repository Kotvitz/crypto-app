# CryptoApp

CryptoApp is a Spring WebFlux-based application that retrieves cryptocurrency rates and provides functionality to calculate currency exchanges. It integrates with the CoinGecko API to fetch real-time cryptocurrency rates.

## Features

- Get Cryptocurrency Rates
  - Retrieve a list of exchange rates for a specific cryptocurrency.
  - Filter the rates by providing target currencies.
- Currency Exchange Calculation
  - Forecast the exchange of cryptocurrency A to B with a specified amount.
  - Automatically includes a 1% transaction fee.

------

## Endpoints

### 1. Get Cryptocurrency Rates

**Request**:
`GET /currencies/{currency}?filter[]=<currency1>&filter[]=<currency2>`

**Example**:

```
curl -X GET "http://localhost:8080/currencies/bitcoin?filter[]=usd&filter[]=eth"
```

**Response**:

```
{
  "source": "bitcoin",
  "rates": {
    "usd": 20000.0,
    "eth": 0.7
  }
}
```

------

### 2. Calculate Cryptocurrency Exchange

**Request**:
`POST /currencies/exchange`

**Request Body**:

```
{
  "from": "bitcoin",
  "to": ["usd", "eth"],
  "amount": 2
}
```

**Example**:

```
curl -X POST "http://localhost:8080/currencies/exchange" -H "Content-Type: application/json" -d '{
  "from": "bitcoin",
  "to": ["usd", "eth"],
  "amount": 2
}'
```

**Response**:

```
{
  "from": "bitcoin",
  "usd": {
    "rate": 20000.0,
    "amount": 2,
    "result": 40000.0,
    "fee": 0.02
  },
  "eth": {
    "rate": 0.7,
    "amount": 2,
    "result": 1.4,
    "fee": 0.02
  }
}
```

------

## How to Run the Project

### Prerequisites

- **Java 17**
- **Maven** (or any compatible build tool)

### Steps

1. Clone the repository:

   ```
   git clone https://github.com/Kotvitz/crypto-app.git
   cd crypto-app
   ```

2. Build the project:

   ```
   mvn clean install
   ```

3. Run the application:

   ```
   mvn spring-boot:run
   ```

4. Access the application:

   - Base URL: `http://localhost:8080`

------

## Running Tests

To run unit tests:

```
mvn test
```

------

## Technologies Used

- **Java 17**
- **Spring Boot 3.x**
- **Spring WebFlux** for reactive programming
- **Mockito** for unit testing
- **JUnit 5** for testing framework
- **CoinGecko API** for cryptocurrency rates

------

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.
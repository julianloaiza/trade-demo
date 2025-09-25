# Trade Demo

A Spring Boot application that processes FIX (Financial Information eXchange) messages and streams trade data in real-time. This project demonstrates the integration of FIX message processing with **simulated** Kafka-like message streaming and **simulated** REST API calls for security data enrichment. The application uses Server-Sent Events (SSE) for real-time web updates.

> **Note**: This is an educational exercise. The Kafka functionality and REST API calls are simulated implementations, not real Kafka or external REST services.

## Overview

This application serves as a trade processing system that:
- Accepts FIX messages via REST API
- Processes and enriches trade data with security information
- Streams processed trades in real-time to connected clients
- Provides a health check endpoint for monitoring

## Features

- **FIX Message Processing**: Converts FIX messages to structured trade data
- **Simulated Kafka Streaming**: Uses Server-Sent Events (SSE) to simulate Kafka-like message streaming
- **Simulated Security Enhancement**: Simulates REST API calls to enrich trade data with security information
- **REST API**: Simple HTTP endpoints for message injection and health monitoring
- **CORS Support**: Configured for frontend integration
- **Educational Focus**: Demonstrates concepts without requiring external dependencies

## Project Structure

```
src/main/java/com/trade/demo/
├── domain/
│   ├── enums/           # Enumerations (IdSource)
│   ├── infrastructure/   # Interfaces (KafkaTemplate, RestTemplate)
│   ├── model/           # Domain models (TradeMessage, Message, SecurityId)
│   └── service/         # Business logic (TradeProcessor)
├── persistence/
│   ├── publisher/       # Simulated Kafka implementation (SSE-based)
│   └── security/        # Simulated REST client implementation
├── web/
│   ├── config/          # Spring configuration
│   └── controller/       # REST controllers
└── TradeDemoApplication.java
```

## Prerequisites

- Java 21
- Gradle 7+
- Spring Boot 3.5.6

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd trade-demo
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

   The application will start on `http://localhost:8080`

## API Endpoints

### Health Check
```http
GET /api/health
```
Returns the service status and active subscriber count.

**Response:**
```
Trade Demo Service is running. Active subscribers: 0
```

### Stream Trades (SSE)
```http
GET /api/stream
```
Establishes a Server-Sent Events connection for real-time trade streaming.

**Content-Type:** `text/event-stream`

### Inject FIX Message
```http
POST /api/fix
Content-Type: application/json
```

Processes FIX messages and converts them to trade data.

## FIX Message Format

The application accepts FIX messages in JSON format with the following tags:

| Tag | Field | Description | Example |
|-----|-------|-------------|---------|
| 17  | execId | Execution ID | "EXEC123456" |
| 48  | securityId | Security identifier | "2323" |
| 22  | securityIdSource | ID source type | "SEDOL", "ISIN", "CUSIP", "RIC" |
| 1   | account | Account number | "123123" |
| 32  | lastShares | Quantity (positive int) | "1200" |
| 6   | avgPx | Average price (positive double) | "150.25" |
| 8   | side | Trade side | "BUY", "SELL" |

## Example Usage

### Send a FIX Message

```bash
curl --location 'http://localhost:8080/api/fix' \
--header 'Content-Type: application/json' \
--data '{
    "17": "EXEC123456",
    "48": "2323", 
    "22": "SEDOL",
    "1": "123123",
    "32": "1200",
    "6": "150.25",
    "8": "BUY"
}'
```

**Response:**
```
FIX message processed successfully
```

### Check Service Health

```bash
curl http://localhost:8080/api/health
```

**Response:**
```
Trade Demo Service is running. Active subscribers: 1
```

### Connect to Trade Stream

```bash
curl -N http://localhost:8080/api/stream
```

This will establish an SSE connection and stream trade updates in real-time.

## Trade Message Structure

The processed trade messages contain the following fields:

```json
{
    "tradeId": "EXEC123456",
    "account": "123123",
    "securityId": "2323",
    "idSource": "SEDOL",
    "isin": "US1234567890",
    "sedol": "1234567",
    "cusip": "123456789",
    "ric": "AAPL.O",
    "ticker": "AAPL",
    "qty": 1200,
    "price": 150.25
}
```

## Configuration

The application uses the following configuration:

- **Port**: 8080 (default)
- **CORS**: Enabled for `http://localhost:4200`
- **Logging**: INFO level for application logs
- **Simulated Security Enhancement**: Simulates integration with external security master service

## Development

### Running Tests
```bash
./gradlew test
```

### Building for Production
```bash
./gradlew bootJar
```

### Dependencies

Key dependencies include:
- Spring Boot Web Starter
- Lombok (for code generation)

## Frontend Integration

The application is designed to work with Angular frontends. The SSE endpoint (`/api/stream`) provides real-time trade data that can be consumed by Angular services using `EventSource` or similar streaming libraries.

## Implementation Details

### Simulated Components

1. **Kafka Simulation**: The `KafkaTemplateImpl` class simulates Kafka functionality using Server-Sent Events (SSE). It maintains a list of connected clients and broadcasts messages to all subscribers.

2. **REST Template Simulation**: The `RestTemplateImpl` class simulates external REST API calls for security data enrichment. It returns mock data instead of making real HTTP requests.

3. **Security Enhancement**: The security enhancement process simulates calling an external security master service to enrich trade data with additional security identifiers.

## License

This project is for educational/demonstration purposes.

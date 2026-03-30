# Power Plant API

A REST API that computes a **production plan** for a set of power plants using a **merit-order based heuristic algorithm**.

**Java 17**
**Spring Boot 3.2**
port **8888**

---

This project solves a simplified version of the **Unit Commitment problem**
This implementation uses a **deterministic greedy + adjustment strategy**:
Not mathematically guaranteed optimal.

---

# Requirements

---

Java 17+, Maven 3.8+, Docker 20+           |

---

# Build and Run

## Option 1 — Maven

```bash
git clone https://github.com/Matthieu-Jck/PowerPlant-Api.git
cd PowerPlant-Api

mvn clean package -DskipTests
java -jar target/PowerPlant-Api-1.0.0.jar
```

API available at http://localhost:8888

---

## Option 2 — Docker

```bash
docker build -t PowerPlant-Api .
docker run -p 8888:8888 PowerPlant-Api
```

Detached mode:

```bash
docker run -d -p 8888:8888 --name PowerPlant-Api PowerPlant-Api
```

---

# Example Usage

```bash
curl -X POST http://localhost:8888/productionplan \
  -H "Content-Type: application/json" \
  -d @example_payloads/payload3.json
```

---

# Running Tests

```bash
mvn test
```
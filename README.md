# VaccinationPetDigital
digital vaccination pet using spring boot 3 and java 17

## Structure
```
com.juliany.vaccinationpet
├── controller
│   ├── PetController
│   └── VaccinationController
├── dto
│   ├── CreatePetRequest
│   ├── PetResponse
│   ├── CreateVaccinationRequest
│   └── VaccinationResponse
├── entity
│   ├── Pet
│   └── Vaccination
├── exception
│   ├── ResourceNotFoundException
│   └── GlobalExceptionHandler
├── mapper
│   ├── PetMapper
│   └── VaccinationMapper
├── repository
│   ├── PetRepository
│   └── VaccinationRepository
├── service
│   ├── PetService
│   └── VaccinationService
└── PetVaccinationApplication
````

## Endpoints
```
POST   /api/v1/pets
GET    /api/v1/pets
GET    /api/v1/pets/{id}
PUT    /api/v1/pets/{id}
DELETE /api/v1/pets/{id}

POST   /api/v1/pets/{petId}/vaccinations
GET    /api/v1/pets/{petId}/vaccinations
GET    /api/v1/vaccinations/{id}
PUT    /api/v1/vaccinations/{id}
DELETE /api/v1/vaccinations/{id}
````

## Development flow
Spring Initializr
      ↓
Dev enviroment init application
      ↓
Dockerfile
      ↓
PostgreSQL on Compose
      ↓
Entities and endpoints
      ↓
Flyway
      ↓
Tests

## Commands
```bash
# Enter the API directory before running the commands below
cd vaccination-pet-api

# Build the application
docker compose up --build

# show app logs
docker compose logs app

# Run the development environment with automatic source synchronization
# Tests run against db-test before the API starts or restarts
# Stop the packaged app first if it is already using port 8080
docker compose stop app
docker compose --profile dev up --watch app-dev

# Run tests with Docker (Java and Maven are not required on the host)
docker compose \
  --profile test \
  up \
  --abort-on-container-exit \
  --exit-code-from test \
  test

# Remove the stopped test containers (optional)
docker compose --profile test rm -s -f test db-test

# verify DB
docker compose exec db \
  psql -U pet_app -d pet_vaccination -c "SELECT 1;"

# Run the application without Docker (requires Java 17 on the host)
./mvnw spring-boot:run
```

The development command creates its image on the first execution. After that,
Compose synchronizes changes from `src`, `pom.xml`, `mvnw`, and `.mvn` without
rebuilding the image. It restarts the development container, runs the tests
against the temporary `db-test`, and starts the API only when they pass.

The standalone test command starts only the test runner and its dedicated
PostgreSQL instance. The current source code is mounted in the test container,
so subsequent runs do not require `--build`. The test database uses temporary
storage, does not share data with the development database, and is discarded
when the container stops. The command returns a non-zero exit code if the Maven
test suite fails.

## Documentarions
[SpringBoot 3.5.16](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
[Spring Initializator](https://start.spring.io)

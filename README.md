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
# Build the application
docker compose up --build

# show app logs
docker compose logs app

# Run test
./mvnw clean test

# verify DB
docker compose exec db \
  psql -U pet_app -d pet_vaccination -c "SELECT 1;"

# Run the application
./mvnw spring-boot:run
```

## Documentarions
[SpringBoot 3.5.16](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
[Spring Initializator](https://start.spring.io)
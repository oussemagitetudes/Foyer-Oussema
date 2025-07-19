# Unit Tests Documentation

## Overview

This directory contains comprehensive unit tests for the Foyer Management System. The tests are organized by entity and cover all CRUD operations, business logic methods, and edge cases.

## Test Structure

### Integration Tests (Spring Context)

These tests run with the full Spring application context and test the actual integration between services and repositories:

- **BlocServiceTest.java** - Tests for Bloc entity operations
- **ChambreServiceTest.java** - Tests for Chambre entity operations  
- **EtudiantServiceTest.java** - Tests for Etudiant entity operations
- **FoyerServiceTest.java** - Tests for Foyer entity operations
- **ReservationServiceTest.java** - Tests for Reservation entity operations
- **UniversiteServiceTest.java** - Tests for Universite entity operations

### Mock Tests (Isolated Unit Testing)

These tests use Mockito to isolate the service layer from the repository layer:

- **BlocServiceMockTest.java** - Mock-based tests for Bloc service

### Test Suite

- **TestSuite.java** - Organizes all tests into a comprehensive test suite

## Test Coverage

Each test class covers the following areas:

### 1. CRUD Operations
- **Create**: `testAdd[Entity]()` - Tests entity creation
- **Read**: `testFindAll[Entities]()`, `testFind[Entity]ById()` - Tests entity retrieval
- **Update**: `testUpdate[Entity]()` - Tests entity updates
- **Delete**: `testDelete[Entity]ById()`, `testDelete[Entity]()` - Tests entity deletion

### 2. Business Logic Methods
Each entity has specific business logic methods that are tested:
- **Bloc**: `affecterChambresABloc()`, `affecterBlocAFoyer()`, `ajouterBlocEtSesChambres()`, etc.
- **Chambre**: `getChambresParNomBloc()`, `nbChambreParTypeEtBloc()`, `getChambresNonReserveParNomFoyerEtTypeChambre()`, etc.
- **Etudiant**: `selectJPQL()`, `affecterReservationAEtudiant()`, `desaffecterReservationAEtudiant()`, etc.
- **Foyer**: `affecterFoyerAUniversite()`, `desaffecterFoyerAUniversite()`, `ajouterFoyerEtAffecterAUniversite()`, etc.
- **Reservation**: `ajouterReservationEtAssignerAChambreEtAEtudiant()`, `getReservationParAnneeUniversitaire()`, `annulerReservation()`, etc.
- **Universite**: `ajouterUniversiteEtSonFoyer()`

### 3. Edge Cases and Error Handling
- **Non-existent IDs**: Tests behavior when searching for non-existent entities
- **Null/Empty Values**: Tests handling of null or empty input values
- **Invalid Parameters**: Tests behavior with invalid parameters
- **Exception Handling**: Tests that appropriate exceptions are thrown

### 4. Mock Testing
- **Repository Mocking**: Mocks repository layer to isolate service testing
- **Behavior Verification**: Verifies that repository methods are called correctly
- **Error Scenarios**: Tests service behavior when repositories return errors

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=BlocServiceTest
```

### Run Test Suite
```bash
mvn test -Dtest=TestSuite
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

## Test Annotations Used

- **@SpringBootTest**: Loads the full Spring application context
- **@ExtendWith(MockitoExtension.class)**: Enables Mockito for mock testing
- **@TestMethodOrder(MethodOrderer.OrderAnnotation.class)**: Ensures test execution order
- **@Order(n)**: Specifies test execution order
- **@BeforeAll/@AfterAll**: Setup and cleanup for all tests
- **@BeforeEach/@AfterEach**: Setup and cleanup for each test
- **@Test**: Marks a method as a test
- **@Mock**: Creates mock objects
- **@InjectMocks**: Injects mocks into the service under test

## Test Data Management

### Test Data Setup
Each test class uses `@BeforeAll` to set up test data:
```java
@BeforeAll
static void before() {
    testEntity = Entity.builder()
        .property1("value1")
        .property2("value2")
        .build();
}
```

### Test Isolation
Tests are designed to be independent and can run in any order. Each test:
- Creates its own test data
- Performs assertions
- Cleans up after itself (when necessary)

## Assertions Used

- **assertNotNull()**: Verifies objects are not null
- **assertEquals()**: Verifies expected vs actual values
- **assertTrue()/assertFalse()**: Verifies boolean conditions
- **assertThrows()**: Verifies that exceptions are thrown
- **assertDoesNotThrow()**: Verifies that no exceptions are thrown
- **verify()**: Verifies mock method calls (in mock tests)

## Best Practices Followed

1. **Given-When-Then Structure**: All tests follow the AAA (Arrange-Act-Assert) pattern
2. **Descriptive Test Names**: Test methods have clear, descriptive names
3. **Test Independence**: Each test can run independently
4. **Proper Cleanup**: Tests clean up after themselves
5. **Comprehensive Coverage**: Tests cover happy path, edge cases, and error scenarios
6. **Mock Verification**: Mock tests verify that dependencies are called correctly

## Adding New Tests

When adding new tests:

1. Follow the existing naming convention: `test[MethodName]()`
2. Use the Given-When-Then structure
3. Add appropriate assertions
4. Include edge case testing
5. Update this documentation if adding new test categories

## Dependencies

The tests use the following dependencies:
- **JUnit 5**: Testing framework
- **Spring Boot Test**: Spring testing support
- **Mockito**: Mocking framework
- **AssertJ**: Fluent assertion library (via JUnit assertions)

## Notes

- Tests are designed to work with an in-memory database (H2) for integration tests
- Mock tests are completely isolated and don't require a database
- All tests use the `@TestMethodOrder` annotation to ensure consistent execution order
- The test suite can be run to execute all tests in a structured manner 
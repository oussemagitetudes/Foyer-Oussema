package tn.esprit.spring;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Foyer Management System
 * 
 * This suite includes comprehensive unit tests for all entities:
 * - Bloc (with both integration and mock tests)
 * - Chambre
 * - Etudiant
 * - Foyer
 * - Reservation
 * - Universite
 * 
 * The tests cover:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Business logic methods
 * - Edge cases and error handling
 * - Mock-based testing for isolated unit testing
 */
@Suite
@SuiteDisplayName("Foyer Management System Test Suite")
@SelectClasses({
    // Integration Tests (with Spring context)
    BlocServiceTest.class,
    ChambreServiceTest.class,
    EtudiantServiceTest.class,
    FoyerServiceTest.class,
    ReservationServiceTest.class,
    UniversiteServiceTest.class,
    
    // Mock Tests (isolated unit testing)
    BlocServiceMockTest.class
})
public class TestSuite {
    // This class serves as a test suite container
    // All tests are automatically discovered and executed
} 
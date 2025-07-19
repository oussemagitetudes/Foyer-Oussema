package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.Services.Universite.IUniversiteService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class UniversiteServiceTest {

    @Autowired
    private IUniversiteService universiteService;

    private static Universite testUniversite;

    @BeforeAll
    static void before() {
        testUniversite = Universite.builder()
                .nomUniversite("TestUniversity")
                .adresse("TestAddress")
                .build();
    }

    @AfterAll
    static void after() {
        // Cleanup if needed
    }

    @BeforeEach
    void beforeEach() {
        // Setup before each test
    }

    @AfterEach
    void afterEach() {
        // Cleanup after each test
    }

    @Test
    @Order(1)
    void testAddUniversite() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("NewUniversity")
                .adresse("NewAddress")
                .build();

        // When
        Universite savedUniversite = universiteService.addOrUpdate(universite);

        // Then
        assertNotNull(savedUniversite);
        assertNotNull(savedUniversite.getIdUniversite());
        assertEquals("NewUniversity", savedUniversite.getNomUniversite());
        assertEquals("NewAddress", savedUniversite.getAdresse());
    }

    @Test
    @Order(2)
    void testFindAllUniversites() {
        // When
        List<Universite> universites = universiteService.findAll();

        // Then
        assertNotNull(universites);
        assertFalse(universites.isEmpty());
    }

    @Test
    @Order(3)
    void testFindUniversiteById() {
        // Given
        Universite universite = universiteService.addOrUpdate(testUniversite);
        Long universiteId = universite.getIdUniversite();

        // When
        Universite foundUniversite = universiteService.findById(universiteId);

        // Then
        assertNotNull(foundUniversite);
        assertEquals(universiteId, foundUniversite.getIdUniversite());
        assertEquals(testUniversite.getNomUniversite(), foundUniversite.getNomUniversite());
        assertEquals(testUniversite.getAdresse(), foundUniversite.getAdresse());
    }

    @Test
    @Order(4)
    void testUpdateUniversite() {
        // Given
        Universite universite = universiteService.addOrUpdate(testUniversite);
        universite.setAdresse("UpdatedAddress");

        // When
        Universite updatedUniversite = universiteService.addOrUpdate(universite);

        // Then
        assertNotNull(updatedUniversite);
        assertEquals("UpdatedAddress", updatedUniversite.getAdresse());
    }

    @Test
    @Order(5)
    void testDeleteUniversiteById() {
        // Given
        Universite universite = universiteService.addOrUpdate(testUniversite);
        Long universiteId = universite.getIdUniversite();

        // When
        universiteService.deleteById(universiteId);

        // Then
        assertThrows(Exception.class, () -> universiteService.findById(universiteId));
    }

    @Test
    @Order(6)
    void testDeleteUniversite() {
        // Given
        Universite universite = universiteService.addOrUpdate(testUniversite);

        // When
        universiteService.delete(universite);

        // Then
        assertThrows(Exception.class, () -> universiteService.findById(universite.getIdUniversite()));
    }

    @Test
    @Order(7)
    void testAjouterUniversiteEtSonFoyer() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("UniversityWithFoyer")
                .adresse("FoyerAddress")
                .build();

        // When
        Universite savedUniversite = universiteService.ajouterUniversiteEtSonFoyer(universite);

        // Then
        assertNotNull(savedUniversite);
        assertNotNull(savedUniversite.getIdUniversite());
        assertEquals("UniversityWithFoyer", savedUniversite.getNomUniversite());
    }

    @Test
    @Order(8)
    void testFindUniversiteByNonExistentId() {
        // Given
        Long nonExistentId = 999999L;

        // When & Then
        assertThrows(Exception.class, () -> universiteService.findById(nonExistentId));
    }

    @Test
    @Order(9)
    void testAddUniversiteWithNullValues() {
        // Given
        Universite universite = new Universite();

        // When
        Universite savedUniversite = universiteService.addOrUpdate(universite);

        // Then
        assertNotNull(savedUniversite);
        assertNotNull(savedUniversite.getIdUniversite());
    }

    @Test
    @Order(10)
    void testAddUniversiteWithEmptyValues() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("")
                .adresse("")
                .build();

        // When
        Universite savedUniversite = universiteService.addOrUpdate(universite);

        // Then
        assertNotNull(savedUniversite);
        assertNotNull(savedUniversite.getIdUniversite());
        assertEquals("", savedUniversite.getNomUniversite());
        assertEquals("", savedUniversite.getAdresse());
    }

    @Test
    @Order(11)
    void testUpdateUniversiteWithNewValues() {
        // Given
        Universite universite = universiteService.addOrUpdate(testUniversite);
        universite.setNomUniversite("CompletelyNewName");
        universite.setAdresse("CompletelyNewAddress");

        // When
        Universite updatedUniversite = universiteService.addOrUpdate(universite);

        // Then
        assertNotNull(updatedUniversite);
        assertEquals("CompletelyNewName", updatedUniversite.getNomUniversite());
        assertEquals("CompletelyNewAddress", updatedUniversite.getAdresse());
    }

    @Test
    @Order(12)
    void testAjouterUniversiteEtSonFoyerWithNullValues() {
        // Given
        Universite universite = new Universite();

        // When
        Universite savedUniversite = universiteService.ajouterUniversiteEtSonFoyer(universite);

        // Then
        assertNotNull(savedUniversite);
        assertNotNull(savedUniversite.getIdUniversite());
    }
} 
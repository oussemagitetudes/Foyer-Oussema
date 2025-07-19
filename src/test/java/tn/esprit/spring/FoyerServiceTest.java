package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.Services.Foyer.IFoyerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class FoyerServiceTest {

    @Autowired
    private IFoyerService foyerService;

    private static Foyer testFoyer;

    @BeforeAll
    static void before() {
        testFoyer = Foyer.builder()
                .nomFoyer("TestFoyer")
                .capaciteFoyer(100L)
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
    void testAddFoyer() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("NewFoyer")
                .capaciteFoyer(150L)
                .build();

        // When
        Foyer savedFoyer = foyerService.addOrUpdate(foyer);

        // Then
        assertNotNull(savedFoyer);
        assertNotNull(savedFoyer.getIdFoyer());
        assertEquals("NewFoyer", savedFoyer.getNomFoyer());
        assertEquals(150L, savedFoyer.getCapaciteFoyer());
    }

    @Test
    @Order(2)
    void testFindAllFoyers() {
        // When
        List<Foyer> foyers = foyerService.findAll();

        // Then
        assertNotNull(foyers);
        assertFalse(foyers.isEmpty());
    }

    @Test
    @Order(3)
    void testFindFoyerById() {
        // Given
        Foyer foyer = foyerService.addOrUpdate(testFoyer);
        Long foyerId = foyer.getIdFoyer();

        // When
        Foyer foundFoyer = foyerService.findById(foyerId);

        // Then
        assertNotNull(foundFoyer);
        assertEquals(foyerId, foundFoyer.getIdFoyer());
        assertEquals(testFoyer.getNomFoyer(), foundFoyer.getNomFoyer());
        assertEquals(testFoyer.getCapaciteFoyer(), foundFoyer.getCapaciteFoyer());
    }

    @Test
    @Order(4)
    void testUpdateFoyer() {
        // Given
        Foyer foyer = foyerService.addOrUpdate(testFoyer);
        foyer.setCapaciteFoyer(200L);

        // When
        Foyer updatedFoyer = foyerService.addOrUpdate(foyer);

        // Then
        assertNotNull(updatedFoyer);
        assertEquals(200L, updatedFoyer.getCapaciteFoyer());
    }

    @Test
    @Order(5)
    void testDeleteFoyerById() {
        // Given
        Foyer foyer = foyerService.addOrUpdate(testFoyer);
        Long foyerId = foyer.getIdFoyer();

        // When
        foyerService.deleteById(foyerId);

        // Then
        assertThrows(Exception.class, () -> foyerService.findById(foyerId));
    }

    @Test
    @Order(6)
    void testDeleteFoyer() {
        // Given
        Foyer foyer = foyerService.addOrUpdate(testFoyer);

        // When
        foyerService.delete(foyer);

        // Then
        assertThrows(Exception.class, () -> foyerService.findById(foyer.getIdFoyer()));
    }

    @Test
    @Order(7)
    void testAffecterFoyerAUniversite() {
        // Given
        Long idFoyer = 1L;
        String nomUniversite = "TestUniversity";

        // When
        Universite universite = foyerService.affecterFoyerAUniversite(idFoyer, nomUniversite);

        // Then
        assertNotNull(universite);
        // Additional assertions based on expected behavior
    }

    @Test
    @Order(8)
    void testDesaffecterFoyerAUniversite() {
        // Given
        Long idUniversite = 1L;

        // When
        Universite universite = foyerService.desaffecterFoyerAUniversite(idUniversite);

        // Then
        assertNotNull(universite);
        // Additional assertions based on expected behavior
    }

    @Test
    @Order(9)
    void testAjouterFoyerEtAffecterAUniversite() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("NewFoyerWithUniv")
                .capaciteFoyer(120L)
                .build();
        Long idUniversite = 1L;

        // When
        Foyer savedFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, idUniversite);

        // Then
        assertNotNull(savedFoyer);
        assertNotNull(savedFoyer.getIdFoyer());
        assertEquals("NewFoyerWithUniv", savedFoyer.getNomFoyer());
    }

    @Test
    @Order(10)
    void testAjoutFoyerEtBlocs() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("FoyerWithBlocs")
                .capaciteFoyer(80L)
                .build();

        // When
        Foyer savedFoyer = foyerService.ajoutFoyerEtBlocs(foyer);

        // Then
        assertNotNull(savedFoyer);
        assertNotNull(savedFoyer.getIdFoyer());
        assertEquals("FoyerWithBlocs", savedFoyer.getNomFoyer());
    }

    @Test
    @Order(11)
    void testAffecterFoyerAUniversiteWithIds() {
        // Given
        Long idFoyer = 1L;
        Long idUniversite = 1L;

        // When
        Universite universite = foyerService.affecterFoyerAUniversite(idFoyer, idUniversite);

        // Then
        assertNotNull(universite);
        // Additional assertions based on expected behavior
    }

    @Test
    @Order(12)
    void testFindFoyerByNonExistentId() {
        // Given
        Long nonExistentId = 999999L;

        // When & Then
        assertThrows(Exception.class, () -> foyerService.findById(nonExistentId));
    }

    @Test
    @Order(13)
    void testAddFoyerWithNullValues() {
        // Given
        Foyer foyer = new Foyer();

        // When
        Foyer savedFoyer = foyerService.addOrUpdate(foyer);

        // Then
        assertNotNull(savedFoyer);
        assertNotNull(savedFoyer.getIdFoyer());
    }

    @Test
    @Order(14)
    void testAffecterFoyerAUniversiteWithNonExistentIds() {
        // Given
        Long nonExistentFoyerId = 999999L;
        String nonExistentUniversite = "NonExistentUniv";

        // When & Then - should handle gracefully or throw appropriate exception
        assertDoesNotThrow(() -> 
            foyerService.affecterFoyerAUniversite(nonExistentFoyerId, nonExistentUniversite)
        );
    }
} 
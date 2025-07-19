package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.Services.Etudiant.IEtudiantService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class EtudiantServiceTest {

    @Autowired
    private IEtudiantService etudiantService;

    private static Etudiant testEtudiant;

    @BeforeAll
    static void before() {
        testEtudiant = Etudiant.builder()
                .nomEt("TestNom")
                .prenomEt("TestPrenom")
                .cin(12345678L)
                .ecole("TestEcole")
                .dateNaissance(java.time.LocalDate.of(2000, 1, 1))
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
    void testAddEtudiant() {
        // Given
        Etudiant etudiant = Etudiant.builder()
                .nomEt("Doe")
                .prenomEt("John")
                .cin(87654321L)
                .ecole("TestUniversity")
                .dateNaissance(java.time.LocalDate.of(1999, 5, 15))
                .build();

        // When
        Etudiant savedEtudiant = etudiantService.addOrUpdate(etudiant);

        // Then
        assertNotNull(savedEtudiant);
        assertNotNull(savedEtudiant.getIdEtudiant());
        assertEquals("Doe", savedEtudiant.getNomEt());
        assertEquals("John", savedEtudiant.getPrenomEt());
        assertEquals(87654321L, savedEtudiant.getCin());
    }

    @Test
    @Order(2)
    void testFindAllEtudiants() {
        // When
        List<Etudiant> etudiants = etudiantService.findAll();

        // Then
        assertNotNull(etudiants);
        assertFalse(etudiants.isEmpty());
    }

    @Test
    @Order(3)
    void testFindEtudiantById() {
        // Given
        Etudiant etudiant = etudiantService.addOrUpdate(testEtudiant);
        Long etudiantId = etudiant.getIdEtudiant();

        // When
        Etudiant foundEtudiant = etudiantService.findById(etudiantId);

        // Then
        assertNotNull(foundEtudiant);
        assertEquals(etudiantId, foundEtudiant.getIdEtudiant());
        assertEquals(testEtudiant.getNomEt(), foundEtudiant.getNomEt());
        assertEquals(testEtudiant.getPrenomEt(), foundEtudiant.getPrenomEt());
    }

    @Test
    @Order(4)
    void testUpdateEtudiant() {
        // Given
        Etudiant etudiant = etudiantService.addOrUpdate(testEtudiant);
        etudiant.setEcole("UpdatedEcole");

        // When
        Etudiant updatedEtudiant = etudiantService.addOrUpdate(etudiant);

        // Then
        assertNotNull(updatedEtudiant);
        assertEquals("UpdatedEcole", updatedEtudiant.getEcole());
    }

    @Test
    @Order(5)
    void testDeleteEtudiantById() {
        // Given
        Etudiant etudiant = etudiantService.addOrUpdate(testEtudiant);
        Long etudiantId = etudiant.getIdEtudiant();

        // When
        etudiantService.deleteById(etudiantId);

        // Then
        assertThrows(Exception.class, () -> etudiantService.findById(etudiantId));
    }

    @Test
    @Order(6)
    void testDeleteEtudiant() {
        // Given
        Etudiant etudiant = etudiantService.addOrUpdate(testEtudiant);

        // When
        etudiantService.delete(etudiant);

        // Then
        assertThrows(Exception.class, () -> etudiantService.findById(etudiant.getIdEtudiant()));
    }

    @Test
    @Order(7)
    void testSelectJPQL() {
        // When
        List<Etudiant> etudiants = etudiantService.selectJPQL("Test");

        // Then
        assertNotNull(etudiants);
        // Additional assertions based on expected data
    }

    @Test
    @Order(8)
    void testAffecterReservationAEtudiant() {
        // Given
        String idReservation = "RES001";
        String nomEtudiant = "TestNom";
        String prenomEtudiant = "TestPrenom";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> 
            etudiantService.affecterReservationAEtudiant(idReservation, nomEtudiant, prenomEtudiant)
        );
    }

    @Test
    @Order(9)
    void testDesaffecterReservationAEtudiant() {
        // Given
        String idReservation = "RES001";
        String nomEtudiant = "TestNom";
        String prenomEtudiant = "TestPrenom";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> 
            etudiantService.desaffecterReservationAEtudiant(idReservation, nomEtudiant, prenomEtudiant)
        );
    }

    @Test
    @Order(10)
    void testFindEtudiantByNonExistentId() {
        // Given
        Long nonExistentId = 999999L;

        // When & Then
        assertThrows(Exception.class, () -> etudiantService.findById(nonExistentId));
    }

    @Test
    @Order(11)
    void testAddEtudiantWithNullValues() {
        // Given
        Etudiant etudiant = new Etudiant();

        // When
        Etudiant savedEtudiant = etudiantService.addOrUpdate(etudiant);

        // Then
        assertNotNull(savedEtudiant);
        assertNotNull(savedEtudiant.getIdEtudiant());
    }

    @Test
    @Order(12)
    void testSelectJPQLWithEmptyResult() {
        // When
        List<Etudiant> etudiants = etudiantService.selectJPQL("NonExistentName");

        // Then
        assertNotNull(etudiants);
        // Should return empty list or handle appropriately
    }
} 
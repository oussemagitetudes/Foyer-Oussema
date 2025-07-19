package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.Services.Chambre.IChambreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ChambreServiceTest {

    @Autowired
    private IChambreService chambreService;

    private static Chambre testChambre;

    @BeforeAll
    static void before() {
        testChambre = Chambre.builder()
                .numeroChambre(101L)
                .typeC(TypeChambre.SIMPLE)
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
    void testAddChambre() {
        // Given
        Chambre chambre = Chambre.builder()
                .numeroChambre(102L)
                .typeC(TypeChambre.DOUBLE)
                .build();

        // When
        Chambre savedChambre = chambreService.addOrUpdate(chambre);

        // Then
        assertNotNull(savedChambre);
        assertNotNull(savedChambre.getIdChambre());
        assertEquals(102L, savedChambre.getNumeroChambre());
        assertEquals(TypeChambre.DOUBLE, savedChambre.getTypeC());
    }

    @Test
    @Order(2)
    void testFindAllChambres() {
        // When
        List<Chambre> chambres = chambreService.findAll();

        // Then
        assertNotNull(chambres);
        assertFalse(chambres.isEmpty());
    }

    @Test
    @Order(3)
    void testFindChambreById() {
        // Given
        Chambre chambre = chambreService.addOrUpdate(testChambre);
        Long chambreId = chambre.getIdChambre();

        // When
        Chambre foundChambre = chambreService.findById(chambreId);

        // Then
        assertNotNull(foundChambre);
        assertEquals(chambreId, foundChambre.getIdChambre());
        assertEquals(testChambre.getNumeroChambre(), foundChambre.getNumeroChambre());
    }

    @Test
    @Order(4)
    void testUpdateChambre() {
        // Given
        Chambre chambre = chambreService.addOrUpdate(testChambre);
        chambre.setTypeC(TypeChambre.TRIPLE);

        // When
        Chambre updatedChambre = chambreService.addOrUpdate(chambre);

        // Then
        assertNotNull(updatedChambre);
        assertEquals(TypeChambre.TRIPLE, updatedChambre.getTypeC());
    }

    @Test
    @Order(5)
    void testDeleteChambreById() {
        // Given
        Chambre chambre = chambreService.addOrUpdate(testChambre);
        Long chambreId = chambre.getIdChambre();

        // When
        chambreService.deleteById(chambreId);

        // Then
        assertThrows(Exception.class, () -> chambreService.findById(chambreId));
    }

    @Test
    @Order(6)
    void testDeleteChambre() {
        // Given
        Chambre chambre = chambreService.addOrUpdate(testChambre);

        // When
        chambreService.delete(chambre);

        // Then
        assertThrows(Exception.class, () -> chambreService.findById(chambre.getIdChambre()));
    }

    @Test
    @Order(7)
    void testGetChambresParNomBloc() {
        // When
        List<Chambre> chambres = chambreService.getChambresParNomBloc("Bloc A");

        // Then
        assertNotNull(chambres);
        // Additional assertions based on expected data
    }

    @Test
    @Order(8)
    void testNbChambreParTypeEtBloc() {
        // When
        long count = chambreService.nbChambreParTypeEtBloc(TypeChambre.SIMPLE, 1L);

        // Then
        assertTrue(count >= 0);
    }

    @Test
    @Order(9)
    void testGetChambresNonReserveParNomFoyerEtTypeChambre() {
        // When
        List<Chambre> chambres = chambreService.getChambresNonReserveParNomFoyerEtTypeChambre("Foyer A", TypeChambre.SIMPLE);

        // Then
        assertNotNull(chambres);
    }

    @Test
    @Order(10)
    void testListeChambresParBloc() {
        // When & Then - should not throw exception
        assertDoesNotThrow(() -> chambreService.listeChambresParBloc());
    }

    @Test
    @Order(11)
    void testPourcentageChambreParTypeChambre() {
        // When & Then - should not throw exception
        assertDoesNotThrow(() -> chambreService.pourcentageChambreParTypeChambre());
    }

    @Test
    @Order(12)
    void testNbPlacesDisponibleParChambreAnneeEnCours() {
        // When & Then - should not throw exception
        assertDoesNotThrow(() -> chambreService.nbPlacesDisponibleParChambreAnneeEnCours());
    }

    @Test
    @Order(13)
    void testGetChambresParNomBlocJava() {
        // When
        List<Chambre> chambres = chambreService.getChambresParNomBlocJava("Bloc A");

        // Then
        assertNotNull(chambres);
    }

    @Test
    @Order(14)
    void testGetChambresParNomBlocKeyWord() {
        // When
        List<Chambre> chambres = chambreService.getChambresParNomBlocKeyWord("Bloc A");

        // Then
        assertNotNull(chambres);
    }

    @Test
    @Order(15)
    void testGetChambresParNomBlocJPQL() {
        // When
        List<Chambre> chambres = chambreService.getChambresParNomBlocJPQL("Bloc A");

        // Then
        assertNotNull(chambres);
    }

    @Test
    @Order(16)
    void testGetChambresParNomBlocSQL() {
        // When
        List<Chambre> chambres = chambreService.getChambresParNomBlocSQL("Bloc A");

        // Then
        assertNotNull(chambres);
    }
} 
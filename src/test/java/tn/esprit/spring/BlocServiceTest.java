package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.Services.Bloc.IBlocService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BlocServiceTest {

    @Autowired
    private IBlocService blocService;

    private static Bloc testBloc;

    @BeforeAll
    static void before() {
        testBloc = Bloc.builder()
                .nomBloc("TestBloc")
                .capaciteBloc(50L)
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
    void testAddBloc() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("NewBloc")
                .capaciteBloc(100L)
                .build();

        // When
        Bloc savedBloc = blocService.addOrUpdate(bloc);

        // Then
        assertNotNull(savedBloc);
        assertNotNull(savedBloc.getIdBloc());
        assertEquals("NewBloc", savedBloc.getNomBloc());
        assertEquals(100L, savedBloc.getCapaciteBloc());
    }

    @Test
    @Order(2)
    void testFindAllBlocs() {
        // When
        List<Bloc> blocs = blocService.findAll();

        // Then
        assertNotNull(blocs);
        assertFalse(blocs.isEmpty());
    }

    @Test
    @Order(3)
    void testFindBlocById() {
        // Given
        Bloc bloc = blocService.addOrUpdate(testBloc);
        Long blocId = bloc.getIdBloc();

        // When
        Bloc foundBloc = blocService.findById(blocId);

        // Then
        assertNotNull(foundBloc);
        assertEquals(blocId, foundBloc.getIdBloc());
        assertEquals(testBloc.getNomBloc(), foundBloc.getNomBloc());
        assertEquals(testBloc.getCapaciteBloc(), foundBloc.getCapaciteBloc());
    }

    @Test
    @Order(4)
    void testUpdateBloc() {
        // Given
        Bloc bloc = blocService.addOrUpdate(testBloc);
        bloc.setCapaciteBloc(75L);

        // When
        Bloc updatedBloc = blocService.addOrUpdate(bloc);

        // Then
        assertNotNull(updatedBloc);
        assertEquals(75L, updatedBloc.getCapaciteBloc());
    }

    @Test
    @Order(5)
    void testDeleteBlocById() {
        // Given
        Bloc bloc = blocService.addOrUpdate(testBloc);
        Long blocId = bloc.getIdBloc();

        // When
        blocService.deleteById(blocId);

        // Then
        assertThrows(Exception.class, () -> blocService.findById(blocId));
    }

    @Test
    @Order(6)
    void testDeleteBloc() {
        // Given
        Bloc bloc = blocService.addOrUpdate(testBloc);

        // When
        blocService.delete(bloc);

        // Then
        assertThrows(Exception.class, () -> blocService.findById(bloc.getIdBloc()));
    }

    @Test
    @Order(7)
    void testAddOrUpdate2() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("BlocUpdate2")
                .capaciteBloc(60L)
                .build();

        // When
        Bloc savedBloc = blocService.addOrUpdate2(bloc);

        // Then
        assertNotNull(savedBloc);
        assertNotNull(savedBloc.getIdBloc());
        assertEquals("BlocUpdate2", savedBloc.getNomBloc());
    }

    @Test
    @Order(8)
    void testAffecterChambresABloc() {
        // Given
        List<Long> numChambres = List.of(101L, 102L, 103L);
        String nomBloc = "TestBloc";

        // When
        Bloc bloc = blocService.affecterChambresABloc(numChambres, nomBloc);

        // Then
        assertNotNull(bloc);
        // Additional assertions based on expected behavior
    }

    @Test
    @Order(9)
    void testAffecterBlocAFoyer() {
        // Given
        String nomBloc = "TestBloc";
        String nomFoyer = "TestFoyer";

        // When
        Bloc bloc = blocService.affecterBlocAFoyer(nomBloc, nomFoyer);

        // Then
        assertNotNull(bloc);
        // Additional assertions based on expected behavior
    }

    @Test
    @Order(10)
    void testAjouterBlocEtSesChambres() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("BlocWithChambres")
                .capaciteBloc(80L)
                .build();

        // When
        Bloc savedBloc = blocService.ajouterBlocEtSesChambres(bloc);

        // Then
        assertNotNull(savedBloc);
        assertNotNull(savedBloc.getIdBloc());
        assertEquals("BlocWithChambres", savedBloc.getNomBloc());
    }

    @Test
    @Order(11)
    void testAjouterBlocEtAffecterAFoyer() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("BlocWithFoyer")
                .capaciteBloc(90L)
                .build();
        String nomFoyer = "TestFoyer";

        // When
        Bloc savedBloc = blocService.ajouterBlocEtAffecterAFoyer(bloc, nomFoyer);

        // Then
        assertNotNull(savedBloc);
        assertNotNull(savedBloc.getIdBloc());
        assertEquals("BlocWithFoyer", savedBloc.getNomBloc());
    }

    @Test
    @Order(12)
    void testFindBlocByNonExistentId() {
        // Given
        Long nonExistentId = 999999L;

        // When & Then
        assertThrows(Exception.class, () -> blocService.findById(nonExistentId));
    }

    @Test
    @Order(13)
    void testAddBlocWithNullValues() {
        // Given
        Bloc bloc = new Bloc();

        // When
        Bloc savedBloc = blocService.addOrUpdate(bloc);

        // Then
        assertNotNull(savedBloc);
        assertNotNull(savedBloc.getIdBloc());
    }

    @Test
    @Order(14)
    void testAffecterChambresABlocWithEmptyList() {
        // Given
        List<Long> numChambres = List.of();
        String nomBloc = "TestBloc";

        // When
        Bloc bloc = blocService.affecterChambresABloc(numChambres, nomBloc);

        // Then
        assertNotNull(bloc);
        // Should handle empty list gracefully
    }

    @Test
    @Order(15)
    void testAffecterBlocAFoyerWithNonExistentNames() {
        // Given
        String nomBloc = "NonExistentBloc";
        String nomFoyer = "NonExistentFoyer";

        // When & Then - should handle gracefully or throw appropriate exception
        assertDoesNotThrow(() -> blocService.affecterBlocAFoyer(nomBloc, nomFoyer));
    }
}
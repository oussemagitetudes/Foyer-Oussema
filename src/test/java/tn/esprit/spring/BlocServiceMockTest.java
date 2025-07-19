package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BlocServiceMockTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private BlocService blocService;

    private Bloc testBloc;
    private Foyer testFoyer;

    @BeforeEach
    void beforeEach() {
        testBloc = Bloc.builder()
                .idBloc(1L)
                .nomBloc("TestBloc")
                .capaciteBloc(50L)
                .build();

        testFoyer = Foyer.builder()
                .idFoyer(1L)
                .nomFoyer("TestFoyer")
                .capaciteFoyer(100L)
                .build();
    }

    @AfterEach
    void afterEach() {
        reset(blocRepository, foyerRepository);
    }

    @Test
    @Order(1)
    void testAddBloc() {
        // Given
        Bloc blocToSave = Bloc.builder()
                .nomBloc("NewBloc")
                .capaciteBloc(100L)
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(testBloc);

        // When
        Bloc savedBloc = blocService.addOrUpdate(blocToSave);

        // Then
        assertNotNull(savedBloc);
        assertEquals(1L, savedBloc.getIdBloc());
        assertEquals("TestBloc", savedBloc.getNomBloc());
        verify(blocRepository, times(1)).save(any(Bloc.class));
    }

    @Test
    @Order(2)
    void testFindAllBlocs() {
        // Given
        List<Bloc> expectedBlocs = Arrays.asList(testBloc);
        when(blocRepository.findAll()).thenReturn(expectedBlocs);

        // When
        List<Bloc> blocs = blocService.findAll();

        // Then
        assertNotNull(blocs);
        assertEquals(1, blocs.size());
        assertEquals(testBloc, blocs.get(0));
        verify(blocRepository, times(1)).findAll();
    }

    @Test
    @Order(3)
    void testFindBlocById() {
        // Given
        when(blocRepository.findById(1L)).thenReturn(Optional.of(testBloc));

        // When
        Bloc foundBloc = blocService.findById(1L);

        // Then
        assertNotNull(foundBloc);
        assertEquals(1L, foundBloc.getIdBloc());
        assertEquals("TestBloc", foundBloc.getNomBloc());
        verify(blocRepository, times(1)).findById(1L);
    }

    @Test
    @Order(4)
    void testFindBlocByIdNotFound() {
        // Given
        when(blocRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> blocService.findById(999L));
        verify(blocRepository, times(1)).findById(999L);
    }

    @Test
    @Order(5)
    void testUpdateBloc() {
        // Given
        Bloc blocToUpdate = Bloc.builder()
                .idBloc(1L)
                .nomBloc("UpdatedBloc")
                .capaciteBloc(75L)
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(blocToUpdate);

        // When
        Bloc updatedBloc = blocService.addOrUpdate(blocToUpdate);

        // Then
        assertNotNull(updatedBloc);
        assertEquals("UpdatedBloc", updatedBloc.getNomBloc());
        assertEquals(75L, updatedBloc.getCapaciteBloc());
        verify(blocRepository, times(1)).save(blocToUpdate);
    }

    @Test
    @Order(6)
    void testDeleteBlocById() {
        // Given
        doNothing().when(blocRepository).deleteById(1L);

        // When
        blocService.deleteById(1L);

        // Then
        verify(blocRepository, times(1)).deleteById(1L);
    }

    @Test
    @Order(7)
    void testDeleteBloc() {
        // Given
        doNothing().when(blocRepository).delete(testBloc);

        // When
        blocService.delete(testBloc);

        // Then
        verify(blocRepository, times(1)).delete(testBloc);
    }

    @Test
    @Order(8)
    void testAddOrUpdate2() {
        // Given
        Bloc blocToSave = Bloc.builder()
                .nomBloc("BlocUpdate2")
                .capaciteBloc(60L)
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(testBloc);

        // When
        Bloc savedBloc = blocService.addOrUpdate2(blocToSave);

        // Then
        assertNotNull(savedBloc);
        assertEquals(1L, savedBloc.getIdBloc());
        verify(blocRepository, times(1)).save(any(Bloc.class));
    }

    @Test
    @Order(9)
    void testAffecterBlocAFoyer() {
        // Given
        when(blocRepository.findByNomBloc("TestBloc")).thenReturn(testBloc);
        when(foyerRepository.findByNomFoyer("TestFoyer")).thenReturn(testFoyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(testBloc);

        // When
        Bloc result = blocService.affecterBlocAFoyer("TestBloc", "TestFoyer");

        // Then
        assertNotNull(result);
        verify(blocRepository, times(1)).findByNomBloc("TestBloc");
        verify(foyerRepository, times(1)).findByNomFoyer("TestFoyer");
        verify(blocRepository, times(1)).save(any(Bloc.class));
    }

    @Test
    @Order(10)
    void testAffecterBlocAFoyerBlocNotFound() {
        // Given
        when(blocRepository.findByNomBloc("NonExistentBloc")).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            blocService.affecterBlocAFoyer("NonExistentBloc", "TestFoyer")
        );
        verify(blocRepository, times(1)).findByNomBloc("NonExistentBloc");
        verify(foyerRepository, never()).findByNomFoyer(anyString());
    }

    @Test
    @Order(11)
    void testAffecterBlocAFoyerFoyerNotFound() {
        // Given
        when(blocRepository.findByNomBloc("TestBloc")).thenReturn(testBloc);
        when(foyerRepository.findByNomFoyer("NonExistentFoyer")).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            blocService.affecterBlocAFoyer("TestBloc", "NonExistentFoyer")
        );
        verify(blocRepository, times(1)).findByNomBloc("TestBloc");
        verify(foyerRepository, times(1)).findByNomFoyer("NonExistentFoyer");
    }

    @Test
    @Order(12)
    void testAjouterBlocEtSesChambres() {
        // Given
        Bloc blocToSave = Bloc.builder()
                .nomBloc("BlocWithChambres")
                .capaciteBloc(80L)
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(testBloc);

        // When
        Bloc savedBloc = blocService.ajouterBlocEtSesChambres(blocToSave);

        // Then
        assertNotNull(savedBloc);
        assertEquals(1L, savedBloc.getIdBloc());
        verify(blocRepository, times(1)).save(any(Bloc.class));
    }

    @Test
    @Order(13)
    void testAjouterBlocEtAffecterAFoyer() {
        // Given
        Bloc blocToSave = Bloc.builder()
                .nomBloc("BlocWithFoyer")
                .capaciteBloc(90L)
                .build();

        when(foyerRepository.findByNomFoyer("TestFoyer")).thenReturn(testFoyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(testBloc);

        // When
        Bloc savedBloc = blocService.ajouterBlocEtAffecterAFoyer(blocToSave, "TestFoyer");

        // Then
        assertNotNull(savedBloc);
        assertEquals(1L, savedBloc.getIdBloc());
        verify(foyerRepository, times(1)).findByNomFoyer("TestFoyer");
        verify(blocRepository, times(1)).save(any(Bloc.class));
    }
}

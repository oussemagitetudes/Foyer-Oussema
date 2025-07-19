package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.Services.Reservation.IReservationService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private IReservationService reservationService;

    private static Reservation testReservation;

    @BeforeAll
    static void before() {
        testReservation = Reservation.builder()
                .idReservation("RES001")
                .anneeUniversitaire(LocalDate.of(2024, 9, 1))
                .estValide(true)
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
    void testAddReservation() {
        // Given
        Reservation reservation = Reservation.builder()
                .idReservation("RES002")
                .anneeUniversitaire(LocalDate.of(2024, 9, 1))
                .estValide(true)
                .build();

        // When
        Reservation savedReservation = reservationService.addOrUpdate(reservation);

        // Then
        assertNotNull(savedReservation);
        assertEquals("RES002", savedReservation.getIdReservation());
        assertEquals(LocalDate.of(2024, 9, 1), savedReservation.getAnneeUniversitaire());
        assertTrue(savedReservation.isEstValide());
    }

    @Test
    @Order(2)
    void testFindAllReservations() {
        // When
        List<Reservation> reservations = reservationService.findAll();

        // Then
        assertNotNull(reservations);
        assertFalse(reservations.isEmpty());
    }

    @Test
    @Order(3)
    void testFindReservationById() {
        // Given
        Reservation reservation = reservationService.addOrUpdate(testReservation);
        String reservationId = reservation.getIdReservation();

        // When
        Reservation foundReservation = reservationService.findById(reservationId);

        // Then
        assertNotNull(foundReservation);
        assertEquals(reservationId, foundReservation.getIdReservation());
        assertEquals(testReservation.getAnneeUniversitaire(), foundReservation.getAnneeUniversitaire());
        assertEquals(testReservation.isEstValide(), foundReservation.isEstValide());
    }

    @Test
    @Order(4)
    void testUpdateReservation() {
        // Given
        Reservation reservation = reservationService.addOrUpdate(testReservation);
        reservation.setEstValide(false);

        // When
        Reservation updatedReservation = reservationService.addOrUpdate(reservation);

        // Then
        assertNotNull(updatedReservation);
        assertFalse(updatedReservation.isEstValide());
    }

    @Test
    @Order(5)
    void testDeleteReservationById() {
        // Given
        Reservation reservation = reservationService.addOrUpdate(testReservation);
        String reservationId = reservation.getIdReservation();

        // When
        reservationService.deleteById(reservationId);

        // Then
        assertThrows(Exception.class, () -> reservationService.findById(reservationId));
    }

    @Test
    @Order(6)
    void testDeleteReservation() {
        // Given
        Reservation reservation = reservationService.addOrUpdate(testReservation);

        // When
        reservationService.delete(reservation);

        // Then
        assertThrows(Exception.class, () -> reservationService.findById(reservation.getIdReservation()));
    }

    @Test
    @Order(7)
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        // Given
        Long numChambre = 101L;
        long cin = 12345678L;

        // When
        Reservation reservation = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(numChambre, cin);

        // Then
        assertNotNull(reservation);
        assertNotNull(reservation.getIdReservation());
    }

    @Test
    @Order(8)
    void testGetReservationParAnneeUniversitaire() {
        // Given
        LocalDate debutAnnee = LocalDate.of(2024, 9, 1);
        LocalDate finAnnee = LocalDate.of(2025, 6, 30);

        // When
        long count = reservationService.getReservationParAnneeUniversitaire(debutAnnee, finAnnee);

        // Then
        assertTrue(count >= 0);
    }

    @Test
    @Order(9)
    void testAnnulerReservation() {
        // Given
        long cinEtudiant = 12345678L;

        // When
        String result = reservationService.annulerReservation(cinEtudiant);

        // Then
        assertNotNull(result);
        // Additional assertions based on expected behavior
    }

    @Test
    @Order(10)
    void testAnnulerReservations() {
        // When & Then - should not throw exception
        assertDoesNotThrow(() -> reservationService.annulerReservations());
    }

    @Test
    @Order(11)
    void testAffectReservationAChambre() {
        // Given
        String idRes = "RES003";
        long idChambre = 101L;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> reservationService.affectReservationAChambre(idRes, idChambre));
    }

    @Test
    @Order(12)
    void testDeaffectReservationAChambre() {
        // Given
        String idRes = "RES003";
        long idChambre = 101L;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> reservationService.deaffectReservationAChambre(idRes, idChambre));
    }

    @Test
    @Order(13)
    void testFindReservationByNonExistentId() {
        // Given
        String nonExistentId = "NONEXISTENT";

        // When & Then
        assertThrows(Exception.class, () -> reservationService.findById(nonExistentId));
    }

    @Test
    @Order(14)
    void testAddReservationWithNullValues() {
        // Given
        Reservation reservation = new Reservation();

        // When
        Reservation savedReservation = reservationService.addOrUpdate(reservation);

        // Then
        assertNotNull(savedReservation);
        assertNotNull(savedReservation.getIdReservation());
    }

    @Test
    @Order(15)
    void testGetReservationParAnneeUniversitaireWithInvalidDates() {
        // Given
        LocalDate finAnnee = LocalDate.of(2024, 9, 1);
        LocalDate debutAnnee = LocalDate.of(2025, 6, 30); // Invalid: end before start

        // When
        long count = reservationService.getReservationParAnneeUniversitaire(debutAnnee, finAnnee);

        // Then
        assertTrue(count >= 0); // Should handle invalid dates gracefully
    }

    @Test
    @Order(16)
    void testAnnulerReservationWithNonExistentCin() {
        // Given
        long nonExistentCin = 99999999L;

        // When
        String result = reservationService.annulerReservation(nonExistentCin);

        // Then
        assertNotNull(result);
        // Should handle non-existent CIN gracefully
    }
} 
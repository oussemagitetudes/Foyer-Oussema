package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Reservation;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static Reservation testReservation;

    @BeforeAll
    static void before() {
        testReservation = Reservation.builder()
                .idReservation("RES001")
                .anneeUniversitaire(LocalDate.of(2024, 9, 1))
                .estValide(true)
                .build();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/reservation";
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
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                reservation,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RES002", response.getBody().getIdReservation());
        assertEquals(LocalDate.of(2024, 9, 1), response.getBody().getAnneeUniversitaire());
        assertTrue(response.getBody().isEstValide());
    }

    @Test
    @Order(2)
    void testFindAllReservations() {
        // When
        ResponseEntity<Reservation[]> response = restTemplate.getForEntity(
                baseUrl + "/findAll",
                Reservation[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @Order(3)
    void testFindReservationById() {
        // Given
        Reservation reservation = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testReservation,
                Reservation.class
        );
        String reservationId = reservation.getIdReservation();

        // When
        ResponseEntity<Reservation> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + reservationId,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reservationId, response.getBody().getIdReservation());
        assertEquals(testReservation.getAnneeUniversitaire(), response.getBody().getAnneeUniversitaire());
        assertEquals(testReservation.isEstValide(), response.getBody().isEstValide());
    }

    @Test
    @Order(4)
    void testFindReservationByIdNotFound() {
        // When
        ResponseEntity<Reservation> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=NONEXISTENT",
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testUpdateReservation() {
        // Given
        Reservation reservation = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testReservation,
                Reservation.class
        );
        reservation.setEstValide(false);

        // When
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                reservation,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEstValide());
    }

    @Test
    @Order(6)
    void testDeleteReservationById() {
        // Given
        Reservation reservation = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testReservation,
                Reservation.class
        );
        String reservationId = reservation.getIdReservation();

        // When
        restTemplate.delete(baseUrl + "/deleteById/" + reservationId);

        // Then - verify it's deleted
        ResponseEntity<Reservation> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + reservationId,
                Reservation.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testDeleteReservation() {
        // Given
        Reservation reservation = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testReservation,
                Reservation.class
        );

        // When
        restTemplate.delete(baseUrl + "/delete", reservation);

        // Then - verify it's deleted
        ResponseEntity<Reservation> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + reservation.getIdReservation(),
                Reservation.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        // Given
        Long numChambre = 101L;
        long cin = 12345678L;

        // When
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/ajouterReservationEtAssignerAChambreEtAEtudiant?numChambre=" + numChambre + "&cin=" + cin,
                null,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdReservation());
    }

    @Test
    @Order(9)
    void testGetReservationParAnneeUniversitaire() {
        // Given
        LocalDate debutAnnee = LocalDate.of(2024, 9, 1);
        LocalDate finAnnee = LocalDate.of(2025, 6, 30);

        // When
        ResponseEntity<Long> response = restTemplate.getForEntity(
                baseUrl + "/getReservationParAnneeUniversitaire?debutAnnee=" + debutAnnee + "&finAnnee=" + finAnnee,
                Long.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() >= 0);
    }

    @Test
    @Order(10)
    void testAnnulerReservation() {
        // Given
        long cinEtudiant = 12345678L;

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/annulerReservation?cinEtudiant=" + cinEtudiant,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(11)
    void testAddReservationWithInvalidData() {
        // Given
        Reservation reservation = new Reservation(); // Empty reservation

        // When
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                reservation,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdReservation());
    }

    @Test
    @Order(12)
    void testGetReservationParAnneeUniversitaireWithInvalidDates() {
        // Given
        LocalDate finAnnee = LocalDate.of(2024, 9, 1);
        LocalDate debutAnnee = LocalDate.of(2025, 6, 30); // Invalid: end before start

        // When
        ResponseEntity<Long> response = restTemplate.getForEntity(
                baseUrl + "/getReservationParAnneeUniversitaire?debutAnnee=" + debutAnnee + "&finAnnee=" + finAnnee,
                Long.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() >= 0); // Should handle invalid dates gracefully
    }

    @Test
    @Order(13)
    void testAnnulerReservationWithNonExistentCin() {
        // Given
        long nonExistentCin = 99999999L;

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/annulerReservation?cinEtudiant=" + nonExistentCin,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Should handle non-existent CIN gracefully
    }

    @Test
    @Order(14)
    void testAddReservationWithNullValues() {
        // Given
        Reservation reservation = Reservation.builder()
                .idReservation(null)
                .anneeUniversitaire(null)
                .estValide(false)
                .build();

        // When
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                reservation,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdReservation());
    }

    @Test
    @Order(15)
    void testAddReservationWithDifferentDates() {
        // Test with different academic years
        LocalDate[] dates = {
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2026, 9, 1)
        };
        
        for (int i = 0; i < dates.length; i++) {
            // Given
            Reservation reservation = Reservation.builder()
                    .idReservation("RES" + (100 + i))
                    .anneeUniversitaire(dates[i])
                    .estValide(true)
                    .build();

            // When
            ResponseEntity<Reservation> response = restTemplate.postForEntity(
                    baseUrl + "/addOrUpdate",
                    reservation,
                    Reservation.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(dates[i], response.getBody().getAnneeUniversitaire());
        }
    }

    @Test
    @Order(16)
    void testUpdateReservationWithNewValues() {
        // Given
        Reservation reservation = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testReservation,
                Reservation.class
        );
        reservation.setAnneeUniversitaire(LocalDate.of(2025, 9, 1));
        reservation.setEstValide(false);

        // When
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                reservation,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(LocalDate.of(2025, 9, 1), response.getBody().getAnneeUniversitaire());
        assertFalse(response.getBody().isEstValide());
    }

    @Test
    @Order(17)
    void testAjouterReservationEtAssignerAChambreEtAEtudiantWithInvalidData() {
        // Given
        Long numChambre = 999999L; // Non-existent room
        long cin = 99999999L; // Non-existent student

        // When
        ResponseEntity<Reservation> response = restTemplate.postForEntity(
                baseUrl + "/ajouterReservationEtAssignerAChambreEtAEtudiant?numChambre=" + numChambre + "&cin=" + cin,
                null,
                Reservation.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
} 
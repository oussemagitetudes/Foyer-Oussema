package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoyerRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static Foyer testFoyer;

    @BeforeAll
    static void before() {
        testFoyer = Foyer.builder()
                .nomFoyer("TestFoyer")
                .capaciteFoyer(100L)
                .build();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/foyer";
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
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdFoyer());
        assertEquals("NewFoyer", response.getBody().getNomFoyer());
        assertEquals(150L, response.getBody().getCapaciteFoyer());
    }

    @Test
    @Order(2)
    void testFindAllFoyers() {
        // When
        ResponseEntity<Foyer[]> response = restTemplate.getForEntity(
                baseUrl + "/findAll",
                Foyer[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @Order(3)
    void testFindFoyerById() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );
        Long foyerId = foyer.getIdFoyer();

        // When
        ResponseEntity<Foyer> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + foyerId,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(foyerId, response.getBody().getIdFoyer());
        assertEquals(testFoyer.getNomFoyer(), response.getBody().getNomFoyer());
        assertEquals(testFoyer.getCapaciteFoyer(), response.getBody().getCapaciteFoyer());
    }

    @Test
    @Order(4)
    void testFindFoyerByIdNotFound() {
        // When
        ResponseEntity<Foyer> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=999999",
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testUpdateFoyer() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );
        foyer.setCapaciteFoyer(200L);

        // When
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200L, response.getBody().getCapaciteFoyer());
    }

    @Test
    @Order(6)
    void testDeleteFoyerById() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );
        Long foyerId = foyer.getIdFoyer();

        // When
        restTemplate.delete(baseUrl + "/deleteById?id=" + foyerId);

        // Then - verify it's deleted
        ResponseEntity<Foyer> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + foyerId,
                Foyer.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testDeleteFoyer() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );

        // When
        restTemplate.delete(baseUrl + "/delete", foyer);

        // Then - verify it's deleted
        ResponseEntity<Foyer> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + foyer.getIdFoyer(),
                Foyer.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testAffecterFoyerAUniversite() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );

        // When
        ResponseEntity<Universite> response = restTemplate.exchange(
                baseUrl + "/affecterFoyerAUniversite?idFoyer=" + foyer.getIdFoyer() + "&nomUniversite=TestUniversity",
                HttpMethod.PUT,
                null,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    void testDesaffecterFoyerAUniversite() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );

        // When
        ResponseEntity<Universite> response = restTemplate.exchange(
                baseUrl + "/desaffecterFoyerAUniversite?idUniversite=1",
                HttpMethod.PUT,
                null,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(10)
    void testAjouterFoyerEtAffecterAUniversite() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("NewFoyerWithUniv")
                .capaciteFoyer(120L)
                .build();

        // When
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/ajouterFoyerEtAffecterAUniversite?idUniversite=1",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdFoyer());
        assertEquals("NewFoyerWithUniv", response.getBody().getNomFoyer());
    }

    @Test
    @Order(11)
    void testAffecterFoyerAUniversiteWithPathVariables() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );

        // When
        ResponseEntity<Universite> response = restTemplate.exchange(
                baseUrl + "/affecterFoyerAUniversite/" + foyer.getIdFoyer() + "/1",
                HttpMethod.PUT,
                null,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(12)
    void testAddFoyerWithInvalidData() {
        // Given
        Foyer foyer = new Foyer(); // Empty foyer

        // When
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdFoyer());
    }

    @Test
    @Order(13)
    void testAffecterFoyerAUniversiteWithNonExistentIds() {
        // When
        ResponseEntity<Universite> response = restTemplate.exchange(
                baseUrl + "/affecterFoyerAUniversite?idFoyer=999999&nomUniversite=NonExistentUniv",
                HttpMethod.PUT,
                null,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(14)
    void testDesaffecterFoyerAUniversiteWithNonExistentId() {
        // When
        ResponseEntity<Universite> response = restTemplate.exchange(
                baseUrl + "/desaffecterFoyerAUniversite?idUniversite=999999",
                HttpMethod.PUT,
                null,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(15)
    void testAddFoyerWithNullValues() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer(null)
                .capaciteFoyer(null)
                .build();

        // When
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdFoyer());
    }

    @Test
    @Order(16)
    void testAddFoyerWithEmptyValues() {
        // Given
        Foyer foyer = Foyer.builder()
                .nomFoyer("")
                .capaciteFoyer(0L)
                .build();

        // When
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdFoyer());
        assertEquals("", response.getBody().getNomFoyer());
        assertEquals(0L, response.getBody().getCapaciteFoyer());
    }

    @Test
    @Order(17)
    void testUpdateFoyerWithNewValues() {
        // Given
        Foyer foyer = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testFoyer,
                Foyer.class
        );
        foyer.setNomFoyer("CompletelyNewName");
        foyer.setCapaciteFoyer(300L);

        // When
        ResponseEntity<Foyer> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                foyer,
                Foyer.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CompletelyNewName", response.getBody().getNomFoyer());
        assertEquals(300L, response.getBody().getCapaciteFoyer());
    }
} 
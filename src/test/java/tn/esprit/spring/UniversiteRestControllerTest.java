package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Universite;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UniversiteRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static Universite testUniversite;

    @BeforeAll
    static void before() {
        testUniversite = Universite.builder()
                .nomUniversite("TestUniversity")
                .adresse("TestAddress")
                .build();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/universite";
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
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
        assertEquals("NewUniversity", response.getBody().getNomUniversite());
        assertEquals("NewAddress", response.getBody().getAdresse());
    }

    @Test
    @Order(2)
    void testFindAllUniversites() {
        // When
        ResponseEntity<Universite[]> response = restTemplate.getForEntity(
                baseUrl + "/findAll",
                Universite[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @Order(3)
    void testFindUniversiteById() {
        // Given
        Universite universite = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testUniversite,
                Universite.class
        );
        Long universiteId = universite.getIdUniversite();

        // When
        ResponseEntity<Universite> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + universiteId,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(universiteId, response.getBody().getIdUniversite());
        assertEquals(testUniversite.getNomUniversite(), response.getBody().getNomUniversite());
        assertEquals(testUniversite.getAdresse(), response.getBody().getAdresse());
    }

    @Test
    @Order(4)
    void testFindUniversiteByIdNotFound() {
        // When
        ResponseEntity<Universite> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=999999",
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testUpdateUniversite() {
        // Given
        Universite universite = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testUniversite,
                Universite.class
        );
        universite.setAdresse("UpdatedAddress");

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UpdatedAddress", response.getBody().getAdresse());
    }

    @Test
    @Order(6)
    void testDeleteUniversiteById() {
        // Given
        Universite universite = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testUniversite,
                Universite.class
        );
        Long universiteId = universite.getIdUniversite();

        // When
        restTemplate.delete(baseUrl + "/deleteById?id=" + universiteId);

        // Then - verify it's deleted
        ResponseEntity<Universite> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + universiteId,
                Universite.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testDeleteUniversite() {
        // Given
        Universite universite = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testUniversite,
                Universite.class
        );

        // When
        restTemplate.delete(baseUrl + "/delete", universite);

        // Then - verify it's deleted
        ResponseEntity<Universite> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + universite.getIdUniversite(),
                Universite.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testAjouterUniversiteEtSonFoyer() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("UniversityWithFoyer")
                .adresse("FoyerAddress")
                .build();

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/ajouterUniversiteEtSonFoyer",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
        assertEquals("UniversityWithFoyer", response.getBody().getNomUniversite());
    }

    @Test
    @Order(9)
    void testAddUniversiteWithInvalidData() {
        // Given
        Universite universite = new Universite(); // Empty universite

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
    }

    @Test
    @Order(10)
    void testAddUniversiteWithNullValues() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite(null)
                .adresse(null)
                .build();

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
    }

    @Test
    @Order(11)
    void testAddUniversiteWithEmptyValues() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("")
                .adresse("")
                .build();

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
        assertEquals("", response.getBody().getNomUniversite());
        assertEquals("", response.getBody().getAdresse());
    }

    @Test
    @Order(12)
    void testUpdateUniversiteWithNewValues() {
        // Given
        Universite universite = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testUniversite,
                Universite.class
        );
        universite.setNomUniversite("CompletelyNewName");
        universite.setAdresse("CompletelyNewAddress");

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CompletelyNewName", response.getBody().getNomUniversite());
        assertEquals("CompletelyNewAddress", response.getBody().getAdresse());
    }

    @Test
    @Order(13)
    void testAddMultipleUniversites() {
        // Test adding multiple universities
        String[] names = {"University A", "University B", "University C"};
        String[] addresses = {"Address A", "Address B", "Address C"};
        
        for (int i = 0; i < names.length; i++) {
            // Given
            Universite universite = Universite.builder()
                    .nomUniversite(names[i])
                    .adresse(addresses[i])
                    .build();

            // When
            ResponseEntity<Universite> response = restTemplate.postForEntity(
                    baseUrl + "/addOrUpdate",
                    universite,
                    Universite.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(names[i], response.getBody().getNomUniversite());
            assertEquals(addresses[i], response.getBody().getAdresse());
        }
    }

    @Test
    @Order(14)
    void testAjouterUniversiteEtSonFoyerWithNullValues() {
        // Given
        Universite universite = new Universite(); // Empty universite

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/ajouterUniversiteEtSonFoyer",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
    }

    @Test
    @Order(15)
    void testAjouterUniversiteEtSonFoyerWithEmptyValues() {
        // Given
        Universite universite = Universite.builder()
                .nomUniversite("")
                .adresse("")
                .build();

        // When
        ResponseEntity<Universite> response = restTemplate.postForEntity(
                baseUrl + "/ajouterUniversiteEtSonFoyer",
                universite,
                Universite.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdUniversite());
        assertEquals("", response.getBody().getNomUniversite());
        assertEquals("", response.getBody().getAdresse());
    }
} 
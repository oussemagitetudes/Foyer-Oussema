package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Etudiant;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EtudiantRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static Etudiant testEtudiant;

    @BeforeAll
    static void before() {
        testEtudiant = Etudiant.builder()
                .nomEt("TestNom")
                .prenomEt("TestPrenom")
                .cin(12345678L)
                .ecole("TestEcole")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/etudiant";
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
                .dateNaissance(LocalDate.of(1999, 5, 15))
                .build();

        // When
        ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                etudiant,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdEtudiant());
        assertEquals("Doe", response.getBody().getNomEt());
        assertEquals("John", response.getBody().getPrenomEt());
        assertEquals(87654321L, response.getBody().getCin());
    }

    @Test
    @Order(2)
    void testFindAllEtudiants() {
        // When
        ResponseEntity<Etudiant[]> response = restTemplate.getForEntity(
                baseUrl + "/findAll",
                Etudiant[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @Order(3)
    void testFindEtudiantById() {
        // Given
        Etudiant etudiant = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testEtudiant,
                Etudiant.class
        );
        Long etudiantId = etudiant.getIdEtudiant();

        // When
        ResponseEntity<Etudiant> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + etudiantId,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(etudiantId, response.getBody().getIdEtudiant());
        assertEquals(testEtudiant.getNomEt(), response.getBody().getNomEt());
        assertEquals(testEtudiant.getPrenomEt(), response.getBody().getPrenomEt());
    }

    @Test
    @Order(4)
    void testFindEtudiantByIdNotFound() {
        // When
        ResponseEntity<Etudiant> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=999999",
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testUpdateEtudiant() {
        // Given
        Etudiant etudiant = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testEtudiant,
                Etudiant.class
        );
        etudiant.setEcole("UpdatedEcole");

        // When
        ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                etudiant,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UpdatedEcole", response.getBody().getEcole());
    }

    @Test
    @Order(6)
    void testDeleteEtudiantById() {
        // Given
        Etudiant etudiant = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testEtudiant,
                Etudiant.class
        );
        Long etudiantId = etudiant.getIdEtudiant();

        // When
        restTemplate.delete(baseUrl + "/deleteById?id=" + etudiantId);

        // Then - verify it's deleted
        ResponseEntity<Etudiant> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + etudiantId,
                Etudiant.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testDeleteEtudiant() {
        // Given
        Etudiant etudiant = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testEtudiant,
                Etudiant.class
        );

        // When
        restTemplate.delete(baseUrl + "/delete", etudiant);

        // Then - verify it's deleted
        ResponseEntity<Etudiant> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + etudiant.getIdEtudiant(),
                Etudiant.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testSelectJPQL() {
        // When
        ResponseEntity<Etudiant[]> response = restTemplate.getForEntity(
                baseUrl + "/selectJPQL?nom=Test",
                Etudiant[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    void testSelectJPQLWithNonExistentName() {
        // When
        ResponseEntity<Etudiant[]> response = restTemplate.getForEntity(
                baseUrl + "/selectJPQL?nom=NonExistentName",
                Etudiant[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(10)
    void testAddEtudiantWithInvalidData() {
        // Given
        Etudiant etudiant = new Etudiant(); // Empty etudiant

        // When
        ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                etudiant,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdEtudiant());
    }

    @Test
    @Order(11)
    void testAddEtudiantWithNullValues() {
        // Given
        Etudiant etudiant = Etudiant.builder()
                .nomEt(null)
                .prenomEt(null)
                .cin(0L)
                .ecole(null)
                .dateNaissance(null)
                .build();

        // When
        ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                etudiant,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdEtudiant());
    }

    @Test
    @Order(12)
    void testAddEtudiantWithEmptyValues() {
        // Given
        Etudiant etudiant = Etudiant.builder()
                .nomEt("")
                .prenomEt("")
                .cin(0L)
                .ecole("")
                .dateNaissance(LocalDate.now())
                .build();

        // When
        ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                etudiant,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdEtudiant());
        assertEquals("", response.getBody().getNomEt());
        assertEquals("", response.getBody().getPrenomEt());
    }

    @Test
    @Order(13)
    void testSelectJPQLWithEmptyName() {
        // When
        ResponseEntity<Etudiant[]> response = restTemplate.getForEntity(
                baseUrl + "/selectJPQL?nom=",
                Etudiant[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(14)
    void testAddMultipleEtudiants() {
        // Test adding multiple students
        String[] names = {"Alice", "Bob", "Charlie"};
        String[] prenoms = {"Smith", "Johnson", "Williams"};
        
        for (int i = 0; i < names.length; i++) {
            // Given
            Etudiant etudiant = Etudiant.builder()
                    .nomEt(names[i])
                    .prenomEt(prenoms[i])
                    .cin(10000000L + i)
                    .ecole("TestUniversity")
                    .dateNaissance(LocalDate.of(2000, 1, 1))
                    .build();

            // When
            ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                    baseUrl + "/addOrUpdate",
                    etudiant,
                    Etudiant.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(names[i], response.getBody().getNomEt());
            assertEquals(prenoms[i], response.getBody().getPrenomEt());
        }
    }

    @Test
    @Order(15)
    void testUpdateEtudiantWithNewValues() {
        // Given
        Etudiant etudiant = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testEtudiant,
                Etudiant.class
        );
        etudiant.setNomEt("CompletelyNewName");
        etudiant.setPrenomEt("CompletelyNewPrenom");
        etudiant.setEcole("CompletelyNewEcole");

        // When
        ResponseEntity<Etudiant> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                etudiant,
                Etudiant.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CompletelyNewName", response.getBody().getNomEt());
        assertEquals("CompletelyNewPrenom", response.getBody().getPrenomEt());
        assertEquals("CompletelyNewEcole", response.getBody().getEcole());
    }
} 
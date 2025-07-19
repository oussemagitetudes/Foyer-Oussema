package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChambreRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static Chambre testChambre;

    @BeforeAll
    static void before() {
        testChambre = Chambre.builder()
                .numeroChambre(101L)
                .typeC(TypeChambre.SIMPLE)
                .build();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/chambre";
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
        ResponseEntity<Chambre> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                chambre,
                Chambre.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdChambre());
        assertEquals(102L, response.getBody().getNumeroChambre());
        assertEquals(TypeChambre.DOUBLE, response.getBody().getTypeC());
    }

    @Test
    @Order(2)
    void testFindAllChambres() {
        // When
        ResponseEntity<Chambre[]> response = restTemplate.getForEntity(
                baseUrl + "/findAll",
                Chambre[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @Order(3)
    void testFindChambreById() {
        // Given
        Chambre chambre = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testChambre,
                Chambre.class
        );
        Long chambreId = chambre.getIdChambre();

        // When
        ResponseEntity<Chambre> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + chambreId,
                Chambre.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(chambreId, response.getBody().getIdChambre());
        assertEquals(testChambre.getNumeroChambre(), response.getBody().getNumeroChambre());
    }

    @Test
    @Order(4)
    void testFindChambreByIdNotFound() {
        // When
        ResponseEntity<Chambre> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=999999",
                Chambre.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testUpdateChambre() {
        // Given
        Chambre chambre = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testChambre,
                Chambre.class
        );
        chambre.setTypeC(TypeChambre.TRIPLE);

        // When
        ResponseEntity<Chambre> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                chambre,
                Chambre.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TypeChambre.TRIPLE, response.getBody().getTypeC());
    }

    @Test
    @Order(6)
    void testDeleteChambreById() {
        // Given
        Chambre chambre = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testChambre,
                Chambre.class
        );
        Long chambreId = chambre.getIdChambre();

        // When
        restTemplate.delete(baseUrl + "/deleteById?id=" + chambreId);

        // Then - verify it's deleted
        ResponseEntity<Chambre> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + chambreId,
                Chambre.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testDeleteChambre() {
        // Given
        Chambre chambre = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testChambre,
                Chambre.class
        );

        // When
        restTemplate.delete(baseUrl + "/delete", chambre);

        // Then - verify it's deleted
        ResponseEntity<Chambre> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + chambre.getIdChambre(),
                Chambre.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testGetChambresParNomBloc() {
        // When
        ResponseEntity<Chambre[]> response = restTemplate.getForEntity(
                baseUrl + "/getChambresParNomBloc?nomBloc=TestBloc",
                Chambre[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    void testNbChambreParTypeEtBloc() {
        // When
        ResponseEntity<Long> response = restTemplate.getForEntity(
                baseUrl + "/nbChambreParTypeEtBloc?type=SIMPLE&idBloc=1",
                Long.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() >= 0);
    }

    @Test
    @Order(10)
    void testGetChambresNonReserveParNomFoyerEtTypeChambre() {
        // When
        ResponseEntity<Chambre[]> response = restTemplate.getForEntity(
                baseUrl + "/getChambresNonReserveParNomFoyerEtTypeChambre?nomFoyer=TestFoyer&type=SIMPLE",
                Chambre[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(11)
    void testAddChambreWithInvalidData() {
        // Given
        Chambre chambre = new Chambre(); // Empty chambre

        // When
        ResponseEntity<Chambre> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                chambre,
                Chambre.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdChambre());
    }

    @Test
    @Order(12)
    void testGetChambresParNomBlocWithNonExistentBloc() {
        // When
        ResponseEntity<Chambre[]> response = restTemplate.getForEntity(
                baseUrl + "/getChambresParNomBloc?nomBloc=NonExistentBloc",
                Chambre[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(13)
    void testNbChambreParTypeEtBlocWithNonExistentBloc() {
        // When
        ResponseEntity<Long> response = restTemplate.getForEntity(
                baseUrl + "/nbChambreParTypeEtBloc?type=SIMPLE&idBloc=999999",
                Long.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0L, response.getBody());
    }

    @Test
    @Order(14)
    void testGetChambresNonReserveParNomFoyerEtTypeChambreWithNonExistentFoyer() {
        // When
        ResponseEntity<Chambre[]> response = restTemplate.getForEntity(
                baseUrl + "/getChambresNonReserveParNomFoyerEtTypeChambre?nomFoyer=NonExistentFoyer&type=SIMPLE",
                Chambre[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(15)
    void testAddChambreWithDifferentTypes() {
        // Test with different room types
        TypeChambre[] types = {TypeChambre.SIMPLE, TypeChambre.DOUBLE, TypeChambre.TRIPLE};
        
        for (TypeChambre type : types) {
            // Given
            Chambre chambre = Chambre.builder()
                    .numeroChambre(200L + type.ordinal())
                    .typeC(type)
                    .build();

            // When
            ResponseEntity<Chambre> response = restTemplate.postForEntity(
                    baseUrl + "/addOrUpdate",
                    chambre,
                    Chambre.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(type, response.getBody().getTypeC());
        }
    }
} 
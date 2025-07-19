package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Bloc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BlocRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static Bloc testBloc;

    @BeforeAll
    static void before() {
        testBloc = Bloc.builder()
                .nomBloc("TestBloc")
                .capaciteBloc(50L)
                .build();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/bloc";
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
        ResponseEntity<Bloc> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                bloc,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdBloc());
        assertEquals("NewBloc", response.getBody().getNomBloc());
        assertEquals(100L, response.getBody().getCapaciteBloc());
    }

    @Test
    @Order(2)
    void testFindAllBlocs() {
        // When
        ResponseEntity<Bloc[]> response = restTemplate.getForEntity(
                baseUrl + "/findAll",
                Bloc[].class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(3)
    void testFindBlocById() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );
        Long blocId = bloc.getIdBloc();

        // When
        ResponseEntity<Bloc> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + blocId,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(blocId, response.getBody().getIdBloc());
        assertEquals(testBloc.getNomBloc(), response.getBody().getNomBloc());
    }

    @Test
    @Order(4)
    void testFindBlocByIdNotFound() {
        // When
        ResponseEntity<Bloc> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=999999",
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testUpdateBloc() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );
        bloc.setCapaciteBloc(75L);

        // When
        ResponseEntity<Bloc> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                bloc,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(75L, response.getBody().getCapaciteBloc());
    }

    @Test
    @Order(6)
    void testDeleteBlocById() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );
        Long blocId = bloc.getIdBloc();

        // When
        restTemplate.delete(baseUrl + "/deleteById?id=" + blocId);

        // Then - verify it's deleted
        ResponseEntity<Bloc> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + blocId,
                Bloc.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testDeleteBloc() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );

        // When
        restTemplate.delete(baseUrl + "/delete", bloc);

        // Then - verify it's deleted
        ResponseEntity<Bloc> response = restTemplate.getForEntity(
                baseUrl + "/findById?id=" + bloc.getIdBloc(),
                Bloc.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testAffecterChambresABloc() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );
        List<Long> numChambres = Arrays.asList(101L, 102L, 103L);

        // When
        ResponseEntity<Bloc> response = restTemplate.exchange(
                baseUrl + "/affecterChambresABloc?nomBloc=" + bloc.getNomBloc(),
                HttpMethod.PUT,
                new HttpEntity<>(numChambres),
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    void testAffecterBlocAFoyer() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );

        // When
        ResponseEntity<Bloc> response = restTemplate.exchange(
                baseUrl + "/affecterBlocAFoyer?nomBloc=" + bloc.getNomBloc() + "&nomFoyer=TestFoyer",
                HttpMethod.PUT,
                null,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(10)
    void testAffecterBlocAFoyer2() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );

        // When
        ResponseEntity<Bloc> response = restTemplate.exchange(
                baseUrl + "/affecterBlocAFoyer2/TestFoyer/" + bloc.getNomBloc(),
                HttpMethod.PUT,
                null,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(11)
    void testAjouterBlocEtSesChambres() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("BlocWithChambres")
                .capaciteBloc(80L)
                .build();

        // When
        ResponseEntity<Bloc> response = restTemplate.postForEntity(
                baseUrl + "/ajouterBlocEtSesChambres",
                bloc,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdBloc());
        assertEquals("BlocWithChambres", response.getBody().getNomBloc());
    }

    @Test
    @Order(12)
    void testAjouterBlocEtAffecterAFoyer() {
        // Given
        Bloc bloc = Bloc.builder()
                .nomBloc("BlocWithFoyer")
                .capaciteBloc(90L)
                .build();

        // When
        ResponseEntity<Bloc> response = restTemplate.postForEntity(
                baseUrl + "/ajouterBlocEtAffecterAFoyer/TestFoyer",
                bloc,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdBloc());
        assertEquals("BlocWithFoyer", response.getBody().getNomBloc());
    }

    @Test
    @Order(13)
    void testAddBlocWithInvalidData() {
        // Given
        Bloc bloc = new Bloc(); // Empty bloc

        // When
        ResponseEntity<Bloc> response = restTemplate.postForEntity(
                baseUrl + "/addOrUpdate",
                bloc,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getIdBloc());
    }

    @Test
    @Order(14)
    void testAffecterChambresABlocWithEmptyList() {
        // Given
        Bloc bloc = restTemplate.postForObject(
                baseUrl + "/addOrUpdate",
                testBloc,
                Bloc.class
        );
        List<Long> numChambres = Arrays.asList();

        // When
        ResponseEntity<Bloc> response = restTemplate.exchange(
                baseUrl + "/affecterChambresABloc?nomBloc=" + bloc.getNomBloc(),
                HttpMethod.PUT,
                new HttpEntity<>(numChambres),
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(15)
    void testAffecterBlocAFoyerWithNonExistentNames() {
        // When
        ResponseEntity<Bloc> response = restTemplate.exchange(
                baseUrl + "/affecterBlocAFoyer?nomBloc=NonExistentBloc&nomFoyer=NonExistentFoyer",
                HttpMethod.PUT,
                null,
                Bloc.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
} 
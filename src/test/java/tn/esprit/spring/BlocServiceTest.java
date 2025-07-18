package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BlocServiceTest {

    @BeforeAll
    static void before() {

    }

    @AfterAll
    static void after() {

    }

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    void afterEach() {

    }

    @Order(1)
    @RepeatedTest(4)
    void test() {

    }

    @Order(4)
    @Test
    void test2() {

    }

    @Order(2)
    @Test
    void test3() {

    }

    @Order(3)
    @Test
    void test4() {

    }
}
package com.miguelpedraza.ecommerce.test;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.miguelpedraza.ecommerce_api.EcommerceApiApplication;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = EcommerceApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@org.springframework.test.context.ContextConfiguration(initializers = PostgresContainerInitializer.class)
@org.springframework.context.annotation.Import(com.miguelpedraza.ecommerce.test.TestSupportConfig.class)
public abstract class BaseFunctionalTest {

}

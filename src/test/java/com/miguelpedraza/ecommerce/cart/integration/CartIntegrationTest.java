package com.miguelpedraza.ecommerce.cart.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.miguelpedraza.ecommerce.cart.presentation.dto.AddCartItemRequest;
import com.miguelpedraza.ecommerce.product.presentation.dto.CreateProductRequest;
import com.miguelpedraza.ecommerce.test.BaseFunctionalTest;
import com.miguelpedraza.ecommerce.user.presentation.dto.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartIntegrationTest extends BaseFunctionalTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.objectMapper = JsonMapper.builder().build();
    }

    @BeforeAll
    static void checkDocker() {
        Assumptions.assumeTrue(org.testcontainers.DockerClientFactory.instance().isDockerAvailable(), "Docker is not available, skipping integration tests");
    }

    @Test
    void createCartAndAddItem() throws Exception {
        // create user
        CreateUserRequest userReq = new CreateUserRequest("Cart User","cart.user@test.local","secret123");
        var userResult = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userReq)))
                .andReturn();
        System.out.println("[TEST] create user status=" + userResult.getResponse().getStatus() + " body=" + userResult.getResponse().getContentAsString());
        // expect created
        org.assertj.core.api.Assertions.assertThat(userResult.getResponse().getStatus()).isEqualTo(201);

        String userLocation = userResult.getResponse().getHeader("Location");
        String[] parts = userLocation.split("/");
        Long userId = Long.valueOf(parts[parts.length-1]);

        // create product
        CreateProductRequest prodReq = new CreateProductRequest("Cart Product","desc", BigDecimal.valueOf(9.99), "CART-SKU-1","default");
        var prodResult = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prodReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String prodLocation = prodResult.getResponse().getHeader("Location");
        parts = prodLocation.split("/");
        Long productId = Long.valueOf(parts[parts.length-1]);

        // add item to cart
        AddCartItemRequest addReq = new AddCartItemRequest(productId, 2);
        var addResult = mockMvc.perform(post(String.format("/api/v1/carts/%d/items", userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)))
                .andReturn();
        System.out.println("[TEST] add item status=" + addResult.getResponse().getStatus() + " body=" + addResult.getResponse().getContentAsString());
        org.assertj.core.api.Assertions.assertThat(addResult.getResponse().getStatus()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(addResult.getResponse().getContentAsString()).contains("CART-SKU-1").contains("Cart Product");

        // fetch cart
        mockMvc.perform(get(String.format("/api/v1/carts/%d", userId)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cart Product")));
    }
}

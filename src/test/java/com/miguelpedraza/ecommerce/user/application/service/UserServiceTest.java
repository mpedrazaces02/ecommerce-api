package com.miguelpedraza.ecommerce.user.application.service;

import com.miguelpedraza.ecommerce.user.domain.User;
import com.miguelpedraza.ecommerce.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserJpaRepository repository;
    private UserService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(UserJpaRepository.class);
        service = new UserService(repository);
    }

    @Test
    void createUser_success() {
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        User u = service.createUser("Name", "a@b.com", "password123");

        assertNotNull(u);
        assertEquals("a@b.com", u.getEmail());
        verify(repository).save(any());
    }

    @Test
    void listUsers() {
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(java.util.List.of(new User("n","e","p"))));

        var page = service.listUsers(PageRequest.of(0,10));
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void getUser_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getUser(1L));
    }
}

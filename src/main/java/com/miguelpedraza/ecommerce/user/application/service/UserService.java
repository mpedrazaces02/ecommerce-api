package com.miguelpedraza.ecommerce.user.application.service;

import com.miguelpedraza.ecommerce.user.application.exception.UserAlreadyExistsException;
import com.miguelpedraza.ecommerce.user.application.exception.UserNotFoundException;
import com.miguelpedraza.ecommerce.user.domain.User;
import com.miguelpedraza.ecommerce.user.infrastructure.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserJpaRepository repository;

    public UserService(UserJpaRepository repository) {
        this.repository = repository;
    }

    public User createUser(String name, String email, String password) {
        repository.findByEmail(email).ifPresent(u -> { throw new UserAlreadyExistsException(email); });
        User u = new User(name, email, password);
        return repository.save(u);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<User> listUsers(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public User updateUser(Long id, String name, String password, boolean active) {
        User u = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        u.update(name, password, active);
        return repository.save(u);
    }

    public void deactivateUser(Long id) {
        User u = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        u.deactivate();
        repository.save(u);
    }
}

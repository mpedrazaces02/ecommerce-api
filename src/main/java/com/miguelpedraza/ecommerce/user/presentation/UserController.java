package com.miguelpedraza.ecommerce.user.presentation;

import com.miguelpedraza.ecommerce.user.application.service.UserService;
import com.miguelpedraza.ecommerce.user.presentation.dto.CreateUserRequest;
import com.miguelpedraza.ecommerce.user.presentation.dto.UpdateUserRequest;
import com.miguelpedraza.ecommerce.user.presentation.dto.UserResponse;
import com.miguelpedraza.ecommerce.user.presentation.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req, UriComponentsBuilder uriBuilder) {
        var u = service.createUser(req.name(), req.email(), req.password());
        var resp = UserMapper.toResponse(u);
        var uri = uriBuilder.path("/api/v1/users/{id}").buildAndExpand(u.getId()).toUri();
        return ResponseEntity.created(uri).body(resp);
    }

    @GetMapping
    public Page<UserResponse> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size);
        return service.listUsers(p).map(UserMapper::toResponse);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return UserMapper.toResponse(service.getUser(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        var u = service.updateUser(id, req.name(), req.password(), req.active());
        return UserMapper.toResponse(u);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}

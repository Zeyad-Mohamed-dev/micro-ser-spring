package com.ecommerce.user.service;

import com.ecommerce.user.dto.CreateUserRequest;
import com.ecommerce.user.dto.UpdateUserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.exception.ResourceNotFoundException;
import com.ecommerce.user.model.Role;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthService authService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {
        authService.validateUniqueFields(request.getUsername(), request.getEmail(), null);

        User user = new User(
                request.getFullName(),
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id, Authentication authentication) {
        User user = findUserById(id);
        ensureSameUserOrAdmin(user, authentication);
        return toResponse(user);
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request, Authentication authentication) {
        User user = findUserById(id);
        ensureSameUserOrAdmin(user, authentication);
        authService.validateUniqueFields(request.getUsername(), request.getEmail(), id);

        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getRole() != null && isAdmin(authentication)) {
            user.setRole(request.getRole());
        }

        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id, Authentication authentication) {
        User user = findUserById(id);
        ensureSameUserOrAdmin(user, authentication);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void ensureSameUserOrAdmin(User user, Authentication authentication) {
        if (isAdmin(authentication) || user.getUsername().equals(authentication.getName())) {
            return;
        }
        throw new AccessDeniedException("You are not allowed to access this user");
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(Role.ROLE_ADMIN.name()));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}

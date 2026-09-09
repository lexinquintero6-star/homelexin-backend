package com.homelexin.controller;

import com.homelexin.dto.AuthRegisterDTO;
import com.homelexin.dto.AuthLoginDTO;
import com.homelexin.dto.UserDTO;
import com.homelexin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody AuthRegisterDTO request) {
        UserDTO userDTO = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody AuthLoginDTO request) {
        UserDTO userDTO = authService.login(request);
        return ResponseEntity.ok(userDTO);
    }
}

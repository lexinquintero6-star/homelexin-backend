package com.homelexin.controller;

import com.homelexin.dto.PropertyDTO;
import com.homelexin.entity.User;
import com.homelexin.service.PropertyService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        List<PropertyDTO> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        try {
            PropertyDTO property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(property);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(
            @Valid @RequestBody PropertyDTO propertyDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            PropertyDTO createdProperty = propertyService.createProperty(propertyDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyDTO propertyDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            PropertyDTO updatedProperty = propertyService.updateProperty(id, propertyDTO, currentUser);
            return ResponseEntity.ok(updatedProperty);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            propertyService.deleteProperty(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

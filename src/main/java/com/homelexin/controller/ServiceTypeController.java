package com.homelexin.controller;

import com.homelexin.dto.ServiceTypeDTO;
import com.homelexin.entity.User;
import com.homelexin.service.ServiceTypeService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import com.homelexin.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/service-types")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> getAllServiceTypes() {
        List<ServiceTypeDTO> serviceTypes = serviceTypeService.getAllServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> getServiceTypeById(@PathVariable Long id) {
        try {
            ServiceTypeDTO serviceType = serviceTypeService.getServiceTypeById(id);
            return ResponseEntity.ok(serviceType);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<ServiceTypeDTO> createServiceType(
            @Valid @RequestBody ServiceTypeDTO serviceTypeDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            ServiceTypeDTO createdServiceType = serviceTypeService.createServiceType(serviceTypeDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdServiceType);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> updateServiceType(
            @PathVariable Long id,
            @Valid @RequestBody ServiceTypeDTO serviceTypeDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            ServiceTypeDTO updatedServiceType = serviceTypeService.updateServiceType(id, serviceTypeDTO, currentUser);
            return ResponseEntity.ok(updatedServiceType);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceType(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            serviceTypeService.deleteServiceType(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

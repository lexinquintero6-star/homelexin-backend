package com.homelexin.controller;

import com.homelexin.dto.ServiceTypeDTO;
import com.homelexin.entity.User;
import com.homelexin.service.ServiceTypeService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> getAllServices() {
        List<ServiceTypeDTO> services = serviceTypeService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> getServiceById(@PathVariable Long id) {
        try {
            ServiceTypeDTO service = serviceTypeService.getServiceById(id);
            return ResponseEntity.ok(service);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<ServiceTypeDTO> createService(
            @Valid @RequestBody ServiceTypeDTO serviceTypeDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            ServiceTypeDTO createdService = serviceTypeService.createService(serviceTypeDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceTypeDTO serviceTypeDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            ServiceTypeDTO updatedService = serviceTypeService.updateService(id, serviceTypeDTO, currentUser);
            return ResponseEntity.ok(updatedService);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            serviceTypeService.deleteService(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

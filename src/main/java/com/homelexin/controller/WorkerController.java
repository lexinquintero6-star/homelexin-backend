package com.homelexin.controller;

import com.homelexin.dto.WorkerDTO;
import com.homelexin.entity.User;
import com.homelexin.service.WorkerService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping
    public ResponseEntity<List<WorkerDTO>> getAllWorkers() {
        List<WorkerDTO> workers = workerService.getAllWorkers();
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerDTO> getWorkerById(@PathVariable Long id) {
        try {
            WorkerDTO worker = workerService.getWorkerById(id);
            return ResponseEntity.ok(worker);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<WorkerDTO> createWorker(
            @Valid @RequestBody WorkerDTO workerDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            WorkerDTO createdWorker = workerService.createWorker(workerDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWorker);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerDTO> updateWorker(
            @PathVariable Long id,
            @Valid @RequestBody WorkerDTO workerDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            WorkerDTO updatedWorker = workerService.updateWorker(id, workerDTO, currentUser);
            return ResponseEntity.ok(updatedWorker);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

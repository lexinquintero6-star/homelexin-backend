package com.homelexin.controller;

import com.homelexin.dto.JobDTO;
import com.homelexin.dto.JobStatusUpdateDTO;
import com.homelexin.entity.User;
import com.homelexin.service.JobService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import com.homelexin.exception.InvalidOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs(
            @RequestAttribute("currentUser") User currentUser) {
        List<JobDTO> jobs = jobService.getJobsByUser(currentUser);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobDTO job = jobService.getJobById(id, currentUser);
            return ResponseEntity.ok(job);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(
            @Valid @RequestBody JobDTO jobDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobDTO createdJob = jobService.createJob(jobDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobDTO jobDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobDTO updatedJob = jobService.updateJob(id, jobDTO, currentUser);
            return ResponseEntity.ok(updatedJob);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            jobService.deleteJob(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}/assign/{workerId}")
    public ResponseEntity<JobDTO> assignWorker(
            @PathVariable Long id,
            @PathVariable Long workerId,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobDTO assignedJob = jobService.assignWorker(id, workerId, currentUser);
            return ResponseEntity.ok(assignedJob);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobDTO> updateJobStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobStatusUpdateDTO statusUpdateDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobDTO updatedJob = jobService.updateJobStatus(id, statusUpdateDTO.getStatus(), currentUser);
            return ResponseEntity.ok(updatedJob);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

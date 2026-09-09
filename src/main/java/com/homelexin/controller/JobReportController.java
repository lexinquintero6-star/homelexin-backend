package com.homelexin.controller;

import com.homelexin.dto.JobReportDTO;
import com.homelexin.dto.JobReportCreateUpdateDTO;
import com.homelexin.entity.User;
import com.homelexin.service.JobReportService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import com.homelexin.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs/{jobId}/report")
@RequiredArgsConstructor
public class JobReportController {

    private final JobReportService jobReportService;

    @GetMapping
    public ResponseEntity<JobReportDTO> getJobReport(
            @PathVariable Long jobId,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobReportDTO report = jobReportService.getJobReport(jobId, currentUser);
            return ResponseEntity.ok(report);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<JobReportDTO> createJobReport(
            @PathVariable Long jobId,
            @Valid @RequestBody JobReportCreateUpdateDTO jobReportDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobReportDTO createdReport = jobReportService.createJobReport(jobId, jobReportDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping
    public ResponseEntity<JobReportDTO> updateJobReport(
            @PathVariable Long jobId,
            @Valid @RequestBody JobReportCreateUpdateDTO jobReportDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobReportDTO updatedReport = jobReportService.updateJobReport(jobId, jobReportDTO, currentUser);
            return ResponseEntity.ok(updatedReport);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

package com.homelexin.controller;

import com.homelexin.dto.JobPhotoDTO;
import com.homelexin.entity.User;
import com.homelexin.service.JobPhotoService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/jobs/{jobId}/photos")
@RequiredArgsConstructor
public class JobPhotoController {

    private final JobPhotoService jobPhotoService;

    @GetMapping
    public ResponseEntity<List<JobPhotoDTO>> getJobPhotos(
            @PathVariable Long jobId,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            List<JobPhotoDTO> photos = jobPhotoService.getJobPhotos(jobId, currentUser);
            return ResponseEntity.ok(photos);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<JobPhotoDTO> uploadJobPhoto(
            @PathVariable Long jobId,
            @Valid @RequestBody JobPhotoDTO jobPhotoDTO,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            JobPhotoDTO uploadedPhoto = jobPhotoService.uploadJobPhoto(jobId, jobPhotoDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedPhoto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deleteJobPhoto(
            @PathVariable Long jobId,
            @PathVariable Long photoId,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            jobPhotoService.deleteJobPhoto(jobId, photoId, currentUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

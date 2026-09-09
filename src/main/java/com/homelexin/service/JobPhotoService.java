package com.homelexin.service;

import com.homelexin.dto.JobPhotoDTO;
import com.homelexin.entity.JobPhoto;
import com.homelexin.entity.Job;
import com.homelexin.entity.User;
import com.homelexin.repository.JobPhotoRepository;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPhotoService {

    private final JobPhotoRepository jobPhotoRepository;
    private final JobService jobService;

    public JobPhotoDTO createJobPhoto(Long jobId, String url, JobPhoto.PhotoType type, User uploadedBy) {
        Job job = jobService.findById(jobId);

        // Verify permissions: owner, admin, or assigned worker
        if (!job.getOwner().getId().equals(uploadedBy.getId()) && 
            !uploadedBy.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(uploadedBy.getId()))) {
            throw new UnauthorizedException("You don't have permission to add photos to this job");
        }

        JobPhoto photo = JobPhoto.builder()
                .job(job)
                .url(url)
                .type(type)
                .uploadedBy(uploadedBy)
                .build();

        JobPhoto savedPhoto = jobPhotoRepository.save(photo);
        return convertToDTO(savedPhoto);
    }

    public JobPhotoDTO getJobPhotoById(Long photoId, User currentUser) {
        JobPhoto photo = jobPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        Job job = photo.getJob();
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to access this photo");
        }

        return convertToDTO(photo);
    }

    public List<JobPhotoDTO> getPhotosByJob(Long jobId, User currentUser) {
        Job job = jobService.findById(jobId);

        // Verify permissions: owner, admin, or assigned worker
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to view photos for this job");
        }

        return jobPhotoRepository.findByJob(job).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobPhotoDTO> getPhotosByJobAndType(Long jobId, JobPhoto.PhotoType type, User currentUser) {
        Job job = jobService.findById(jobId);

        // Verify permissions
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to view photos for this job");
        }

        return jobPhotoRepository.findByJob(job).stream()
                .filter(photo -> photo.getType().equals(type))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteJobPhoto(Long photoId, User currentUser) {
        JobPhoto photo = jobPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        Job job = photo.getJob();
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !photo.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this photo");
        }

        jobPhotoRepository.delete(photo);
    }

    private JobPhotoDTO convertToDTO(JobPhoto photo) {
        return JobPhotoDTO.builder()
                .id(photo.getId())
                .jobId(photo.getJob().getId())
                .url(photo.getUrl())
                .type(photo.getType())
                .uploadedById(photo.getUploadedBy().getId())
                .uploadedByName(photo.getUploadedBy().getName())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}

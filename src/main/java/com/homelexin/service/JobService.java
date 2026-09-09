package com.homelexin.service;

import com.homelexin.dto.JobDTO;
import com.homelexin.dto.JobCreateUpdateDTO;
import com.homelexin.dto.JobStatusUpdateDTO;
import com.homelexin.entity.Job;
import com.homelexin.entity.Property;
import com.homelexin.entity.ServiceType;
import com.homelexin.entity.User;
import com.homelexin.entity.Worker;
import com.homelexin.entity.JobReport;
import com.homelexin.repository.JobRepository;
import com.homelexin.repository.JobReportRepository;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import com.homelexin.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final JobReportRepository jobReportRepository;
    private final PropertyService propertyService;
    private final ServiceTypeService serviceTypeService;
    private final WorkerService workerService;
    private final UserService userService;

    public JobDTO createJob(Long ownerId, JobCreateUpdateDTO request) {
        User owner = userService.findById(ownerId);
        Property property = propertyService.findById(request.getPropertyId());
        ServiceType serviceType = serviceTypeService.findById(request.getServiceTypeId());

        Job job = Job.builder()
                .property(property)
                .owner(owner)
                .serviceType(serviceType)
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Job.JobStatus.SOLICITADO)
                .requestedDate(LocalDateTime.now())
                .build();

        Job savedJob = jobRepository.save(job);
        return convertToDTO(savedJob);
    }

    public JobDTO getJobById(Long id, User currentUser) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to access this job");
        }

        return convertToDTO(job);
    }

    public List<JobDTO> getJobsByOwner(Long ownerId, User currentUser) {
        if (!currentUser.getId().equals(ownerId) && !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to view these jobs");
        }

        User owner = userService.findById(ownerId);
        return jobRepository.findByOwner(owner).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobDTO> getJobsByWorker(Long workerId) {
        Worker worker = workerService.findById(workerId);
        return jobRepository.findByAssignedWorker(worker).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobDTO> getJobsByStatus(Job.JobStatus status) {
        return jobRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public JobDTO updateJob(Long id, JobCreateUpdateDTO request, User currentUser) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to update this job");
        }

        Property property = propertyService.findById(request.getPropertyId());
        ServiceType serviceType = serviceTypeService.findById(request.getServiceTypeId());

        job.setProperty(property);
        job.setServiceType(serviceType);
        job.setDescription(request.getDescription());
        job.setPriority(request.getPriority());

        Job updatedJob = jobRepository.save(job);
        return convertToDTO(updatedJob);
    }

    public JobDTO assignWorker(Long jobId, Long workerId, User currentUser) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to assign workers to this job");
        }

        Worker worker = workerService.findById(workerId);
        job.setAssignedWorker(worker);
        job.setStatus(Job.JobStatus.ASIGNADO);

        Job updatedJob = jobRepository.save(job);
        return convertToDTO(updatedJob);
    }

    public JobDTO updateJobStatus(Long jobId, JobStatusUpdateDTO request, User currentUser) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to update this job status");
        }

        if (request.getStatus().equals(Job.JobStatus.COMPLETADO)) {
            JobReport report = jobReportRepository.findByJob(job).orElse(null);
            if (report == null) {
                throw new BusinessLogicException("Cannot mark job as completed without a report");
            }
        }

        job.setStatus(request.getStatus());

        if (request.getStatus().equals(Job.JobStatus.COMPLETADO)) {
            job.setCompletedDate(LocalDateTime.now());
        }

        Job updatedJob = jobRepository.save(job);
        return convertToDTO(updatedJob);
    }

    public void deleteJob(Long id, User currentUser) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to delete this job");
        }

        jobRepository.delete(job);
    }

    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    private JobDTO convertToDTO(Job job) {
        return JobDTO.builder()
                .id(job.getId())
                .propertyId(job.getProperty().getId())
                .propertyName(job.getProperty().getName())
                .ownerId(job.getOwner().getId())
                .ownerName(job.getOwner().getName())
                .serviceTypeId(job.getServiceType().getId())
                .serviceTypeName(job.getServiceType().getName())
                .description(job.getDescription())
                .priority(job.getPriority())
                .status(job.getStatus())
                .assignedWorkerId(job.getAssignedWorker() != null ? job.getAssignedWorker().getId() : null)
                .assignedWorkerName(job.getAssignedWorker() != null ? job.getAssignedWorker().getUser().getName() : null)
                .requestedDate(job.getRequestedDate())
                .scheduledDate(job.getScheduledDate())
                .completedDate(job.getCompletedDate())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}

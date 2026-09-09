package com.homelexin.service;

import com.homelexin.dto.JobReportDTO;
import com.homelexin.dto.JobReportCreateUpdateDTO;
import com.homelexin.entity.JobReport;
import com.homelexin.entity.Job;
import com.homelexin.entity.User;
import com.homelexin.repository.JobReportRepository;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import com.homelexin.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobReportService {

    private final JobReportRepository jobReportRepository;
    private final JobService jobService;

    public JobReportDTO createJobReport(Long jobId, JobReportCreateUpdateDTO request, User currentUser) {
        Job job = jobService.findById(jobId);

        // Verify permissions: owner, admin, or assigned worker
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to create a report for this job");
        }

        // Check if report already exists
        if (jobReportRepository.findByJob(job).isPresent()) {
            throw new ResourceAlreadyExistsException("A report already exists for this job");
        }

        JobReport report = JobReport.builder()
                .job(job)
                .description(request.getDescription())
                .workPerformed(request.getWorkPerformed())
                .materials(request.getMaterials())
                .observations(request.getObservations())
                .recommendations(request.getRecommendations())
                .build();

        JobReport savedReport = jobReportRepository.save(report);
        return convertToDTO(savedReport);
    }

    public JobReportDTO getJobReport(Long jobId, User currentUser) {
        Job job = jobService.findById(jobId);

        // Verify permissions: owner, admin, or assigned worker
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to view reports for this job");
        }

        JobReport report = jobReportRepository.findByJob(job)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found for job id: " + jobId));

        return convertToDTO(report);
    }

    public JobReportDTO updateJobReport(Long jobId, JobReportCreateUpdateDTO request, User currentUser) {
        Job job = jobService.findById(jobId);

        // Verify permissions: owner, admin, or assigned worker
        if (!job.getOwner().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN) &&
            !(job.getAssignedWorker() != null && job.getAssignedWorker().getUser().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedException("You don't have permission to update reports for this job");
        }

        JobReport report = jobReportRepository.findByJob(job)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found for job id: " + jobId));

        report.setDescription(request.getDescription());
        report.setWorkPerformed(request.getWorkPerformed());
        report.setMaterials(request.getMaterials());
        report.setObservations(request.getObservations());
        report.setRecommendations(request.getRecommendations());

        JobReport updatedReport = jobReportRepository.save(report);
        return convertToDTO(updatedReport);
    }

    public boolean reportExists(Long jobId) {
        Job job = jobService.findById(jobId);
        return jobReportRepository.findByJob(job).isPresent();
    }

    private JobReportDTO convertToDTO(JobReport report) {
        return JobReportDTO.builder()
                .id(report.getId())
                .jobId(report.getJob().getId())
                .description(report.getDescription())
                .workPerformed(report.getWorkPerformed())
                .materials(report.getMaterials())
                .observations(report.getObservations())
                .recommendations(report.getRecommendations())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}

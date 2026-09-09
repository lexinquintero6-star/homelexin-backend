package com.homelexin.repository;

import com.homelexin.entity.JobReport;
import com.homelexin.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobReportRepository extends JpaRepository<JobReport, Long> {
    Optional<JobReport> findByJob(Job job);
}

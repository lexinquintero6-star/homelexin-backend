package com.homelexin.repository;

import com.homelexin.entity.Job;
import com.homelexin.entity.User;
import com.homelexin.entity.Worker;
import com.homelexin.entity.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByOwner(User owner);
    List<Job> findByAssignedWorker(Worker worker);
    List<Job> findByStatus(JobStatus status);
    List<Job> findByOwnerAndStatus(User owner, JobStatus status);
    List<Job> findByAssignedWorkerAndStatus(Worker worker, JobStatus status);
}

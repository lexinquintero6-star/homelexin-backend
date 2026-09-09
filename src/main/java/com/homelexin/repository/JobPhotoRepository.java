package com.homelexin.repository;

import com.homelexin.entity.JobPhoto;
import com.homelexin.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPhotoRepository extends JpaRepository<JobPhoto, Long> {
    List<JobPhoto> findByJob(Job job);
}

package com.homelexin.repository;

import com.homelexin.entity.Worker;
import com.homelexin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByUser(User user);
    List<Worker> findByAvailable(Boolean available);
}

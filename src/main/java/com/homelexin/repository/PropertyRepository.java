package com.homelexin.repository;

import com.homelexin.entity.Property;
import com.homelexin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(User owner);
    Optional<Property> findByIdAndOwner(Long id, User owner);
}

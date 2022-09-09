package com.lec.ecommerse.repository;

import com.lec.ecommerse.domain.User;
import com.lec.ecommerse.exception.BadRequestException;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email) throws ResourceNotFoundException;

    boolean existsById(Long id) throws ResourceNotFoundException;

    @Modifying
    @Query("UPDATE User u " +
            "SET u.firstName = ?2, u.lastName = ?3, u.phoneNumber = ?4, " +
            "u.email = ?5, u.address = ?6, u.zipCode = ?7 " +
            "WHERE u.id = ?1")
    void update(Long id, String firstName, String lastName, String phoneNumber, String email, String address,
                String zipCode) throws BadRequestException;
}

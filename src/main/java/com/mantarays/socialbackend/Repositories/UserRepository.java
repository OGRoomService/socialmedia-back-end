package com.mantarays.socialbackend.Repositories;

import com.mantarays.socialbackend.Models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> 
{
    User findByUsername(String username);
}
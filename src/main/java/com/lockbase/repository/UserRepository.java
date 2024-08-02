package com.lockbase.repository;

import com.lockbase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("Select u from User u where u.email = :email")
    public User findUserByEmail(@Param("email") String email);

    @Query("Select u from User u where u.email = :email and u.id =:id")
    public User findUserByEmailAndId(@Param("email") String email, @Param("id") Integer id);

    Optional<User> findByUsername(String username);
}
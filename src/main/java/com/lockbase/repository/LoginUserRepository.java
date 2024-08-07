package com.lockbase.repository;

import com.lockbase.model.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginUserRepository extends JpaRepository<LoginUser, Integer> {

    @Query("Select u from LoginUser u where u.email = :email and u.username = :username")
    public LoginUser findUserByEmailAndUsername(@Param("email") String email,
                                                @Param("username") String username);

    @Query("Select u from LoginUser u where u.email = :email and u.id =:id")
    public LoginUser findUserByEmailAndId(@Param("email") String email, @Param("id") Integer id);

    Optional<LoginUser> findByUsername(String username);
}
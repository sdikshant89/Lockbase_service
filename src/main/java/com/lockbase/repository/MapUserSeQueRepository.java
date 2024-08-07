package com.lockbase.repository;

import com.lockbase.dto.SecAnsDTO;
import com.lockbase.model.MapUserSeQue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MapUserSeQueRepository extends JpaRepository<MapUserSeQue, Integer> {

    @Query("SELECT new com.lockbase.dto.SecAnsDTO(musq.securityQuestion.id, musq.securityQuestion" +
            ".question) from " +
            "MapUserSeQue musq where" +
            " musq.user.id = :userId")
    List<SecAnsDTO> findAnsByUserId(@Param("userId") Integer userId);
}
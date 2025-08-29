package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgrammingLanguageRepo extends JpaRepository<ProgrammingLanguage, Integer> {
    @Query("SELECT pg FROM ProgrammingLanguage pg WHERE pg.programmingLanguageId in :ids")
    List<ProgrammingLanguage> findByProgrammingLanguageIds(@Param("ids") List<Integer> ids);
}

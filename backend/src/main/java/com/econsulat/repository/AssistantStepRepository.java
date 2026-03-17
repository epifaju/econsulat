package com.econsulat.repository;

import com.econsulat.model.AssistantStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssistantStepRepository extends JpaRepository<AssistantStep, Long> {

    Optional<AssistantStep> findByStepId(String stepId);

    @Query("SELECT s FROM AssistantStep s LEFT JOIN FETCH s.choices c LEFT JOIN FETCH c.nextStep LEFT JOIN FETCH c.resultDocumentType ORDER BY s.displayOrder ASC, c.displayOrder ASC")
    List<AssistantStep> findAllWithChoicesOrdered();
}

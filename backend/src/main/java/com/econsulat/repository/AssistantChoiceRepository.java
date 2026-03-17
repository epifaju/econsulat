package com.econsulat.repository;

import com.econsulat.model.AssistantChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssistantChoiceRepository extends JpaRepository<AssistantChoice, Long> {

    List<AssistantChoice> findByStep_IdOrderByDisplayOrderAsc(Long stepId);
}

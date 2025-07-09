package com.econsulat.repository;

import com.econsulat.model.Civilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiviliteRepository extends JpaRepository<Civilite, Long> {
}
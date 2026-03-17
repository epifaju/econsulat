package com.econsulat.repository;

import com.econsulat.model.FaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqCategoryRepository extends JpaRepository<FaqCategory, Long> {

    @Query("SELECT DISTINCT c FROM FaqCategory c LEFT JOIN FETCH c.entries ORDER BY c.displayOrder ASC")
    List<FaqCategory> findAllWithEntriesOrdered();
}

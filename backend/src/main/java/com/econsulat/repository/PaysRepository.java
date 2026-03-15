package com.econsulat.repository;

import com.econsulat.model.Pays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaysRepository extends JpaRepository<Pays, Long> {

    /**
     * Liste des pays ordonnés : Guinée-Bissau, France, Portugal, Sénégal en premier,
     * puis les autres par ordre alphabétique du libellé.
     */
    @Query(value = """
        SELECT * FROM pays
        ORDER BY CASE libelle
            WHEN 'Guinée-Bissau' THEN 1
            WHEN 'France' THEN 2
            WHEN 'Portugal' THEN 3
            WHEN 'Sénégal' THEN 4
            ELSE 5
        END, libelle
        """, nativeQuery = true)
    List<Pays> findAllOrdered();
}
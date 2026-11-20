package com.example.territorio.persistencia;

import com.example.territorio.dominio.Medicao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorIdOrderByMomentoDesc(String sensorId);

    /** Leituras mais recentes primeiro. Use PageRequest.of(0, n) para limitar. */
    @Query("select m from Medicao m where m.sensorId = :sensorId order by m.momento desc")
    List<Medicao> buscarRecentes(@Param("sensorId") String sensorId, Pageable pageable);
}

package com.example.territorio.persistencia;

import com.example.territorio.dominio.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findAllByOrderByMomentoDesc();
}

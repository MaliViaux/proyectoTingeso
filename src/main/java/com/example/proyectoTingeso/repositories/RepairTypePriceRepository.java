package com.example.proyectoTingeso.repositories;

import com.example.proyectoTingeso.entities.RepairTypePriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTypePriceRepository extends JpaRepository<RepairTypePriceEntity, Long> {
    RepairTypePriceEntity findByNameAndEngineType(String name, String engineType);
}

package com.example.proyectoTingeso.repositories;

import com.example.proyectoTingeso.entities.RepairRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepairRecordRepository extends JpaRepository<RepairRecordEntity, Long> {
    @Query("SELECT r FROM RepairRecordEntity r WHERE r.car.id = :carId AND r.entryDate BETWEEN :startDate AND :endDate")
    List<RepairRecordEntity> findRecordsByCarIdAndEntryDateBetween(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT r.car.carBrand, r.entryDate, r.exitDate " +
            "FROM RepairRecordEntity r " +
            "WHERE r.car.carBrand IS NOT NULL AND r.exitDate IS NOT NULL")
    List<Object[]> findBrandAndRepairDates();
    @Query("SELECT rtp.repairTypeNumber, rtp.engineType, COUNT(rr), SUM(rtp.price) " +
            "FROM RepairRecordEntity rr " +
            "JOIN rr.repairTypesPrices rtp " +
            "GROUP BY rtp.repairTypeNumber, rtp.engineType ")
    List<Object[]> findRepairTypeEngineStats();
    @Query("SELECT rtp.repairTypeNumber, rr.car.carType, COUNT(rr), SUM(rtp.price) " +
            "FROM RepairRecordEntity rr " +
            "JOIN rr.repairTypesPrices rtp " +
            "GROUP BY rtp.repairTypeNumber, rr.car.carType ")
    List<Object[]> findRepairTypeCarStats();
}
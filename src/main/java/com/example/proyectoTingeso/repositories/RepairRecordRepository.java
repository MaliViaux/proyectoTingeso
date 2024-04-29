package com.example.proyectoTingeso.repositories;

import com.example.proyectoTingeso.entities.RepairRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface RepairRecordRepository extends JpaRepository<RepairRecordEntity, Long> {
    List<RepairRecordEntity> findByCar_CarBrand(String caBrand);
    @Query("SELECT r FROM RepairRecordEntity r JOIN r.repairTypesPrices rp WHERE rp.repairTypeNumber = :repairTypeNumber AND rp.engineType = :engineType")
    List<RepairRecordEntity> findByRepairTypeNumberAndEngineType(@Param("repairTypeNumber") Integer repairTypeNumber, @Param("engineType") String engineType);
    @Query("SELECT r FROM RepairRecordEntity r JOIN r.car c JOIN r.repairTypesPrices rp WHERE c.carType = :carType AND rp.repairTypeNumber = :repairTypeNumber")
    List<RepairRecordEntity> findByRepairTypeNumberAndCarType(@Param("carType") String carType, @Param("repairTypeNumber") Integer repairTypeNumber);
    @Query("SELECT r FROM RepairRecordEntity r WHERE r.car.id = :carId AND r.entryDate BETWEEN :startDate AND :endDate")
    List<RepairRecordEntity> findRecordsByCarIdAndEntryDateBetween(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT r.car.carBrand, SUM(DATEDIFF(r.exitDate, r.entryDate)), COUNT(r) " +
            "FROM RepairRecordEntity r " +
            "WHERE r.car.carBrand IS NOT NULL AND r.exitDate IS NOT NULL " +
            "GROUP BY r.car.carBrand")
    List<Object[]> findTotalRepairTimeAndCountByBrand();
}
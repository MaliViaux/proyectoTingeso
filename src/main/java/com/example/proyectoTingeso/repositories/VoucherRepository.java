package com.example.proyectoTingeso.repositories;

import com.example.proyectoTingeso.entities.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    VoucherEntity findByVoucherMonthAndVoucherYearAndBrandAndNumberOfVouchersGreaterThan(
            String month, Integer year, String brand, Integer numberOfVouchersThreshold);
}

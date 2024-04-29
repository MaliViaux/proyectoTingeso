package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.RepairRecordEntity;
import com.example.proyectoTingeso.entities.RepairTypePriceEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairRecordRepository;
import com.example.proyectoTingeso.repositories.RepairTypePriceRepository;
import com.example.proyectoTingeso.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalculationService {
    @Autowired
    RepairRecordRepository repairRecordRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    RepairTypePriceRepository repairTypePriceRepository;
    @Autowired
    VoucherRepository voucherRepository;

    private double iva = 0.19;

    public RepairRecordEntity calculateAndUpdateTotalRepairCost(Long repairRecordId) {
        RepairRecordEntity repairRecord = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id:" + repairRecordId));

        // Calcular el total de los costos de reparación
        Integer totalCost = repairRecord.getRepairTypesPrices().stream()
                .mapToInt(RepairTypePriceEntity::getPrice)
                .sum();

        repairRecord.setTotalRepairCost(totalCost);
        return repairRecordRepository.save(repairRecord);
    }

    public RepairRecordEntity calculateAndUpdateDiscountAmountEntryDate(Long repairRecordId) {
        RepairRecordEntity repairRecord = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id :: " + repairRecordId));

        final double discountPercentageForEntryDate = 0.10; // 10% de descuento por el dia y hora

        // Verificar si entro un lunes o jueves entre las 09:00 y las 12:00
        DayOfWeek dayOfWeek = repairRecord.getEntryDate().getDayOfWeek();
        boolean isDiscountDay = (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.THURSDAY);
        LocalTime entryTime = repairRecord.getEntryTime().toLocalTime();;
        boolean isDiscountTime = (!entryTime.isBefore(LocalTime.of(13, 0)) &&
                !entryTime.isAfter(LocalTime.of(16, 0)));

        // Calcular el descuento si aplica.
        if (isDiscountDay && isDiscountTime) {
            double discount = repairRecord.getTotalRepairCost() * discountPercentageForEntryDate;
            repairRecord.setDiscountAmountEntryDate((int) discount);
        } else {
            repairRecord.setDiscountAmountEntryDate(0);
        }

        return repairRecordRepository.save(repairRecord);
    }

    public RepairRecordEntity calculateAndUpdateAgeAndMileageChargeAmount(Long repairRecordId) {
        RepairRecordEntity repairRecord = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id :: " + repairRecordId));

        int currentYear = java.time.Year.now().getValue(); // Año actual para calcular la antigüedad del vehículo.

        // Calcular los montos de recargo.
        double ageChargeAmount = repairRecord.getTotalRepairCost() *
                determineAgeChargePercentage(currentYear - repairRecord.getCar().getCarYear(),
                        repairRecord.getCar().getCarType());
        double mileageChargeAmount = repairRecord.getTotalRepairCost() *
                determineMileageChargePercentage(repairRecord.getCar().getCarMileage(), repairRecord.getCar().getCarType());

        repairRecord.setChargeAmountAge((int) ageChargeAmount);
        repairRecord.setChargeAmountMileage((int) mileageChargeAmount);
        return repairRecordRepository.save(repairRecord);
    }

    private double determineAgeChargePercentage(int carAge, String carType) {
        if (carAge <= 5) {
            return 0.0;
        } else if (carAge <= 10) {
            return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.05 : 0.07;
        } else if (carAge <= 15) {
            return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.09 : 0.11;
        } else {
            return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.15 : 0.20;
        }
    }

    private double determineMileageChargePercentage(int mileage, String carType) {
        if (mileage <= 5000) {
            return 0.0;
        } else if (mileage <= 12000) {
            return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.03 : 0.05;
        } else if (mileage <= 25000) {
            return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.07 : 0.09;
        } else if (mileage <= 40000) {
            return 0.12;
        } else {
            return 0.20;
        }
    }

    public RepairRecordEntity calculateAndUpdateDelayCharge(Long repairRecordId) {
        RepairRecordEntity repairRecord = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id: " + repairRecordId));

        final double dailyDelayChargePercentage = 0.05; // 5% de recargo por día de retraso
        LocalDate today = LocalDate.now(); // Fecha actual
        long daysDelayed = ChronoUnit.DAYS.between(repairRecord.getExitDate(), today); // Días de retraso

        // Asegurarse de que exitDate esté definido
        if (repairRecord.getExitDate() == null) {
            throw new RuntimeException("Exit date is not defined for repair record id :: " + repairRecordId);
        }

        if (daysDelayed > 0) {
            double delayCharge = repairRecord.getTotalRepairCost() * dailyDelayChargePercentage * daysDelayed;
            repairRecord.setChargeAmountDelay((int) delayCharge);
            return repairRecordRepository.save(repairRecord);
        } else {
            repairRecord.setChargeAmountDelay(0);
        }

        return repairRecord;
    }

    public RepairRecordEntity calculateAndUpdateDiscountAmountNumberOfRepairs(Long repairRecordId) {
        RepairRecordEntity repairRecord = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id: " + repairRecordId));

        Integer repairsWithinLastYear = getRepairsForCarWithinLastYear(repairRecord.getCar().getId());

        if (repairsWithinLastYear-1 > 0) {
            double DiscountAmountNumberOfRepairs = repairRecord.getTotalRepairCost() *
                    calculateDiscountPercentage(repairsWithinLastYear-1, repairRecord.getCar().getEngineType());
            repairRecord.setDiscountAmountNumberOfRepairs((int) DiscountAmountNumberOfRepairs);
            return repairRecordRepository.save(repairRecord);
        }

        repairRecord.setDiscountAmountNumberOfRepairs(0);
        return repairRecord;
    }

    public Integer getRepairsForCarWithinLastYear(Long carId) {
        LocalDate today = LocalDate.now();
        LocalDate twelveMonthsAgo = today.minusMonths(12);
        List<RepairRecordEntity> records = repairRecordRepository.findRecordsByCarIdAndEntryDateBetween(carId, twelveMonthsAgo, today);
        return records.size();
    }

    private double calculateDiscountPercentage(int repairCount, String engineType) {
        Map<String, double[]> discountMap = new HashMap<>();
        discountMap.put("Gasolina", new double[]{0.05, 0.10, 0.15, 0.20});
        discountMap.put("Diesel", new double[]{0.07, 0.12, 0.17, 0.22});
        discountMap.put("Hibrido", new double[]{0.10, 0.15, 0.20, 0.25});
        discountMap.put("Electrico", new double[]{0.08, 0.13, 0.18, 0.23});

        int index;
        if (repairCount >= 10) {
            index = 3;
        } else if (repairCount >= 6) {
            index = 2;
        } else if (repairCount >= 3) {
            index = 1;
        } else {
            index = 0;
        }

        return discountMap.getOrDefault(engineType, new double[]{0.0})[index];
    }

    public RepairRecordEntity calculateFinalCost(Long repairRecordId) {
        RepairRecordEntity record = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id :: " + repairRecordId));

        record.setFinalCost(record.getTotalRepairCost() - record.getDiscountAmountEntryDate() -
                record.getDiscountAmountNumberOfRepairs() - record.getDiscountAmountVoucher() +
                record.getChargeAmountAge() + record.getChargeAmountMileage() + record.getChargeAmountDelay());
        record.setIva((int) (record.getFinalCost() * iva));
        record.setFinalCost(record.getFinalCost() + record.getIva());

        return repairRecordRepository.save(record);
    }
}

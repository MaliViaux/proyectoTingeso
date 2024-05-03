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

    public RepairRecordEntity calculateAndUpdateTotalRepairCost(RepairRecordEntity Record) {
        // Calcular el total de los costos de reparación
        Integer totalCost = Record.getRepairTypesPrices().stream()
                .mapToInt(RepairTypePriceEntity::getPrice)
                .sum();

        Record.setTotalRepairCost(totalCost);
        return repairRecordRepository.save(Record);
    }

    public RepairRecordEntity calculateAndUpdateDiscountAmountEntryDate(RepairRecordEntity record) {

        final double discountPercentageForEntryDate = 0.10; // 10% de descuento por el dia y hora

        // Verificar si entro un lunes o jueves entre las 09:00 y las 12:00
        DayOfWeek dayOfWeek = record.getEntryDate().getDayOfWeek();
        boolean isDiscountDay = (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.THURSDAY);
        LocalTime entryTime = record.getEntryTime().toLocalTime();;
        boolean isDiscountTime = (!entryTime.isBefore(LocalTime.of(13, 0)) &&
                !entryTime.isAfter(LocalTime.of(16, 0)));

        // Calcular el descuento si aplica.
        if (isDiscountDay && isDiscountTime) {
            double discount = record.getTotalRepairCost() * discountPercentageForEntryDate;
            record.setDiscountAmountEntryDate((int) discount);
        } else {
            record.setDiscountAmountEntryDate(0);
        }

        return repairRecordRepository.save(record);
    }

    public RepairRecordEntity calculateAndUpdateAgeAndMileageChargeAmount(RepairRecordEntity record) {

        int currentYear = java.time.Year.now().getValue(); // Año actual para calcular la antigüedad del vehículo.

        // Calcular los montos de recargo.
        double ageChargeAmount = record.getTotalRepairCost() *
                determineAgeChargePercentage(currentYear - record.getCar().getCarYear(),
                        record.getCar().getCarType());
        double mileageChargeAmount = record.getTotalRepairCost() *
                determineMileageChargePercentage(record.getCar().getCarMileage(), record.getCar().getCarType());

        record.setChargeAmountAge((int) ageChargeAmount);
        record.setChargeAmountMileage((int) mileageChargeAmount);
        return repairRecordRepository.save(record);
    }

    private double determineAgeChargePercentage(int carAge, String carType) {
        if (carAge <= 5) { return 0.0;
        } else if (carAge <= 10) { return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.05 : 0.07;
        } else if (carAge <= 15) { return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.09 : 0.11;
        } else { return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.15 : 0.20; }
    }

    private double determineMileageChargePercentage(int mileage, String carType) {
        if (mileage <= 5000) { return 0.0;
        } else if (mileage <= 12000) { return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.03 : 0.05;
        } else if (mileage <= 25000) { return (carType.equals("Sedan") || carType.equals("Hatchback")) ? 0.07 : 0.09;
        } else if (mileage <= 40000) { return 0.12;
        } else { return 0.20; }
    }

    public RepairRecordEntity calculateAndUpdateDelayCharge(RepairRecordEntity record) {

        final double dailyDelayChargePercentage = 0.05; // 5% de recargo por día de retraso
        LocalDate today = LocalDate.now(); // Fecha actual
        long daysDelayed = ChronoUnit.DAYS.between(record.getExitDate(), today); // Días de retraso

        // Asegurarse de que exitDate esté definido
        if (record.getExitDate() == null) {
            throw new RuntimeException("Exit date is not defined for repair record id :: " + record.getId());
        }

        if (daysDelayed > 0) {
            double delayCharge = record.getTotalRepairCost() * dailyDelayChargePercentage * daysDelayed;
            record.setChargeAmountDelay((int) delayCharge);
            return repairRecordRepository.save(record);
        } else {
            record.setChargeAmountDelay(0);
        }

        return record;
    }

    public RepairRecordEntity calculateAndUpdateDiscountAmountNumberOfRepairs(RepairRecordEntity record) {
        Integer repairsWithinLastYear = getRepairsForCarWithinLastYear(record.getCar().getId());

        if (repairsWithinLastYear > 0) {
            double DiscountAmountNumberOfRepairs = record.getTotalRepairCost() *
                    calculateDiscountPercentage(repairsWithinLastYear, record.getCar().getEngineType());
            record.setDiscountAmountNumberOfRepairs((int) DiscountAmountNumberOfRepairs);
            return repairRecordRepository.save(record);
        }

        record.setDiscountAmountNumberOfRepairs(0);
        return record;
    }

    public Integer getRepairsForCarWithinLastYear(Long carId) {
        LocalDate today = LocalDate.now();
        LocalDate twelveMonthsAgo = today.minusMonths(12);
        List<RepairRecordEntity> records = repairRecordRepository.findRecordsByCarIdAndEntryDateBetween(carId, twelveMonthsAgo, today);
        return records.size()-1;
    }

    private double calculateDiscountPercentage(int repairCount, String engineType) {
        Map<String, double[]> discountMap = new HashMap<>();
        discountMap.put("Gasolina", new double[]{0.05, 0.10, 0.15, 0.20});
        discountMap.put("Diesel", new double[]{0.07, 0.12, 0.17, 0.22});
        discountMap.put("Hibrido", new double[]{0.10, 0.15, 0.20, 0.25});
        discountMap.put("Electrico", new double[]{0.08, 0.13, 0.18, 0.23});

        int index;
        if (repairCount >= 10) { index = 3;
        } else if (repairCount >= 6) { index = 2;
        } else if (repairCount >= 3) { index = 1;
        } else { index = 0; }

        return discountMap.getOrDefault(engineType, new double[]{0.0})[index];
    }

    public RepairRecordEntity calculateFinalCost(RepairRecordEntity record) {
        record.setFinalCost(record.getTotalRepairCost() - record.getDiscountAmountEntryDate() -
                record.getDiscountAmountNumberOfRepairs() - record.getDiscountAmountVoucher() +
                record.getChargeAmountAge() + record.getChargeAmountMileage() + record.getChargeAmountDelay());
        record.setIva((int) (record.getFinalCost() * iva));
        record.setFinalCost(record.getFinalCost() + record.getIva());

        return repairRecordRepository.save(record);
    }
}

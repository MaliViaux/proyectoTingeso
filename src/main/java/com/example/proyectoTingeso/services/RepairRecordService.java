package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.entities.RepairRecordEntity;
import com.example.proyectoTingeso.entities.RepairTypePriceEntity;
import com.example.proyectoTingeso.entities.VoucherEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairRecordRepository;
import com.example.proyectoTingeso.repositories.RepairTypePriceRepository;
import com.example.proyectoTingeso.repositories.VoucherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RepairRecordService {
    @Autowired
    RepairRecordRepository repairRecordRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    RepairTypePriceRepository repairTypePriceRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    CalculationService calculationService;

    private double iva = 0.19;

    public ArrayList<RepairRecordEntity> getRepairRecords() {
        return (ArrayList<RepairRecordEntity>) repairRecordRepository.findAll(); }

    public RepairRecordEntity getRepairRecordById(Long id){
        return repairRecordRepository.findById(id).get();
    }

    public List<RepairRecordEntity> getRepairRecordsByBrand(String brand){
        return repairRecordRepository.findByCar_CarBrand(brand);
    }

    public List<RepairRecordEntity> getRepairRecordsByRepairTypeNumberAndCarType(String carType, Integer repairTypeNumber) {
        return repairRecordRepository.findByRepairTypeNumberAndCarType(carType, repairTypeNumber);
    }

    public List<RepairRecordEntity> getRepairRecordsByRepairTypeNumberAndEngineType(String engineType, Integer repairTypeNumber) {
        return repairRecordRepository.findByRepairTypeNumberAndEngineType(repairTypeNumber, engineType);
    }

    public List<RepairRecordEntity> getRepairRecordsByCarIdAndEntryDateBetween(Long carId, LocalDate startDate, LocalDate endDate) {
        return repairRecordRepository.findRecordsByCarIdAndEntryDateBetween(carId, startDate, endDate);
    }

    public RepairRecordEntity saveRepairRecord(RepairRecordEntity repairRecord){
        CarEntity car = carRepository.findByCarPlate(repairRecord.getCarPlate()).orElseThrow(
                () -> new EntityNotFoundException("Car with plate " + repairRecord.getCarPlate() + " not found."));
        repairRecord.setCar(car);

        // Busca los RepairTypePriceEntity por cada nombre en repairTypeNames y segun motor
        Set<RepairTypePriceEntity> repairTypePrices = new HashSet<>();
        for (String repairTypeName : repairRecord.getRepairTypeNames()) {
            RepairTypePriceEntity foundRepairTypePrices = repairTypePriceRepository.findByNameAndEngineType(repairTypeName, car.getEngineType());
            repairTypePrices.add(foundRepairTypePrices);
        }

        // Asigna los RepairTypePriceEntity encontrados al repairRecord
        repairRecord.setRepairTypesPrices(repairTypePrices);
        repairRecordRepository.save(repairRecord);
        calculationService.calculateAndUpdateTotalRepairCost(repairRecord.getId());
        calculationService.calculateAndUpdateDiscountAmountEntryDate(repairRecord.getId());
        calculationService.calculateAndUpdateAgeAndMileageChargeAmount(repairRecord.getId());
        calculationService.calculateAndUpdateDiscountAmountNumberOfRepairs(repairRecord.getId());
        repairRecord.setDiscountAmountVoucher(0);
        repairRecord.setChargeAmountDelay(0);
        calculationService.calculateFinalCost(repairRecord.getId());
        isVoucherAvailable(repairRecord);
        repairRecord.setIsVoucherApplied(false);
        car.setNumberOfRecords(car.getNumberOfRecords() + 1);

        return repairRecordRepository.save(repairRecord);
    }

    public RepairRecordEntity updateRepairRecord(RepairRecordEntity repairRecord) {
        return repairRecordRepository.save(repairRecord);
    }

    public boolean deleteRepairRecord(Long id) throws Exception {
        try{
            RepairRecordEntity record = repairRecordRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Repair record not found for this id:" + id));
            if (record.getIsVoucherApplied()){
                record.getVoucher().setNumberOfVouchers(record.getVoucher().getNumberOfVouchers() + 1);
                record.getVoucher().setNumberOfRecords(record.getVoucher().getNumberOfRecords() - 1);
            }
            record.getCar().setNumberOfRecords(record.getCar().getNumberOfRecords() - 1);
            repairRecordRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void finalizeRecord(Long repairRecordId) {
        RepairRecordEntity record = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id: " + repairRecordId));

        // Establecer la fecha y hora de salida al tiempo actual
        LocalDateTime now = LocalDateTime.now();
        record.setExitDate(now.toLocalDate());
        record.setExitTime(LocalDateTime.now());

        repairRecordRepository.save(record);
    }

    public void pickupRecord(Long repairRecordId) {
        RepairRecordEntity record = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id: " + repairRecordId));

        // Establecer la fecha y hora de salida al tiempo actual
        LocalDateTime now = LocalDateTime.now();
        record.setPickupDate(now.toLocalDate());
        record.setPickupTime(LocalDateTime.now());
        calculationService.calculateAndUpdateDelayCharge(record.getId());
        calculationService.calculateFinalCost(record.getId());
        if (record.getVoucher() == null){
            record.setDiscountAmountVoucher(0);
        }

        repairRecordRepository.save(record);
    }

    public RepairRecordEntity isVoucherAvailable(RepairRecordEntity repairRecord) {
        // Buscar voucher por mes año y marca
        VoucherEntity voucher = voucherRepository.
                findByMonthAndYearAndBrandAndNumberOfVouchersGreaterThan(
                        monthInSpanish(repairRecord.getEntryDate().getMonth().getValue()),
                        repairRecord.getEntryDate().getYear(),
                        repairRecord.getCar().getCarBrand(),0);

        if (voucher != null) {
            repairRecord.setIsVoucherAvailable(true);
        }

        return repairRecord;
    }

    public void applyVoucher(Long repairRecordId) {
        RepairRecordEntity record = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id: " + repairRecordId));

        record.setIsVoucherApplied(true);
        VoucherEntity voucher = voucherRepository.
                findByMonthAndYearAndBrandAndNumberOfVouchersGreaterThan(
                        monthInSpanish(record.getEntryDate().getMonth().getValue()),
                        record.getEntryDate().getYear(),
                        record.getCar().getCarBrand(),0);
        voucher.setNumberOfVouchers(voucher.getNumberOfVouchers() - 1);
        record.setVoucher(voucher);
        record.setDiscountAmountVoucher(voucher.getDiscountAmount());
        record.getVoucher().setNumberOfRecords(record.getVoucher().getNumberOfRecords() + 1);
        calculationService.calculateFinalCost(record.getId());

        repairRecordRepository.save(record);
    }

    public void removeVoucher(Long repairRecordId) {
        RepairRecordEntity record = repairRecordRepository.findById(repairRecordId)
                .orElseThrow(() -> new RuntimeException("Repair record not found for this id: " + repairRecordId));

        record.setIsVoucherApplied(false);
        VoucherEntity voucher = voucherRepository.
                findByMonthAndYearAndBrandAndNumberOfVouchersGreaterThan(
                        monthInSpanish(record.getEntryDate().getMonth().getValue()),
                        record.getEntryDate().getYear(),
                        record.getCar().getCarBrand(),0);
        voucher.setNumberOfVouchers(voucher.getNumberOfVouchers() + 1);
        record.getVoucher().setNumberOfRecords(record.getVoucher().getNumberOfRecords() - 1);
        record.setDiscountAmountVoucher(0);
        calculationService.calculateFinalCost(record.getId());
        record.setVoucher(null);

        repairRecordRepository.save(record);
    }

    public String monthInSpanish(Integer month) {
        String monthName;

        switch (month) {
            case 1:
                monthName = "Enero";
                break;
            case 2:
                monthName = "Febrero";
                break;
            case 3:
                monthName = "Marzo";
                break;
            case 4:
                monthName = "Abril";
                break;
            case 5:
                monthName = "Mayo";
                break;
            case 6:
                monthName = "Junio";
                break;
            case 7:
                monthName = "Julio";
                break;
            case 8:
                monthName = "Agosto";
                break;
            case 9:
                monthName = "Septiembre";
                break;
            case 10:
                monthName = "Octubre";
                break;
            case 11:
                monthName = "Noviembre";
                break;
            case 12:
                monthName = "Diciembre";
                break;
            default:
                monthName = "Mes inválido";
                break;
        }

        return monthName;
    }
}

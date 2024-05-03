package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.entities.RepairRecordEntity;
import com.example.proyectoTingeso.entities.RepairTypePriceEntity;
import com.example.proyectoTingeso.entities.VoucherEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairTypePriceRepository;
import com.example.proyectoTingeso.repositories.VoucherRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RepairRecordServiceTest {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private RepairTypePriceRepository repairTypePriceRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private RepairRecordService recordService;

    private RepairRecordEntity record1;
    private RepairRecordEntity record2;
    private RepairRecordEntity record3;
    private RepairRecordEntity record4;
    private VoucherEntity voucher;

    @BeforeEach
    void setup() {
        carRepository.save(new CarEntity(15L, "DHJJ99", "Mazda", "Sedan",
                "Gasolina", 2011, 26000));
        carRepository.save(new CarEntity(16L, "SDFG44", "Ford", "PickUp",
                "Diesel", 2000, 41000));
        carRepository.save(new CarEntity(17L, "GHJK49", "Hyundai", "HatchBack",
                "Hibrido", 2016, 13000));
        carRepository.save(new CarEntity(18L, "QWER79", "Audi", "SUV",
                "Electrico", 2021, 1000));

        repairTypePriceRepository.save(new RepairTypePriceEntity(21L,1,"BrakeRepair",
                "Gasolina",120000));
        repairTypePriceRepository.save(new RepairTypePriceEntity(22L,2,"EngineRepair",
                "Diesel",350000));
        repairTypePriceRepository.save(new RepairTypePriceEntity(23L,2,"EngineRepair",
                "Gasolina",300000));
        repairTypePriceRepository.save(new RepairTypePriceEntity(24L,2,"EngineRepair",
                "Hibrido",400000));
        repairTypePriceRepository.save(new RepairTypePriceEntity(25L,2,"EngineRepair",
                "Electrico",500000));

        LocalDate date = LocalDate.of(2024, 4, 29);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(13, 30));
        LocalDate date2 = LocalDate.of(2024, 4, 27);
        LocalDateTime dateTime2 = LocalDateTime.of(date2, LocalTime.of(9, 30));
        record1 = new RepairRecordEntity(1L, "DHJJ99", date, dateTime);
        record2 = new RepairRecordEntity(2L, "SDFG44", date2, dateTime2);
        record3 = new RepairRecordEntity(3L, "GHJK49", date, dateTime);
        record4 = new RepairRecordEntity(4L, "QWER79", date2, dateTime2);
        record1.setRepairTypeNames(Arrays.asList("BrakeRepair"));
        record2.setRepairTypeNames(Arrays.asList("EngineRepair"));
        record3.setRepairTypeNames(Arrays.asList("EngineRepair"));
        record4.setRepairTypeNames(Arrays.asList("EngineRepair"));

        voucher = new VoucherEntity(5L, null, 15000,
                2, "Mazda", "Abril", 2024, 0);
        voucherRepository.save(voucher);
    }

    @Test
    @Transactional
    public void whenGetRepairRecords_thenListIsReturned() {
        // Given
        RepairRecordEntity savedRecord1 = recordService.saveRepairRecord(record1);
        RepairRecordEntity savedRecord2 = recordService.saveRepairRecord(record2);
        RepairRecordEntity savedRecord3 = recordService.saveRepairRecord(record3);
        RepairRecordEntity savedRecord4 = recordService.saveRepairRecord(record4);
        List<RepairRecordEntity> expectedRecords =
                Arrays.asList(savedRecord1,savedRecord2,savedRecord3,savedRecord4);

        // When
        ArrayList<RepairRecordEntity> actualRecords = recordService.getRepairRecords();

        // Then
        assertThat(actualRecords).containsExactlyElementsOf(expectedRecords);
    }

    @Test
    @Transactional
    public void whenGetRepairRecordById_thenRecordIsReturned() {
        // Given
        RepairRecordEntity savedRecord1 = recordService.saveRepairRecord(record1);
        Long id = 1L;

        // When
        RepairRecordEntity foundRecord = recordService.getRepairRecordById(id);

        // Then
        assertThat(foundRecord).isEqualTo(savedRecord1);
    }

    @Test
    @Transactional
    public void whenSaveRepairRecord_thenRecordIsSaved() {
        // Given
        record1.setRepairTypeNames(Arrays.asList("BrakeRepair", "EngineRepair"));

        // When
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);

        // Then
        assertThat(savedRecord.getCar().getCarPlate()).isEqualTo(record1.getCarPlate());
        assertThat(savedRecord.getDiscountAmountVoucher()).isEqualTo(0);
        assertThat(savedRecord.getChargeAmountDelay()).isEqualTo(0);
        assertThat(savedRecord.getIsVoucherApplied()).isEqualTo(false);
        assertThat(savedRecord.getTotalRepairCost()).isEqualTo(420000);
    }

    @Test
    @Transactional
    public void whenRecordNumberGreaterThanZero_thenDiscountAmountMatches() {
        // Given
        LocalDate date2 = LocalDate.of(2024, 3, 20);
        LocalDateTime dateTime2 = LocalDateTime.of(date2, LocalTime.of(9, 30));
        RepairRecordEntity newRecord = new RepairRecordEntity(5L, "DHJJ99", date2, dateTime2);
        newRecord.setRepairTypeNames(Arrays.asList("EngineRepair"));
        recordService.saveRepairRecord(newRecord);

        // When
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);

        // Then
        assertThat(savedRecord.getDiscountAmountNumberOfRepairs()).isEqualTo(12000);
    }

    @Test
    @Transactional
    public void whenUpdateRepairRecord_thenRecordIsUpdated() {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        savedRecord.setCarPlate("PCCS18"); // Make some change

        // When
        RepairRecordEntity updatedRecord = recordService.updateRepairRecord(savedRecord);

        // Then
        assertThat(updatedRecord).isNotNull();
        assertThat(updatedRecord.getCarPlate()).isEqualTo("PCCS18");
    }

    @Test
    @Transactional
    public void whenDeleteRepairRecord_thenRecordIsDeleted() throws Exception {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();

        // When
        boolean result = recordService.deleteRepairRecord(id);

        // Then
        assertThat(result).isTrue();
        assertThrows(Exception.class, () -> recordService.getRepairRecordById(id));
    }

    @Test
    @Transactional
    public void whenDeleteRepairRecordWithVoucher_thenRecordIsDeleted() throws Exception {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();
        recordService.applyVoucher(id);

        // When
        boolean result = recordService.deleteRepairRecord(id);

        // Then
        assertThat(result).isTrue();
        assertThrows(Exception.class, () -> recordService.getRepairRecordById(id));
    }

    @Test
    @Transactional
    public void whenDeleteNonExistentRecord_thenExceptionIsThrown() {
        // Given
        Long nonExistentId = 999L; // Assuming this ID does not exist

        // Then
        assertThrows(Exception.class, () -> recordService.deleteRepairRecord(nonExistentId));
    }

    @Test
    @Transactional
    public void whenFinalizeRecord_thenExitDateAndTimeAreSet() {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();

        // When
        recordService.finalizeRecord(id);
        RepairRecordEntity finalizedRecord = recordService.getRepairRecordById(id);

        // Then
        LocalDateTime expectedTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertThat(finalizedRecord.getExitDate()).isEqualTo(LocalDate.now());
        assertThat(finalizedRecord.getExitTime().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(expectedTime);
    }

    @Test
    @Transactional
    public void whenPickupRecord_thenPickupDateAndTimeAreSet() {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();
        recordService.finalizeRecord(id);

        // When
        recordService.pickupRecord(id);
        RepairRecordEntity pickedUpRecord = recordService.getRepairRecordById(id);

        // Then
        assertThat(pickedUpRecord.getPickupDate()).isEqualTo(LocalDate.now());
        assertThat(pickedUpRecord.getPickupTime().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @Transactional
    public void whenDelayed_thenChargeAmountMatches() {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();
        recordService.finalizeRecord(id);
        LocalDate date = LocalDate.of(2024, 4, 30);
        savedRecord.setExitDate(date);
        recordService.updateRepairRecord(savedRecord);

        // When
        recordService.pickupRecord(id);
        RepairRecordEntity pickedUpRecord = recordService.getRepairRecordById(id);

        // Then
        assertThat(pickedUpRecord.getChargeAmountDelay()).isGreaterThan(0);
    }

    @Test
    @Transactional
    public void whenApplyVoucher_thenVoucherIsApplied() {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();

        // When
        recordService.applyVoucher(id);
        RepairRecordEntity voucherAppliedRecord = recordService.getRepairRecordById(id);

        // Then
        assertThat(voucherAppliedRecord.getIsVoucherApplied()).isTrue();
        assertThat(voucherAppliedRecord.getDiscountAmountVoucher()).isGreaterThan(0);
        VoucherEntity usedVoucher = voucherRepository.findById(voucherAppliedRecord.getVoucher().getId()).get();
        assertThat(usedVoucher.getNumberOfVouchers()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void whenRemoveVoucher_thenVoucherIsRemoved() {
        // Given
        RepairRecordEntity savedRecord = recordService.saveRepairRecord(record1);
        Long id = savedRecord.getId();
        recordService.applyVoucher(id);
        RepairRecordEntity voucherAppliedRecord = recordService.getRepairRecordById(id);

        // When
        recordService.removeVoucher(id);

        // Then
        assertThat(voucherAppliedRecord.getIsVoucherApplied()).isFalse();
        assertThat(voucherAppliedRecord.getDiscountAmountVoucher()).isEqualTo(0);
    }

    @ParameterizedTest
    @CsvSource({
            "1, Enero", "2, Febrero", "3, Marzo",
            "4, Abril", "5, Mayo", "6, Junio",
            "7, Julio", "8, Agosto", "9, Septiembre",
            "10, Octubre", "11, Noviembre", "12, Diciembre"
    })
    public void testMonthInSpanish(int month, String expectedMonthName) {
        // Act
        String result = recordService.monthInSpanish(month);

        // Assert
        assertEquals(expectedMonthName, result, "The month in Spanish should match the expected value.");
    }

    @ParameterizedTest
    @CsvSource({"0", "13", "-1"})
    public void testMonthInSpanishWithInvalidMonth(int month) {
        // Act
        String result = recordService.monthInSpanish(month);

        // Assert
        assertEquals("Mes inválido", result, "Invalid months should return 'Mes inválido'.");
    }
}

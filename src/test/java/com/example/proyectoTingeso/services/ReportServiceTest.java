package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.entities.RepairRecordEntity;
import com.example.proyectoTingeso.entities.RepairTypePriceEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairTypePriceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReportServiceTest {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private RepairTypePriceRepository repairTypePriceRepository;
    @Autowired
    private RepairRecordService recordService;
    @Autowired
    private ReportService reportService;

    private RepairRecordEntity record1;
    private RepairRecordEntity record2;

    @BeforeEach
    void setup() {
        carRepository.save(new CarEntity(15L, "DHJJ99", "Mazda", "Sedan",
                "Gasolina", 2011, 26000));
        carRepository.save(new CarEntity(16L, "SDFG44", "Mazda", "Pickup",
                "Diesel", 2000, 41000));

        repairTypePriceRepository.save(new RepairTypePriceEntity(21L,1,"BrakeRepair",
                "Gasolina",120000));
        repairTypePriceRepository.save(new RepairTypePriceEntity(22L,2,"EngineRepair",
                "Diesel",350000));

        LocalDate date = LocalDate.of(2024, 4, 27);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(13, 30));
        LocalDate date2 = LocalDate.of(2024, 4, 23);
        LocalDateTime dateTime2 = LocalDateTime.of(date2, LocalTime.of(9, 30));
        record1 = new RepairRecordEntity(1L, "DHJJ99", date, dateTime);
        record2 = new RepairRecordEntity(2L, "SDFG44", date2, dateTime2);
        record1.setRepairTypeNames(Arrays.asList("BrakeRepair"));
        record2.setRepairTypeNames(Arrays.asList("EngineRepair"));
        recordService.saveRepairRecord(record1);
        recordService.saveRepairRecord(record2);
    }

    @Test
    @Transactional
    public void whenRecords_getAverageRepairTimePerBrand() {
        // Given
        LocalDate exit = LocalDate.of(2024, 5, 3);
        record1.setExitDate(exit);
        record2.setExitDate(exit);
        record1.setPickupDate(exit);
        record2.setPickupDate(exit);
        recordService.updateRepairRecord(record1);
        recordService.updateRepairRecord(record2);

        // When
        Map<String, Double> averageRepairTimes = reportService.getAverageRepairTimePerBrand();

        // Then
        assertEquals(8.0, averageRepairTimes.get("Mazda"), "The average repair time for Mazda should be 7 days.");
    }

    @Test
    @Transactional
    public void whenGetRepairTypeStatistics_givenReportNumberForCarStats_thenCorrectStatisticsReturned() {
        // When
        List<Map<String, Object>> stats = reportService.getRepairTypeStatistics(2);

        // Then
        assertFalse(stats.isEmpty(), "Stats should not be empty");
        assertEquals(2, stats.size(), "Should contain statistics for two repair types");
        stats.forEach(stat -> {
            assertTrue(stat.containsKey("Sedan") || stat.containsKey("Pickup"), "Should contain stats keyed by car type");
            assertNotNull(stat.get("Monto"), "Should calculate total amount");
        });
    }

    @Test
    @Transactional
    public void whenGetRepairTypeStatistics_givenReportNumberForEngineStats_thenCorrectStatisticsReturned() {
        // When
        List<Map<String, Object>> stats = reportService.getRepairTypeStatistics(4);

        // Then: Validate the results are as expected
        assertFalse(stats.isEmpty(), "Stats should not be empty");
        assertEquals(2, stats.size(), "Should contain statistics for two repair types");
        stats.forEach(stat -> {
            assertTrue(stat.containsKey("Gasolina") || stat.containsKey("Diesel"), "Should contain stats keyed by engine type");
            assertNotNull(stat.get("Monto"), "Should calculate total amount");
        });
    }
}

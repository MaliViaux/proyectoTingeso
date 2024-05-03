package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    RepairRecordRepository repairRecordRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    RepairRecordService repairRecordService;
    public Map<String, Double> getAverageRepairTimePerBrand() {
        List<Object[]> brandRepairData = repairRecordRepository.findBrandAndRepairDates();
        Map<String, List<Long>> repairTimesByBrand = new HashMap<>();

        for (Object[] data : brandRepairData) { // Calculate the difference in days and organize by brand
            String brand = (String) data[0];
            LocalDate entryDate = (LocalDate) data[1];
            LocalDate exitDate = (LocalDate) data[2];
            long daysBetween = ChronoUnit.DAYS.between(entryDate, exitDate);

            if (!repairTimesByBrand.containsKey(brand)) {
                repairTimesByBrand.put(brand, new ArrayList<>());
            }
            repairTimesByBrand.get(brand).add(daysBetween);
        }

        Map<String, Double> averageRepairTimes = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : repairTimesByBrand.entrySet()) { // Calculate average repair times per brand
            double average = entry.getValue().stream().mapToLong(Long::longValue).average().orElse(0);
            averageRepairTimes.put(entry.getKey(), average);
        }

        // Sort the map by average repair time from highest to lowest
        Map<String, Double> sortedMap = new TreeMap<>((a, b) -> averageRepairTimes.get(b).compareTo(averageRepairTimes.get(a)));
        sortedMap.putAll(averageRepairTimes);
        return sortedMap;
    }

    public List<Map<String, Object>> getRepairTypeStatistics(Integer reportNumber) {
        List<Object[]> results;
        if (reportNumber == 2){ results = repairRecordRepository.findRepairTypeCarStats(); }
        else { results = repairRecordRepository.findRepairTypeEngineStats(); }

        Map<Integer, Map<String, Object>> repairTypeStatsMap = new LinkedHashMap<>();
        results.forEach(objects -> {
            Integer repairTypeNumber = (Integer) objects[0];
            String statsType = (String) objects[1]; // reporte 2 de tipo de auto, report 4 tipo de motor
            Long count = (Long) objects[2];
            Integer totalAmount = ((Long) objects[3]).intValue();

            Map<String, Object> stats = repairTypeStatsMap.computeIfAbsent(repairTypeNumber, k -> new HashMap<>());
            stats.put("repairTypeNumber", repairTypeNumber);
            stats.put(statsType, count);
            stats.put("Monto", (Integer) stats.getOrDefault("Monto", 0) + totalAmount);
        });

        // orden mayor a menor
        return repairTypeStatsMap.values().stream()
                .sorted((map1, map2) -> Integer.compare((Integer) map2.get("Monto"), (Integer) map1.get("Monto")))
                .collect(Collectors.toList());
    }
}

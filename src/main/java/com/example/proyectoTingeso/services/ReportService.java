package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Object[]> brandRepairData = repairRecordRepository.findTotalRepairTimeAndCountByBrand();
        Map<String, Double> averageRepairTimes = new HashMap<>();

        for (Object[] data : brandRepairData) {
            String brand = (String) data[0];
            Long totalDays = (Long) data[1];
            Long count = (Long) data[2];

            double averageTime = count > 0 ? (double) totalDays / count : 0;
            averageRepairTimes.put(brand, averageTime);
        }

        // Ordenar el mapa por valores de mayor a menor
        Map<String, Double> sortedMap = new TreeMap<>((a, b) -> {
            int result = averageRepairTimes.get(b).compareTo(averageRepairTimes.get(a));
            if (result == 0) {
                return a.compareTo(b); // Comparar por marca si los tiempos promedio son iguales
            }
            return result;
        });

        sortedMap.putAll(averageRepairTimes);
        return sortedMap;
    }

    public List<Map<String, Object>> getRepairTypeStatistics(Integer reportNumber) {
        List<Object[]> results;
        if (reportNumber == 2){
            results = repairRecordRepository.findRepairTypeCarStats();
        } else {
            results = repairRecordRepository.findRepairTypeEngineStats();
        }

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

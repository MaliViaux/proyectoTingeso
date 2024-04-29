package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.repositories.CarRepository;
import com.example.proyectoTingeso.repositories.RepairRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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


}

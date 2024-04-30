package com.example.proyectoTingeso.controllers;

import com.example.proyectoTingeso.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin("*")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/averageRepairTime")
    public Map<String, Double> getAverageRepairTimePerBrand() {
        return reportService.getAverageRepairTimePerBrand();
    }

    @GetMapping("/stats/{reportNumber}")
    public List<Map<String, Object>> getRepairTypeStatistics(@PathVariable Integer reportNumber) {
        return reportService.getRepairTypeStatistics(reportNumber);
    }
}

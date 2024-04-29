package com.example.proyectoTingeso.controllers;

import com.example.proyectoTingeso.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin("*")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/averageRepairTime")
    public Map<String, Double> getAverageRepairTimePerBrand() {
        Map<String, Double> averageRepairTimes = reportService.getAverageRepairTimePerBrand();

        return averageRepairTimes;
    }

    // hacer un metodo que en la ruta pase el nombre de la reparacion y ahi obtener las cosas. y en el front end un map

}

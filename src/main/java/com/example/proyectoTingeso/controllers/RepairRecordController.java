package com.example.proyectoTingeso.controllers;

import com.example.proyectoTingeso.entities.RepairRecordEntity;
import com.example.proyectoTingeso.services.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/record")
@CrossOrigin("*")
public class RepairRecordController {
    @Autowired
    RepairRecordService repairRecordService;

    @GetMapping("/")
    public ResponseEntity<List<RepairRecordEntity>> listRecords() {
        List<RepairRecordEntity> records = repairRecordService.getRepairRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairRecordEntity> getRecordById(@PathVariable Long id) {
        RepairRecordEntity record = repairRecordService.getRepairRecordById(id);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/")
    public ResponseEntity<RepairRecordEntity> saveRecord(@RequestBody RepairRecordEntity record) {
        RepairRecordEntity newRecord = repairRecordService.saveRepairRecord(record);
        return ResponseEntity.ok(newRecord);
    }

    @PutMapping("/")
    public ResponseEntity<RepairRecordEntity> updateRecord(@RequestBody RepairRecordEntity record){
        RepairRecordEntity updatedRecord = repairRecordService.updateRepairRecord(record);
        return ResponseEntity.ok(updatedRecord);
    }

    @PutMapping("/{id}/finalize")
    public ResponseEntity<String> finalizeRecord(@PathVariable Long id) {
        try {
            repairRecordService.finalizeRecord(id);
            return ResponseEntity.ok("Reparación marcada como finalizada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al finalizar la reparación: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/pickup")
    public ResponseEntity<String> pickupRecord(@PathVariable Long id) {
        try {
            repairRecordService.pickupRecord(id);
            return ResponseEntity.ok("Reparación marcada como retirada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al retirar la reparación: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/applyVoucher")
    public ResponseEntity<String> applyVoucher(@PathVariable Long id) {
        try {
            repairRecordService.applyVoucher(id);
            return ResponseEntity.ok("Bono aplicado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al aplicar bono: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/removeVoucher")
    public ResponseEntity<String> removeVoucher(@PathVariable Long id) {
        try {
            repairRecordService.removeVoucher(id);
            return ResponseEntity.ok("Bono quitado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al quitar bono: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteRecordById(@PathVariable Long id) throws Exception {
        var isDeleted = repairRecordService.deleteRepairRecord(id);
        return ResponseEntity.noContent().build();
    }
}

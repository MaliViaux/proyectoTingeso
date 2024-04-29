package com.example.proyectoTingeso.controllers;

import com.example.proyectoTingeso.entities.VoucherEntity;
import com.example.proyectoTingeso.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/voucher")
@CrossOrigin("*")
public class VoucherController {
    @Autowired
    VoucherService voucherService;

    @GetMapping("/")
    public ResponseEntity<List<VoucherEntity>> listVouchers() {
        List<VoucherEntity> vouchers = voucherService.getVouchers();
        return ResponseEntity.ok(vouchers);
    }

    @PostMapping("/")
    public ResponseEntity<VoucherEntity> saveVoucher(@RequestBody VoucherEntity voucher) {
        VoucherEntity newVoucher = voucherService.saveVoucher(voucher);
        return ResponseEntity.ok(newVoucher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherEntity> getVoucherById(@PathVariable Long id) {
        VoucherEntity voucher = voucherService.getVoucherById(id);
        return ResponseEntity.ok(voucher);
    }

    @PutMapping("/")
    public ResponseEntity<VoucherEntity> updateVoucher(@RequestBody VoucherEntity voucher){
        VoucherEntity updatedVoucher = voucherService.updateVoucher(voucher);
        return ResponseEntity.ok(updatedVoucher);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteVoucherById(@PathVariable Long id) throws Exception {
        var isDeleted = voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }
}

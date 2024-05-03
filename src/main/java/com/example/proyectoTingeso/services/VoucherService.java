package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.VoucherEntity;
import com.example.proyectoTingeso.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherService {
    @Autowired
    VoucherRepository voucherRepository;
    public ArrayList<VoucherEntity> getVouchers() {
        List<VoucherEntity> vouchersList = voucherRepository.findAll();
        return new ArrayList<>(vouchersList);
    }

    public VoucherEntity saveVoucher(VoucherEntity voucher){
        voucher.setNumberOfRecords(0);
        return voucherRepository.save(voucher);
    }

    public VoucherEntity getVoucherById(Long id){
        return voucherRepository.findById(id).get();
    }

    public VoucherEntity updateVoucher(VoucherEntity voucher) {
        voucher.setNumberOfRecords(0);
        return voucherRepository.save(voucher);
    }

    public boolean deleteVoucher(Long id) throws Exception {
        try{
            voucherRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

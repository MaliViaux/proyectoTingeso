package com.example.proyectoTingeso.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "voucher")
    @JsonBackReference
    @Column(nullable = true)
    private Set<RepairRecordEntity> voucherRecords; // registros asociados al bono

    @Column(nullable = false)
    private Integer discountAmount; //monto del descuento
    @Column(nullable = false)
    private Integer numberOfVouchers; // cantidad de bonos
    @Column(nullable = false)
    private String brand; // marca asociada al bono
    @Column(nullable = false)
    private String voucherMonth; // mes del bono
    @Column(nullable = false)
    private Integer voucherYear; // año del bono
    @Column(nullable = true)
    private Integer numberOfRecords; // numero de registros asociados
}

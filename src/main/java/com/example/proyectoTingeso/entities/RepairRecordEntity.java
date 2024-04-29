package com.example.proyectoTingeso.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "repairRecords")
@Data // set getters y setters
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"car"})
public class RepairRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "repair_record_repair_type_price",
        joinColumns = @JoinColumn(name = "repairRecordId"),
        inverseJoinColumns = @JoinColumn(name = "repairTypePriceId"))
    private Set<RepairTypePriceEntity> repairTypesPrices; // lista de reparaciones asociadas

    @ManyToOne
    @JoinColumn(name="car_id", nullable = true)
    private CarEntity car; // vehiculo asociado con la reparacion

    @ManyToOne
    @JoinColumn(name="voucher_id", nullable = true)
    private VoucherEntity voucher; // bono asociado con la reparacion

    @ElementCollection
    @CollectionTable(name = "repair_type_names", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "repair_type_names")
    private List<String> repairTypeNames; // los nombres de las reparaciones asociadas

    @Column(nullable = false)
    private String carPlate; // patente del vehiculo
    @Column(nullable = false)
    private LocalDate entryDate; // fecha de ingreso
    @Column(nullable = false)
    private LocalDateTime entryTime; // hora de ingreso
    @Column(nullable = true)
    private Boolean isVoucherApplied; // si tiene un bono aplicado
    @Column(nullable = true)
    private Boolean isVoucherAvailable; // si hay un bono disponible
    @Column(nullable = true)
    private Integer totalRepairCost; // suma de los costos de los tipos de reparaciones
    @Column(nullable = true)
    private Integer finalCost; // costos de las reparaciones mas decuentos y recargos
    @Column(nullable = true)
    private LocalDate exitDate; // fecha de salida
    @Column(nullable = true)
    private LocalDateTime exitTime; // hora de salida
    @Column(nullable = true)
    private LocalDate pickupDate; // fecha de recogida
    @Column(nullable = true)
    private LocalDateTime pickupTime; // hora de recogida
    @Column(nullable = true)
    private Integer chargeAmountAge; // cargo por antiguedad
    @Column(nullable = true)
    private Integer chargeAmountMileage; // cargo por kilometraje
    @Column(nullable = true)
    private Integer chargeAmountDelay; // cargo por atraso
    @Column(nullable = true)
    private Integer DiscountAmountVoucher; // descuento por bono
    @Column(nullable = true)
    private Integer DiscountAmountEntryDate; // descuento por dia de llegada
    @Column(nullable = true)
    private Integer DiscountAmountNumberOfRepairs; // descuento por numero de reparaciones en un año
    @Column(nullable = true)
    private Integer iva; // 19%
}

package com.example.proyectoTingeso.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "repairTypePrice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairTypePriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    @Column(unique = true, nullable = false)
    private Long repairTypePriceId;

    @ManyToMany(mappedBy = "repairTypesPrices")
    @JsonBackReference
    private Set<RepairRecordEntity> recordsWithType;

    private Integer repairTypeNumber; // 1-11 frenos, refrigeracion, motor, etc
    private String name; // ej: Reparacion del sistema de frenos
    private String engineType; // gasolina, diesel, electrico, hibrido
    private Integer price; // precio CLP

    public RepairTypePriceEntity(Long id, Integer repairTypeNumber, String name, String engineType, Integer price) {
        this.repairTypePriceId = id;
        this.recordsWithType = null;
        this.repairTypeNumber = repairTypeNumber;
        this.name = name;
        this.engineType = engineType;
        this.price = price;
    }
}

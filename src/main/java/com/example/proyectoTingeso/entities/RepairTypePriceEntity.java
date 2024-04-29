package com.example.proyectoTingeso.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "repairTypePrice")
@Data // set getters y setters
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
}

package com.example.proyectoTingeso.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// IMPORTANE MORE RUN/DEBUG->RUN WITH COVERAGE, PORCENTAJE DE PROBADO
// para la evaluacion controladeores y persistencia un 10%
/*
descargar docker
repo de imagenes docker hub
docker official image
consola C users amali docker pull mysql
bajar la imagen de mysql

cmd
docker images ver las imagenes
docker ps ver que esta corriendo

en intellig dms del comando grandote
docker login
docker push sube la imagen a docker
hasta ahora es subir el backend


Front end
generar build de prod
npm run build crea carpeta dist
se usa para pasar a produccion
generar la imagen del front end
dockerfile en vs
cambiar linea 2

docker hub front end y backend

para automatizar despliegue
docker compose, necesito script
archivo docker compose.yml
puede estar en cualquier parte
instalar docker desktop

comando cdm docker compose up, para desplegar fornt y back

docker compose down --rmi all
docker compose up


dato
los nueves, wikipedia
 */
@Entity
@Table(name = "cars")
@Data // set getters y setters
@NoArgsConstructor
@AllArgsConstructor
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "car")
    @JsonBackReference
    @Column(nullable = true)
    private Set<RepairRecordEntity> carRecords; // registros asociados al vehiculo

    @Column(unique = true, nullable = false)
    private String carPlate; // patente
    @Column(nullable = false)
    private String carBrand; // marca
    @Column(nullable = false)
    private String carType; // tipo de auto (Sedán/Hatchback/SUV/Pickup/Furgoneta)
    @Column(nullable = true)
    private String carModel; // modelo
    @Column(nullable = false)
    private String engineType; // tipo de motor (Gasolina/Diésel/Híbrido/Eléctrico)
    @Column(nullable = true)
    private Integer carSeats; // numero de asientos
    @Column(nullable = false)
    private Integer carYear; // año de fabricacion
    @Column(nullable = false)
    private Integer carMileage; // kilometraje
    @Column(nullable = true)
    private Integer numberOfRecords; // numero de registros asociados

    public CarEntity(Long id, String carPlate, String carBrand, String carType,
                     String engineType, Integer carYear, Integer carMileage) {
        this.id = id;
        this.carRecords = null;
        this.carPlate = carPlate;
        this.carBrand = carBrand;
        this.carModel = "any";
        this.carType = carType;
        this.engineType = engineType;
        this.carYear = carYear;
        this.carMileage = carMileage;
        this.numberOfRecords = 0;
    }
}

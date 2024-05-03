package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    @Autowired
    CarRepository carRepository;
    public ArrayList<CarEntity> getCars() {
        List<CarEntity> carsList = carRepository.findAll();
        return new ArrayList<>(carsList);
    }

    public CarEntity saveCar(CarEntity car) {
        Optional<CarEntity> existingCar = carRepository.findByCarPlate(car.getCarPlate());
        if (existingCar.isPresent()) {
            // Throw an exception or handle according to your requirements
            throw new IllegalStateException("Car with plate " + car.getCarPlate() + " already exists.");
        }

        car.setNumberOfRecords(0); // Ensure number of records is set to zero
        return carRepository.save(car);
    }

    public CarEntity getCarById(Long id) { return carRepository.findById(id).get(); }

    public CarEntity updateCar(CarEntity car) {
        if (car.getId() == null) {
            throw new IllegalStateException("Car cannot be updated as it does not have an existing ID.");
        }
        car.setNumberOfRecords(0);

        Optional<CarEntity> existingCar = carRepository.findByCarPlate(car.getCarPlate());
        if (existingCar.isPresent() && !existingCar.get().getId().equals(car.getId())) {
            throw new IllegalStateException("Another car with plate " + car.getCarPlate() + " already exists.");
        }

        return carRepository.save(car);
    }

    public boolean deleteCar(Long id) throws Exception {
        try{
            carRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

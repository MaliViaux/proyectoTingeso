package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarService {
    @Autowired
    CarRepository carRepository;
    public ArrayList<CarEntity> getCars() {
        List<CarEntity> carsList = carRepository.findAll();
        return new ArrayList<>(carsList);
    }

    public CarEntity saveCar(CarEntity car){
        car.setNumberOfRecords(0);
        return carRepository.save(car);
    }

    public CarEntity getCarById(Long id){
        return carRepository.findById(id).get();
    }

    public CarEntity updateCar(CarEntity car) {
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

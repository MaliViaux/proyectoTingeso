package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.repositories.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CarServiceTest {
    @MockBean
    CarRepository carRepository;
    @Autowired
    CarService carService;

    @Test
    public void whenGetCars_thenListIsReturned() {
        // Given
        CarEntity car1 = new CarEntity(1L, "DHJJ99", "Mazda", "Sedan",
                "Gasolina", 2020, 10000);
        CarEntity car2 = new CarEntity(2L, "PCCS18", "Mazda", "Sedan",
                "Gasolina", 2020, 10000);
        List<CarEntity> expectedCars = Arrays.asList(car1, car2);
        when(carRepository.findAll()).thenReturn(expectedCars);

        // When
        List<CarEntity> cars = carService.getCars();

        // Then
        assertThat(cars).isEqualTo(expectedCars);
    }

    @Test
    public void whenSaveCar_thenNumberOfRecordsIsZero() {
        // Given
        CarEntity car = new CarEntity(1L, "DHJJ99", "Mazda", "Sedan",
                "Gasolina", 2020, 10000);
        when(carRepository.save(any(CarEntity.class))).thenReturn(car);

        // When
        CarEntity savedCar = carService.saveCar(car);

        // Then
        assertThat(savedCar.getNumberOfRecords()).isEqualTo(0);
        verify(carRepository).save(car);
    }

    @Test
    public void whenGetCarById_thenCarIsReturned() {
        // Given
        CarEntity expectedCar = new CarEntity(1L, "DHJJ99", "Mazda", "Sedan",
                "Gasolina", 2020, 10000);
        when(carRepository.findById(1L)).thenReturn(Optional.of(expectedCar));

        // When
        CarEntity car = carService.getCarById(1L);

        // Then
        assertThat(car).isEqualTo(expectedCar);
    }

    @Test
    public void whenUpdateCar_thenUpdatedCarIsReturned() {
        // Given
        CarEntity carToUpdate = new CarEntity(1L, "DHJJ99", "Mazda", "Sedan",
                "Gasolina", 2020, 10000);
        when(carRepository.save(any(CarEntity.class))).thenReturn(carToUpdate);  // Mocking the save method

        // When
        CarEntity updatedCar = carService.updateCar(carToUpdate);

        // Then
        assertThat(updatedCar).isEqualTo(carToUpdate);
        verify(carRepository).save(carToUpdate);
    }

    @Test
    public void whenDeleteCar_thenTrue() throws Exception {
        // Given
        Long carId = 1L;
        when(carRepository.existsById(carId)).thenReturn(true);
        doNothing().when(carRepository).deleteById(carId);

        // When
        boolean result = carService.deleteCar(carId);

        // Then
        assertTrue(result);
        verify(carRepository).deleteById(carId);
    }

    @Test
    public void whenDeleteCar_thenException() {
        // Given
        doThrow(new RuntimeException("Failed to delete")).when(carRepository).deleteById(1L);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            carService.deleteCar(1L);
        });

        // Check exception message
        assertTrue(exception.getMessage().contains("Failed to delete"));
    }
}

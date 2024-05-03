package com.example.proyectoTingeso.controllers;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.services.CarService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
public class CarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    public void listCars_ShouldReturnCars() throws Exception {
        CarEntity car1 = new CarEntity(1L, "ABC123", "Mazda", "Sedan", "Gasoline", 2020, 15000);
        CarEntity car2 = new CarEntity(2L, "XYZ789", "Ford", "SUV", "Diesel", 2018, 25000);

        List<CarEntity> carList = new ArrayList<>(Arrays.asList(car1, car2));

        given(carService.getCars()).willReturn((ArrayList<CarEntity>) carList);

        mockMvc.perform(get("/api/v1/car/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].carBrand", is("Mazda")))
                .andExpect(jsonPath("$[1].carBrand", is("Ford")));
    }

    @Test
    public void getCarById_ShouldReturnCar() throws Exception {
        CarEntity car = new CarEntity(1L, "ABC123", "Mazda", "Sedan", "Gasoline", 2020, 15000);
        given(carService.getCarById(1L)).willReturn(car);

        mockMvc.perform(get("/api/v1/car/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carBrand", is("Mazda")));
    }

    @Test
    public void saveCar_ShouldReturnSavedCar() throws Exception {
        CarEntity savedCar = new CarEntity(1L, "DEF456", "BMW", "Convertible", "Hybrid", 2022, 5000);
        given(carService.saveCar(Mockito.any(CarEntity.class))).willReturn(savedCar);

        String carJson = """
        {
            "carPlate": "DEF456",
            "carBrand": "BMW",
            "carType": "Convertible",
            "engineType": "Hybrid",
            "carYear": 2022,
            "carMileage": 5000
        }
        """;

        mockMvc.perform(post("/api/v1/car/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carBrand", is("BMW")));
    }

    @Test
    public void updateCar_ShouldReturnUpdatedCar() throws Exception {
        CarEntity updatedCar = new CarEntity(1L, "GHI789", "Toyota", "Coupe", "Electric", 2021, 3000);

        given(carService.updateCar(Mockito.any(CarEntity.class))).willReturn(updatedCar);

        String carJson = """
        {
            "id": 1,
            "carPlate": "GHI789",
            "carBrand": "Toyota",
            "carType": "Coupe",
            "engineType": "Electric",
            "carYear": 2021,
            "carMileage": 3000
        }
        """;

        mockMvc.perform(put("/api/v1/car/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carBrand", is("Toyota")));
    }

    @Test
    public void deleteCarById_ShouldReturn204() throws Exception {
        when(carService.deleteCar(1L)).thenReturn(true); // Assuming the method returns a boolean

        mockMvc.perform(delete("/api/v1/car/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
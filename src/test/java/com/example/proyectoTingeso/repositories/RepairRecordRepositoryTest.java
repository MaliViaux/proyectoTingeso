package com.example.proyectoTingeso.repositories;

import com.example.proyectoTingeso.entities.CarEntity;
import com.example.proyectoTingeso.entities.RepairRecordEntity;
import com.example.proyectoTingeso.entities.RepairTypePriceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RepairRecordRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepairRecordRepository repairRecordRepository;

    //@Test
    //public void whenFindByCarBrand_thenReturnRecords() {

        // then
        //assertThat(foundRecords).hasSize(2).extracting(thisRecord -> thisRecord.getCar().getCarBrand()).containsOnly("MERCEDES");
}

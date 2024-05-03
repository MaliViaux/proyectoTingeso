package com.example.proyectoTingeso.controllers;

import com.example.proyectoTingeso.entities.VoucherEntity;
import com.example.proyectoTingeso.services.VoucherService;
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

@WebMvcTest(VoucherController.class)
public class VoucherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoucherService voucherService;

    @Test
    public void listVouchers_ShouldReturnVouchers() throws Exception {
        VoucherEntity voucher1 = new VoucherEntity(1L, null,
                50000, 2, "Mazda",
                "April", 2023, 0);
        VoucherEntity voucher2 = new VoucherEntity(2L, null,
                60000, 1, "Ford",
                "May", 2023, 0);

        List<VoucherEntity> voucherList = new ArrayList<>(Arrays.asList(voucher1, voucher2));

        given(voucherService.getVouchers()).willReturn((ArrayList<VoucherEntity>) voucherList);

        mockMvc.perform(get("/api/v1/voucher/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand", is("Mazda")))
                .andExpect(jsonPath("$[1].brand", is("Ford")));
    }

    @Test
    public void getVoucherById_ShouldReturnVoucher() throws Exception {
        VoucherEntity voucher = new VoucherEntity(1L, null,
                50000, 2, "Mazda",
                "April", 2023, 0);
        given(voucherService.getVoucherById(1L)).willReturn(voucher);

        mockMvc.perform(get("/api/v1/voucher/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.brand", is("Mazda")));
    }

    @Test
    public void saveVoucher_ShouldReturnSavedVoucher() throws Exception {
        VoucherEntity savedVoucher = new VoucherEntity(1L, null,
                40000, 2, "BMW",
                "March", 2024, 0);
        given(voucherService.saveVoucher(Mockito.any(VoucherEntity.class))).willReturn(savedVoucher);

        String voucherJson = """
        {
            "discountAmount": 40000,
            "numberOfVouchers": 0,
            "brand": "BMW",
            "voucherMonth": "March",
            "voucherYear": 2024,
            "numberOfRecords": 0
        }
        """;

        mockMvc.perform(post("/api/v1/voucher/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voucherJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is("BMW")));
    }

    @Test
    public void updateVoucher_ShouldReturnUpdatedVoucher() throws Exception {
        VoucherEntity updatedVoucher = new VoucherEntity(1L, null,
                45000, 1, "Toyota",
                "February", 2024, 1);

        given(voucherService.updateVoucher(Mockito.any(VoucherEntity.class))).willReturn(updatedVoucher);

        String voucherJson = """
        {
            "id": 1,
            "discountAmount": 45000,
            "numberOfVouchers": 1,
            "brand": "Toyota",
            "voucherMonth": "February",
            "voucherYear": 2024,
            "numberOfRecords": 1
        }
        """;

        mockMvc.perform(put("/api/v1/voucher/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voucherJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is("Toyota")));
    }

    @Test
    public void deleteVoucherById_ShouldReturn204() throws Exception {
        when(voucherService.deleteVoucher(1L)).thenReturn(true); // Assuming the method returns a boolean

        mockMvc.perform(delete("/api/v1/voucher/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}

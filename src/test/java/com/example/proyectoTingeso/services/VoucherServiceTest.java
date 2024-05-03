package com.example.proyectoTingeso.services;

import com.example.proyectoTingeso.entities.VoucherEntity;
import com.example.proyectoTingeso.repositories.VoucherRepository;
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
public class VoucherServiceTest {
    @MockBean
    VoucherRepository voucherRepository;
    @Autowired
    VoucherService voucherService;

    @Test
    public void whenGetVouchers_thenListIsReturned() {
        // Given
        VoucherEntity voucher1 = new VoucherEntity(1L, null, 15000, 2, "Toyota", "Mayo", 2023, 0);
        VoucherEntity voucher2 = new VoucherEntity(2L, null, 20000, 3, "Mazda", "Abril", 2024, 0);
        List<VoucherEntity> expectedVouchers = Arrays.asList(voucher1, voucher2);
        when(voucherRepository.findAll()).thenReturn(expectedVouchers);

        // When
        List<VoucherEntity> vouchers = voucherService.getVouchers();

        // Then
        assertThat(vouchers).isEqualTo(expectedVouchers);
    }

    @Test
    public void whenSaveVoucher_thenNumberOfRecordsIsZero() {
        // Given
        VoucherEntity voucher = new VoucherEntity(1L, null, 15000, 2, "Toyota", "Mayo", 2023, 0);
        when(voucherRepository.save(any(VoucherEntity.class))).thenReturn(voucher);

        // When
        VoucherEntity savedVoucher = voucherService.saveVoucher(voucher);

        // Then
        assertThat(savedVoucher.getNumberOfRecords()).isEqualTo(0);
        verify(voucherRepository).save(voucher);
    }

    @Test
    public void whenGetVoucherById_thenVoucherIsReturned() {
        // Given
        VoucherEntity expectedVoucher = new VoucherEntity(1L, null, 15000, 2, "Toyota", "Mayo", 2023, 0);
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(expectedVoucher));

        // When
        VoucherEntity voucher = voucherService.getVoucherById(1L);

        // Then
        assertThat(voucher).isEqualTo(expectedVoucher);
    }

    @Test
    public void whenUpdateVoucher_thenUpdatedVoucherIsReturned() {
        // Given
        VoucherEntity voucherToUpdate = new VoucherEntity(1L, null, 15000, 2, "Toyota", "Mayo", 2023,0);
        when(voucherRepository.save(any(VoucherEntity.class))).thenReturn(voucherToUpdate);  // Mocking the save method

        // When
        VoucherEntity updatedVoucher = voucherService.updateVoucher(voucherToUpdate);

        // Then
        assertThat(updatedVoucher).isEqualTo(voucherToUpdate);  // Asserting that the returned voucher is the same as the updated one
        verify(voucherRepository).save(voucherToUpdate);  // Verifying that the repository's save method was indeed called with the voucher
    }

    @Test
    public void whenDeleteVoucher_thenTrue() throws Exception {
        // Given
        Long voucherId = 1L;
        when(voucherRepository.existsById(voucherId)).thenReturn(true);
        doNothing().when(voucherRepository).deleteById(voucherId);

        // When
        boolean result = voucherService.deleteVoucher(voucherId);

        // Then
        assertTrue(result);
        verify(voucherRepository).deleteById(voucherId);
    }

    @Test
    public void whenDeleteVoucher_thenException() {
        // Given
        Long voucherId = 1L;
        doThrow(new RuntimeException("Failed to delete")).when(voucherRepository).deleteById(voucherId);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            voucherService.deleteVoucher(voucherId);
        });

        // Check exception message
        assertTrue(exception.getMessage().contains("Failed to delete"));
    }
}

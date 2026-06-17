package com.duoc.alertservice.service;

import com.duoc.alertservice.model.AlertaEmergencia;
import com.duoc.alertservice.repository.AlertaEmergenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaEmergenciaServiceTest {

    @Mock
    private AlertaEmergenciaRepository alertaEmergenciaRepository;

    @InjectMocks
    private AlertaEmergenciaService alertaEmergenciaService;

    private AlertaEmergencia alertaMock;

    @BeforeEach
    void setUp() {
        alertaMock = new AlertaEmergencia();
        alertaMock.setId(1L);
        alertaMock.setIdReporte(105L);
        alertaMock.setMensajeAlerta("Evacuación preventiva de la zona por avance rápido del fuego.");
        alertaMock.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
        alertaMock.setFechaEmision(LocalDateTime.now());
    }

    @Test
    void testFindByNivelRiesgo_Exitoso() {
        when(alertaEmergenciaRepository.findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION))
                .thenReturn(Arrays.asList(alertaMock));

        List<AlertaEmergencia> resultados = alertaEmergenciaService.findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);

        assertNotNull(resultados, "La lista devuelta no debería ser nula");
        assertFalse(resultados.isEmpty(), "La lista devuelta no debería estar vacía");
        assertEquals(1, resultados.size(), "Debería haber exactamente 1 alerta en la lista");
        assertEquals(AlertaEmergencia.NivelRiesgo.EVACUACION, resultados.get(0).getNivelRiesgo(), "El nivel de riesgo debe coincidir con la búsqueda");
        assertEquals(105L, resultados.get(0).getIdReporte(), "El ID del reporte asociado debe coincidir");

        verify(alertaEmergenciaRepository, times(1)).findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
    }
}
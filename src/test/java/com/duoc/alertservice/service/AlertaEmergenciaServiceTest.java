package com.duoc.alertservice.service;

import com.duoc.alertservice.client.ReportClient;
import com.duoc.alertservice.dto.ReporteIncendioDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaEmergenciaServiceTest {

    @Mock
    private AlertaEmergenciaRepository alertaEmergenciaRepository;

    @Mock
    private ReportClient reportClient;

    @InjectMocks
    private AlertaEmergenciaService alertaEmergenciaService;

    private AlertaEmergencia alertaMock;

    @BeforeEach
    void setUp() {
        alertaMock = new AlertaEmergencia();
        alertaMock.setId(1L);
        alertaMock.setIdReporte(105L);
        alertaMock.setMensajeAlerta("Evacuación preventiva");
        alertaMock.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
        alertaMock.setFechaEmision(LocalDateTime.now());
    }

    @Test
    void testFindAll_Exitoso() {
        when(alertaEmergenciaRepository.findAll()).thenReturn(Arrays.asList(alertaMock));

        List<AlertaEmergencia> resultado = alertaEmergenciaService.findAll();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(alertaEmergenciaRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Exitoso() {
        when(alertaEmergenciaRepository.findById(1L)).thenReturn(Optional.of(alertaMock));

        AlertaEmergencia resultado = alertaEmergenciaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(105L, resultado.getIdReporte());
        verify(alertaEmergenciaRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NoExistente() {
        when(alertaEmergenciaRepository.findById(99L)).thenReturn(Optional.empty());

        AlertaEmergencia resultado = alertaEmergenciaService.findById(99L);

        assertNull(resultado);
        verify(alertaEmergenciaRepository, times(1)).findById(99L);
    }

    @Test
    void testFindByNivelRiesgo_Exitoso() {
        when(alertaEmergenciaRepository.findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION))
                .thenReturn(Arrays.asList(alertaMock));

        List<AlertaEmergencia> resultados = alertaEmergenciaService.findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);

        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals(AlertaEmergencia.NivelRiesgo.EVACUACION, resultados.get(0).getNivelRiesgo());
        verify(alertaEmergenciaRepository, times(1)).findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
    }

    @Test
    void testFindByFechaEmisionRango_NullInicio_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                alertaEmergenciaService.findByFechaEmisionRango(null, LocalDateTime.now()));
    }

    @Test
    void testFindByFechaEmisionRango_NullFin_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                alertaEmergenciaService.findByFechaEmisionRango(LocalDateTime.now(), null));
    }

    @Test
    void testFindByFechaEmisionRango_InicioAfterFin_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                alertaEmergenciaService.findByFechaEmisionRango(
                        LocalDateTime.of(2024, 12, 31, 23, 59),
                        LocalDateTime.of(2024, 1, 1, 0, 0)));
    }

    @Test
    void testFindByFechaEmisionRango_Exitoso() {
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(alertaEmergenciaRepository.findByFechaEmisionBetween(inicio, fin)).thenReturn(Arrays.asList(alertaMock));

        List<AlertaEmergencia> resultado = alertaEmergenciaService.findByFechaEmisionRango(inicio, fin);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(alertaEmergenciaRepository, times(1)).findByFechaEmisionBetween(inicio, fin);
    }

    @Test
    void testSave_Exitoso() {
        when(alertaEmergenciaRepository.save(any(AlertaEmergencia.class))).thenReturn(alertaMock);

        AlertaEmergencia resultado = alertaEmergenciaService.save(alertaMock);

        assertNotNull(resultado);
        assertEquals(AlertaEmergencia.NivelRiesgo.EVACUACION, resultado.getNivelRiesgo());
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testDeleteById_Exitoso() {
        doNothing().when(alertaEmergenciaRepository).deleteById(1L);

        alertaEmergenciaService.deleteById(1L);

        verify(alertaEmergenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGenerarAlertaDesdeReporte_IdReporteNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                alertaEmergenciaService.generarAlertaDesdeReporte(null));
    }

    @Test
    void testGenerarAlertaDesdeReporte_EstadoEnCombate_RetornaCATASTROFE() {
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(1L);
        dto.setDescripcion("Incendio activo en cerro");
        dto.setEstado("EN_COMBATE");
        when(reportClient.getReporte(1L)).thenReturn(dto);

        AlertaEmergencia resultado = alertaEmergenciaService.generarAlertaDesdeReporte(1L);

        assertNotNull(resultado);
        assertEquals(AlertaEmergencia.NivelRiesgo.CATASTROFE, resultado.getNivelRiesgo());
        assertEquals(1L, resultado.getIdReporte());
        assertTrue(resultado.getMensajeAlerta().contains("ALERTA DE EVACUACION"));
        assertNotNull(resultado.getFechaEmision());
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testGenerarAlertaDesdeReporte_EstadoDesconocido_RetornaPREVENTIVO() {
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(2L);
        dto.setDescripcion("Reporte sin conexion");
        dto.setEstado("DESCONOCIDO");
        when(reportClient.getReporte(2L)).thenReturn(dto);

        AlertaEmergencia resultado = alertaEmergenciaService.generarAlertaDesdeReporte(2L);

        assertNotNull(resultado);
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, resultado.getNivelRiesgo());
        assertTrue(resultado.getMensajeAlerta().contains("Preventiva"));
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testGenerarAlertaDesdeReporte_EstadoPreventivo_RetornaPREVENTIVO() {
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(3L);
        dto.setDescripcion("Incendio controlado");
        dto.setEstado("CONTROLADO");
        when(reportClient.getReporte(3L)).thenReturn(dto);

        AlertaEmergencia resultado = alertaEmergenciaService.generarAlertaDesdeReporte(3L);

        assertNotNull(resultado);
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, resultado.getNivelRiesgo());
        assertTrue(resultado.getMensajeAlerta().contains("observacion"));
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testGenerarAlertaDesdeReporte_EstadoExtinguido_RetornaPREVENTIVO() {
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(4L);
        dto.setDescripcion("Incendio extinguido");
        dto.setEstado("EXTINGUIDO");
        when(reportClient.getReporte(4L)).thenReturn(dto);

        AlertaEmergencia resultado = alertaEmergenciaService.generarAlertaDesdeReporte(4L);

        assertNotNull(resultado);
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, resultado.getNivelRiesgo());
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }
}

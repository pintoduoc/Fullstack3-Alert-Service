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
        alertaMock.setMensajeAlerta("Evacuación preventiva de la zona por avance rápido del fuego.");
        alertaMock.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
        alertaMock.setFechaEmision(LocalDateTime.now());
    }

    @Test
    void testFindAll_Exitoso() {
        when(alertaEmergenciaRepository.findAll()).thenReturn(Arrays.asList(alertaMock));
        List<AlertaEmergencia> resultado = alertaEmergenciaService.findAll();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(AlertaEmergencia.NivelRiesgo.EVACUACION, resultado.get(0).getNivelRiesgo());
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
    void testFindById_NoEncontrado() {
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

        assertNotNull(resultados, "La lista devuelta no debería ser nula");
        assertFalse(resultados.isEmpty(), "La lista devuelta no debería estar vacía");
        assertEquals(1, resultados.size(), "Debería haber exactamente 1 alerta en la lista");
        assertEquals(AlertaEmergencia.NivelRiesgo.EVACUACION, resultados.get(0).getNivelRiesgo(), "El nivel de riesgo debe coincidir con la búsqueda");
        assertEquals(105L, resultados.get(0).getIdReporte(), "El ID del reporte asociado debe coincidir");

        verify(alertaEmergenciaRepository, times(1)).findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
    }

    @Test
    void testFindByFechaEmisionRango_Exitoso() {
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(alertaEmergenciaRepository.findByFechaEmisionBetween(inicio, fin))
                .thenReturn(Arrays.asList(alertaMock));

        List<AlertaEmergencia> resultados = alertaEmergenciaService.findByFechaEmisionRango(inicio, fin);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        verify(alertaEmergenciaRepository, times(1)).findByFechaEmisionBetween(inicio, fin);
    }

    @Test
    void testFindByFechaEmisionRango_FechaInicioNull_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> alertaEmergenciaService.findByFechaEmisionRango(null, LocalDateTime.now()));
    }

    @Test
    void testFindByFechaEmisionRango_FechaFinNull_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> alertaEmergenciaService.findByFechaEmisionRango(LocalDateTime.now(), null));
    }

    @Test
    void testFindByFechaEmisionRango_FechaInicioPosteriorFin_LanzaExcepcion() {
        LocalDateTime inicio = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 1, 1, 0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> alertaEmergenciaService.findByFechaEmisionRango(inicio, fin));
    }

    @Test
    void testSave_Exitoso() {
        when(alertaEmergenciaRepository.save(any(AlertaEmergencia.class))).thenReturn(alertaMock);
        AlertaEmergencia resultado = alertaEmergenciaService.save(alertaMock);
        assertNotNull(resultado);
        assertEquals("Evacuación preventiva de la zona por avance rápido del fuego.", resultado.getMensajeAlerta());
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testDeleteById_Exitoso() {
        doNothing().when(alertaEmergenciaRepository).deleteById(1L);
        alertaEmergenciaService.deleteById(1L);
        verify(alertaEmergenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGenerarAlertaDesdeReporte_NullId_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> alertaEmergenciaService.generarAlertaDesdeReporte(null));
    }

    @Test
    void testGenerarAlertaDesdeReporte_EstadoEnCombate_CreaCatastrofe() {
        Long idReporte = 1L;
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(idReporte);
        dto.setDescripcion("Incendio activo en la zona");
        dto.setEstado("EN_COMBATE");

        when(reportClient.getReporte(idReporte)).thenReturn(dto);
        when(alertaEmergenciaRepository.save(any(AlertaEmergencia.class))).thenAnswer(i -> i.getArgument(0));

        AlertaEmergencia alerta = alertaEmergenciaService.generarAlertaDesdeReporte(idReporte);

        assertNotNull(alerta);
        assertEquals(idReporte, alerta.getIdReporte());
        assertEquals(AlertaEmergencia.NivelRiesgo.CATASTROFE, alerta.getNivelRiesgo());
        assertTrue(alerta.getMensajeAlerta().contains("ALERTA DE EVACUACION"));
        assertNotNull(alerta.getFechaEmision());

        verify(reportClient, times(1)).getReporte(idReporte);
        verify(alertaEmergenciaRepository, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testGenerarAlertaDesdeReporte_EstadoPendiente_CreaPreventivo() {
        Long idReporte = 2L;
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(idReporte);
        dto.setDescripcion("Incendio en observacion");
        dto.setEstado("PENDIENTE");

        when(reportClient.getReporte(idReporte)).thenReturn(dto);
        when(alertaEmergenciaRepository.save(any(AlertaEmergencia.class))).thenAnswer(i -> i.getArgument(0));

        AlertaEmergencia alerta = alertaEmergenciaService.generarAlertaDesdeReporte(idReporte);

        assertNotNull(alerta);
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, alerta.getNivelRiesgo());
        assertTrue(alerta.getMensajeAlerta().contains("Incidente en observacion"));
    }

    @Test
    void testGenerarAlertaDesdeReporte_FallbackDesconocido_CreaPreventivo() {
        Long idReporte = 3L;
        ReporteIncendioDTO dto = new ReporteIncendioDTO();
        dto.setId(idReporte);
        dto.setDescripcion("Información del reporte no disponible temporalmente debido a problemas de conexión.");
        dto.setEstado("DESCONOCIDO");

        when(reportClient.getReporte(idReporte)).thenReturn(dto);
        when(alertaEmergenciaRepository.save(any(AlertaEmergencia.class))).thenAnswer(i -> i.getArgument(0));

        AlertaEmergencia alerta = alertaEmergenciaService.generarAlertaDesdeReporte(idReporte);

        assertNotNull(alerta);
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, alerta.getNivelRiesgo());
        assertTrue(alerta.getMensajeAlerta().contains("Alerta Preventiva"));
    }
}

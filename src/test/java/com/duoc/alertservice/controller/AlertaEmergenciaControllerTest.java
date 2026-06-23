package com.duoc.alertservice.controller;

import com.duoc.alertservice.model.AlertaEmergencia;
import com.duoc.alertservice.service.AlertaEmergenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertaEmegenciaController.class)
class AlertaEmergenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertaEmergenciaService alertaEmergenciaService;

    private AlertaEmergencia alertaMock;

    @BeforeEach
    void setUp() {
        alertaMock = new AlertaEmergencia();
        alertaMock.setId(1L);
        alertaMock.setIdReporte(105L);
        alertaMock.setMensajeAlerta("Alerta de evacuacion");
        alertaMock.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE);
        alertaMock.setFechaEmision(LocalDateTime.now());
    }

    @Test
    void testFindAll() throws Exception {
        when(alertaEmergenciaService.findAll()).thenReturn(Arrays.asList(alertaMock));

        mockMvc.perform(get("/api/alerta-emergencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].mensajeAlerta").value("Alerta de evacuacion"));

        verify(alertaEmergenciaService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        when(alertaEmergenciaService.findById(1L)).thenReturn(alertaMock);

        mockMvc.perform(get("/api/alerta-emergencia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelRiesgo").value("CATASTROFE"));

        verify(alertaEmergenciaService, times(1)).findById(1L);
    }

    @Test
    void testFindByNivelRiesgo() throws Exception {
        when(alertaEmergenciaService.findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE))
                .thenReturn(Arrays.asList(alertaMock));

        mockMvc.perform(get("/api/alerta-emergencia/nivel-riesgo").param("nivelRiesgo", "CATASTROFE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(alertaEmergenciaService, times(1)).findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE);
    }

    @Test
    void testFindByFechaEmisionRango() throws Exception {
        when(alertaEmergenciaService.findByFechaEmisionRango(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(alertaMock));

        mockMvc.perform(get("/api/alerta-emergencia/rango-fechas")
                        .param("fechaInicio", "2024-01-01T00:00:00")
                        .param("fechaFin", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(alertaEmergenciaService, times(1)).findByFechaEmisionRango(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testCreateAlerta() throws Exception {
        when(alertaEmergenciaService.generarAlertaDesdeReporte(105L)).thenReturn(alertaMock);

        mockMvc.perform(post("/api/alerta-emergencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idReporte\":105,\"mensajeAlerta\":\"Alerta de evacuacion\",\"nivelRiesgo\":\"CATASTROFE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReporte").value(105));

        verify(alertaEmergenciaService, times(1)).generarAlertaDesdeReporte(105L);
    }

    @Test
    void testUpdateAlerta() throws Exception {
        when(alertaEmergenciaService.save(any(AlertaEmergencia.class))).thenReturn(alertaMock);

        mockMvc.perform(put("/api/alerta-emergencia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idReporte\":105,\"mensajeAlerta\":\"Alerta de evacuacion\",\"nivelRiesgo\":\"CATASTROFE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelRiesgo").value("CATASTROFE"));

        verify(alertaEmergenciaService, times(1)).save(any(AlertaEmergencia.class));
    }

    @Test
    void testDeleteAlerta() throws Exception {
        doNothing().when(alertaEmergenciaService).deleteById(1L);

        mockMvc.perform(delete("/api/alerta-emergencia/1"))
                .andExpect(status().isOk());

        verify(alertaEmergenciaService, times(1)).deleteById(1L);
    }
}

package com.duoc.alertservice.controller;

import com.duoc.alertservice.model.AlertaEmergencia;
import com.duoc.alertservice.service.AlertaEmergenciaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertaEmegenciaController.class)
class AlertaEmergenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertaEmergenciaService alertaEmergenciaService;

    @Test
    void testFindAll() throws Exception {
        AlertaEmergencia a = new AlertaEmergencia();
        a.setId(1L);
        a.setIdReporte(1L);
        a.setMensajeAlerta("Alerta de prueba");
        a.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.PREVENTIVO);
        a.setFechaEmision(LocalDateTime.now());
        when(alertaEmergenciaService.findAll()).thenReturn(Arrays.asList(a));

        mockMvc.perform(get("/api/alerta-emergencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mensajeAlerta").value("Alerta de prueba"));
    }

    @Test
    void testFindById() throws Exception {
        AlertaEmergencia a = new AlertaEmergencia();
        a.setId(1L);
        a.setIdReporte(1L);
        a.setMensajeAlerta("Alerta por id");
        a.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.EVACUACION);
        a.setFechaEmision(LocalDateTime.now());
        when(alertaEmergenciaService.findById(1L)).thenReturn(a);

        mockMvc.perform(get("/api/alerta-emergencia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajeAlerta").value("Alerta por id"));
    }

    @Test
    void testFindByNivelRiesgo() throws Exception {
        AlertaEmergencia a = new AlertaEmergencia();
        a.setId(1L);
        a.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE);
        a.setFechaEmision(LocalDateTime.now());
        when(alertaEmergenciaService.findByNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE))
                .thenReturn(Arrays.asList(a));

        mockMvc.perform(get("/api/alerta-emergencia/nivel-riesgo").param("nivelRiesgo", "CATASTROFE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nivelRiesgo").value("CATASTROFE"));
    }

    @Test
    void testCreateAlerta() throws Exception {
        AlertaEmergencia a = new AlertaEmergencia();
        a.setId(1L);
        a.setIdReporte(1L);
        a.setMensajeAlerta("Nueva alerta");
        a.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE);
        a.setFechaEmision(LocalDateTime.now());
        when(alertaEmergenciaService.generarAlertaDesdeReporte(anyLong())).thenReturn(a);

        mockMvc.perform(post("/api/alerta-emergencia")
                        .contentType("application/json")
                        .content("{\"idReporte\":1,\"mensajeAlerta\":\"Nueva alerta\",\"nivelRiesgo\":\"CATASTROFE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajeAlerta").value("Nueva alerta"));
    }

    @Test
    void testUpdateAlerta() throws Exception {
        AlertaEmergencia a = new AlertaEmergencia();
        a.setId(1L);
        a.setMensajeAlerta("Alerta actualizada");
        a.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.PREVENTIVO);
        a.setFechaEmision(LocalDateTime.now());
        when(alertaEmergenciaService.save(any(AlertaEmergencia.class))).thenReturn(a);

        mockMvc.perform(put("/api/alerta-emergencia/1")
                        .contentType("application/json")
                        .content("{\"mensajeAlerta\":\"Alerta actualizada\",\"nivelRiesgo\":\"PREVENTIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajeAlerta").value("Alerta actualizada"));
    }

    @Test
    void testDeleteAlerta() throws Exception {
        mockMvc.perform(delete("/api/alerta-emergencia/1"))
                .andExpect(status().isOk());
    }
}

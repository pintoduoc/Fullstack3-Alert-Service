package com.duoc.alertservice.service;

import com.duoc.alertservice.client.ReportClient;
import com.duoc.alertservice.dto.ReporteIncendioDTO;
import com.duoc.alertservice.model.AlertaEmergencia;
import com.duoc.alertservice.repository.AlertaEmergenciaRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaEmergenciaService {
    private final AlertaEmergenciaRepository alertaEmergenciaRepository;
    private final ReportClient reportClient;

    public AlertaEmergenciaService(AlertaEmergenciaRepository alertaEmergenciaRepository, ReportClient reportClient) {
        this.alertaEmergenciaRepository = alertaEmergenciaRepository;
        this.reportClient = reportClient;
    }

    public List<AlertaEmergencia> findAll() {
        return alertaEmergenciaRepository.findAll();
    }

    public AlertaEmergencia findById(Long id) {
        return alertaEmergenciaRepository.findById(id).orElse(null);
    }

    public List<AlertaEmergencia> findByNivelRiesgo(AlertaEmergencia.NivelRiesgo nivelRiesgo) {
        return alertaEmergenciaRepository.findByNivelRiesgo(nivelRiesgo);
    }

    public List<AlertaEmergencia> findByFechaEmisionRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return alertaEmergenciaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    public AlertaEmergencia save(AlertaEmergencia alerta) {
        return alertaEmergenciaRepository.save(alerta);
    }

    public void deleteById(Long id) {
        alertaEmergenciaRepository.deleteById(id);
    }

    public AlertaEmergencia generarAlertaDesdeReporte(Long idReporte) {
        if (idReporte == null) {
            throw new IllegalArgumentException("idReporte es obligatorio para generar la alerta");
        }

        // 1. Consumir el servicio externo (El Circuit Breaker actúa aquí si falla)
        ReporteIncendioDTO reporteDTO = reportClient.getReporte(idReporte);
        String estadoReporte = reporteDTO != null && reporteDTO.getEstado() != null ? reporteDTO.getEstado() : "DESCONOCIDO";
        String descripcionReporte = reporteDTO != null && reporteDTO.getDescripcion() != null
                ? reporteDTO.getDescripcion()
                : "Sin descripcion disponible";

        // 2. Crear la entidad local de Alerta
        AlertaEmergencia nuevaAlerta = new AlertaEmergencia();
        nuevaAlerta.setIdReporte(idReporte); // Vinculamos el ID
        // Asumiendo que tu entidad tiene un campo fecha (recomendado)
        nuevaAlerta.setFechaEmision(LocalDateTime.now());

        // 3. Aplicar lógica de negocio basada en la respuesta (DTO)
        if ("DESCONOCIDO".equals(estadoReporte)) {
            // Caso donde actuó el Circuit Breaker (Fallback method)
            nuevaAlerta.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.PREVENTIVO);
            nuevaAlerta.setMensajeAlerta("Alerta Preventiva (Sin conexion al origen): " + descripcionReporte);

        } else if ("EN_COMBATE".equals(estadoReporte)) {
            // Caso crítico real
            nuevaAlerta.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.CATASTROFE);
            nuevaAlerta.setMensajeAlerta("ALERTA DE EVACUACION: " + descripcionReporte);

        } else {
            // Otros estados
            nuevaAlerta.setNivelRiesgo(AlertaEmergencia.NivelRiesgo.PREVENTIVO);
            nuevaAlerta.setMensajeAlerta("Incidente en observacion: " + descripcionReporte);
        }

        // 4. Guardar en la base de datos usando tu propio método 'save'
        return this.save(nuevaAlerta);
    }
}

package com.duoc.alertservice.service;

import com.duoc.alertservice.client.ReportClient;
import com.duoc.alertservice.dto.ReporteIncendioDTO;
import com.duoc.alertservice.model.AlertaEmergencia;
import com.duoc.alertservice.repository.AlertaEmergenciaRepository;
import com.duoc.alertservice.strategy.NivelRiesgoContext;
import com.duoc.alertservice.strategy.NivelRiesgoStrategy;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaEmergenciaService {
    private final AlertaEmergenciaRepository alertaEmergenciaRepository;
    private final ReportClient reportClient;
    private final NivelRiesgoContext nivelRiesgoContext;

    public AlertaEmergenciaService(AlertaEmergenciaRepository alertaEmergenciaRepository, ReportClient reportClient) {
        this.alertaEmergenciaRepository = alertaEmergenciaRepository;
        this.reportClient = reportClient;
        this.nivelRiesgoContext = new NivelRiesgoContext();
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

        // 3. Aplicar Strategy Pattern para determinar nivel de riesgo y mensaje
        NivelRiesgoStrategy strategy = nivelRiesgoContext.getStrategy(estadoReporte);
        nuevaAlerta.setNivelRiesgo(strategy.determinarNivelRiesgo());
        nuevaAlerta.setMensajeAlerta(strategy.generarMensaje(descripcionReporte));

        // 4. Guardar en la base de datos usando tu propio método 'save'
        return this.save(nuevaAlerta);
    }
}

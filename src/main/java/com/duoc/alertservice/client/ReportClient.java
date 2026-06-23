package com.duoc.alertservice.client;

import com.duoc.alertservice.dto.ReporteIncendioDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
@Service
public class ReportClient {
    // Instanciamos RestTemplate para hacer la llamada HTTP [cite: 109]
    private final RestTemplate restTemplate = new RestTemplate();
    private final String reportServiceBaseUrl;

    public ReportClient(@Value("${report-service.base-url}") String reportServiceBaseUrl) {
        this.reportServiceBaseUrl = reportServiceBaseUrl;
    }

    // Configuramos el Circuit Breaker indicando qué método ejecutar si falla [cite: 110]
    @CircuitBreaker(name = "reportService", fallbackMethod = "fallbackReport")
    public ReporteIncendioDTO getReporte(Long id) {
        String url = reportServiceBaseUrl + "/api/reporte-incendio/" + id;
        ReporteIncendioDTO reporte = restTemplate.getForObject(url, ReporteIncendioDTO.class);

        // report-service actualmente puede responder 200 con body null cuando el id no existe.
        if (reporte == null) {
            return fallbackReport(id, new IllegalArgumentException("Reporte no encontrado"));
        }

        return reporte;
    }

    // Este es el "Plan B". Si el Report Service está caído, esto es lo que se devuelve [cite: 117, 118, 119, 120]
    public ReporteIncendioDTO fallbackReport(Long id, Exception e) {
        ReporteIncendioDTO reporteFallback = new ReporteIncendioDTO();
        reporteFallback.setDescripcion("Información del reporte no disponible temporalmente debido a problemas de conexión.");
        reporteFallback.setEstado("DESCONOCIDO");
        return reporteFallback;
    }

}

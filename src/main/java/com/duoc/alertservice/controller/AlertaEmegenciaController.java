package com.duoc.alertservice.controller;

import com.duoc.alertservice.model.AlertaEmergencia;
import com.duoc.alertservice.service.AlertaEmergenciaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/alerta-emergencia")
public class AlertaEmegenciaController {
    @Autowired
    private AlertaEmergenciaService alertaEmergenciaService;

    //Obtener todas las alertas de emergencia. No se requiere ningún parámetro ni cuerpo para esta consulta.
    @GetMapping
    public List<AlertaEmergencia> findAll() {
        return alertaEmergenciaService.findAll();
    }

    //Obtener alerta de emergencia por id. Se debe enviar el id de la alerta en la URL. (Ejemplo: /api/alerta-emergencia/1)
    @GetMapping("/{id}")
    public AlertaEmergencia findById(@PathVariable Long id) {
        return alertaEmergenciaService.findById(id);
    }

    //Obtener alertas de emergencia por nivel de riesgo. Se debe enviar el nivel de riesgo en la URL. (Ejemplo: /api/alerta-emergencia/nivel-riesgo?nivelRiesgo=CATASTROFE)
    @GetMapping("/nivel-riesgo")
    public List<AlertaEmergencia> findByNivelRiesgo(@RequestParam AlertaEmergencia.NivelRiesgo nivelRiesgo) {
        return alertaEmergenciaService.findByNivelRiesgo(nivelRiesgo);
    }

    //Obtener alertas de emergencia por rango de fechas. Se deben enviar las fechas en formato ISO 8601.
    //(Ejemplo: /api/alerta-emergencia/rango-fechas?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-12-31T23:59:59)
    @GetMapping("/rango-fechas")
    public List<AlertaEmergencia> findByFechaEmisionRango(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        return alertaEmergenciaService.findByFechaEmisionRango(fechaInicio, fechaFin);
    }

    //Crear alerta de emergencia. Se debe enviar la alerta en el cuerpo de la solicitud.
    //El cuerpo puede contener todos los campos de la alerta, excepto el id y la fechaEmision..
    //Los campos de la alerta a ingresar son: idReporte (Long), mensajeAlerta (String) y nivelRiesgo (PREVENTIVO/EVACUACION/CATASTROFE).
    //Ejemplo de cuerpo de solicitud:
    /*
    {
        "idReporte": 1,
        "mensajeAlerta": "Esta es una alerta de emergencia",
        "nivelRiesgo": "CATASTROFE"
    }
    */
    @PostMapping
    public AlertaEmergencia createAlertaEmergencia(@RequestBody AlertaEmergencia alerta) {
        return alertaEmergenciaService.generarAlertaDesdeReporte(alerta.getIdReporte());
    }

    //Actualizar alerta de emergencia por id. Se debe enviar el id de la alerta a actualizar en la URL y la alerta actualizada en el cuerpo de la solicitud (sin id en el cuerpo).
    @PutMapping("/{id}")
    public AlertaEmergencia updateAlertaEmergencia(@PathVariable Long id, @RequestBody AlertaEmergencia alerta) {
        alerta.setId(id);
        return alertaEmergenciaService.save(alerta);
    }

    //Eliminar alerta de emergencia por id. Se debe enviar el id de la alerta a eliminar en la URL. (Ejemplo: /api/alerta-emergencia/1)
    @DeleteMapping("/{id}")
    public void deleteAlertaEmergencia(@PathVariable Long id) {
        alertaEmergenciaService.deleteById(id);
    }
}

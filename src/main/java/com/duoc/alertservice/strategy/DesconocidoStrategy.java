package com.duoc.alertservice.strategy;

import com.duoc.alertservice.model.AlertaEmergencia;

public class DesconocidoStrategy implements NivelRiesgoStrategy {

    @Override
    public AlertaEmergencia.NivelRiesgo determinarNivelRiesgo() {
        return AlertaEmergencia.NivelRiesgo.PREVENTIVO;
    }

    @Override
    public String generarMensaje(String descripcion) {
        return "Alerta Preventiva (Sin conexion al origen): " + descripcion;
    }
}

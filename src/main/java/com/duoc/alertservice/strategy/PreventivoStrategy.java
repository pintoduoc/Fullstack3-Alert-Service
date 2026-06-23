package com.duoc.alertservice.strategy;

import com.duoc.alertservice.model.AlertaEmergencia;

public class PreventivoStrategy implements NivelRiesgoStrategy {

    @Override
    public AlertaEmergencia.NivelRiesgo determinarNivelRiesgo() {
        return AlertaEmergencia.NivelRiesgo.PREVENTIVO;
    }

    @Override
    public String generarMensaje(String descripcion) {
        return "Incidente en observacion: " + descripcion;
    }
}

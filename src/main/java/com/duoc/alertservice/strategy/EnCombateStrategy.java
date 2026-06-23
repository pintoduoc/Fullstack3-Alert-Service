package com.duoc.alertservice.strategy;

import com.duoc.alertservice.model.AlertaEmergencia;

public class EnCombateStrategy implements NivelRiesgoStrategy {

    @Override
    public AlertaEmergencia.NivelRiesgo determinarNivelRiesgo() {
        return AlertaEmergencia.NivelRiesgo.CATASTROFE;
    }

    @Override
    public String generarMensaje(String descripcion) {
        return "ALERTA DE EVACUACION: " + descripcion;
    }
}

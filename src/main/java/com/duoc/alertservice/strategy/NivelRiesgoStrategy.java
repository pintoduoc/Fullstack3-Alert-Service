package com.duoc.alertservice.strategy;

import com.duoc.alertservice.model.AlertaEmergencia;

public interface NivelRiesgoStrategy {
    AlertaEmergencia.NivelRiesgo determinarNivelRiesgo();
    String generarMensaje(String descripcion);
}

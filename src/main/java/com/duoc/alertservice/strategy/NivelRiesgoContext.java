package com.duoc.alertservice.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NivelRiesgoContext {

    private final Map<String, NivelRiesgoStrategy> strategies = new ConcurrentHashMap<>();

    public NivelRiesgoContext() {
        strategies.put("EN_COMBATE", new EnCombateStrategy());
        strategies.put("DESCONOCIDO", new DesconocidoStrategy());
    }

    public NivelRiesgoStrategy getStrategy(String estadoReporte) {
        return strategies.getOrDefault(estadoReporte, new PreventivoStrategy());
    }
}

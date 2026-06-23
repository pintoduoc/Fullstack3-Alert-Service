package com.duoc.alertservice.strategy;

import com.duoc.alertservice.model.AlertaEmergencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NivelRiesgoContextTest {

    private final NivelRiesgoContext context = new NivelRiesgoContext();

    @Test
    void testStrategyEnCombate_ReturnsCatastrofe() {
        NivelRiesgoStrategy strategy = context.getStrategy("EN_COMBATE");
        assertEquals(AlertaEmergencia.NivelRiesgo.CATASTROFE, strategy.determinarNivelRiesgo());
        assertTrue(strategy.generarMensaje("Incendio en cerro").contains("ALERTA DE EVACUACION"));
    }

    @Test
    void testStrategyDesconocido_ReturnsPreventivo() {
        NivelRiesgoStrategy strategy = context.getStrategy("DESCONOCIDO");
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, strategy.determinarNivelRiesgo());
        assertTrue(strategy.generarMensaje("Sin conexion").contains("Preventiva"));
    }

    @Test
    void testStrategyPendiente_ReturnsPreventivo() {
        NivelRiesgoStrategy strategy = context.getStrategy("PENDIENTE");
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, strategy.determinarNivelRiesgo());
        assertTrue(strategy.generarMensaje("En revision").contains("observacion"));
    }

    @Test
    void testStrategyControlado_ReturnsPreventivo() {
        NivelRiesgoStrategy strategy = context.getStrategy("CONTROLADO");
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, strategy.determinarNivelRiesgo());
    }

    @Test
    void testStrategyExtinguido_ReturnsPreventivo() {
        NivelRiesgoStrategy strategy = context.getStrategy("EXTINGUIDO");
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, strategy.determinarNivelRiesgo());
    }

    @Test
    void testStrategyUnknown_ReturnsPreventivo() {
        NivelRiesgoStrategy strategy = context.getStrategy("NO_EXISTE");
        assertEquals(AlertaEmergencia.NivelRiesgo.PREVENTIVO, strategy.determinarNivelRiesgo());
    }
}

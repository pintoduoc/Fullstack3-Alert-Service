package com.duoc.alertservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alerta_emergencia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaEmergencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idReporte;
    private String mensajeAlerta;
    private LocalDateTime fechaEmision;
    @Enumerated(EnumType.STRING)
    private NivelRiesgo nivelRiesgo;
    public enum NivelRiesgo {
        PREVENTIVO,
        EVACUACION,
        CATASTROFE
    }
}

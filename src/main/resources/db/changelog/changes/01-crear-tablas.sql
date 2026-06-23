-- liquibase formatted sql

-- changeset benja:1
CREATE TABLE alerta_emergencia (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   id_reporte BIGINT,
                                   mensaje_alerta VARCHAR(255),
                                   fecha_emision DATETIME,
                                   nivel_riesgo VARCHAR(50)
);
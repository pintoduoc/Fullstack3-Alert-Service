-- liquibase formatted sql

-- changeset benja:2
INSERT INTO alerta_emergencia (id_reporte, mensaje_alerta, fecha_emision, nivel_riesgo) VALUES
                                                                                            (1, 'Posible foco de incendio en observación, manténgase atento.', '2026-05-01 10:00:00', 'PREVENTIVO'),
                                                                                            (2, 'ALERTA: Evacue la zona norte inmediatamente. Riesgo alto.', '2026-05-02 14:30:00', 'EVACUACION'),
                                                                                            (3, 'Incendio en pastizales controlado, no hay riesgo para viviendas.', '2026-05-03 09:15:00', 'PREVENTIVO'),
                                                                                            (4, 'Emergencia superada. Puede volver a sus actividades.', '2026-05-04 18:45:00', 'PREVENTIVO'),
                                                                                            (5, 'CATASTROFE COMUNAL: Incendio forestal fuera de control. Siga instrucciones.', '2026-05-05 16:20:00', 'CATASTROFE'),
                                                                                            (6, 'Fuego menor reportado, equipos de emergencia en camino.', '2026-05-06 11:10:00', 'PREVENTIVO'),
                                                                                            (7, 'Fuego completamente extinguido. Gracias por reportar.', '2026-05-06 15:00:00', 'PREVENTIVO'),
                                                                                            (8, 'ALERTA: Evacuación preventiva por rebrote en sector cordillerano.', '2026-05-07 08:30:00', 'EVACUACION'),
                                                                                            (9, 'Situación contenida por nuestras brigadas.', '2026-05-07 12:00:00', 'PREVENTIVO'),
                                                                                            (10, 'Humo denso reportado. Evite actividades físicas al aire libre.', '2026-05-07 13:15:00', 'PREVENTIVO');
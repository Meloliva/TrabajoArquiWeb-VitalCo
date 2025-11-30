-- =============================================
-- ROLES
-- =============================================
INSERT INTO roles (tipo) VALUES
                             ('ADMIN'),
                             ('PACIENTE'),
                             ('NUTRICIONISTA');

-- =============================================
-- TURNOS
-- =============================================
INSERT INTO turno (inicioturno, finturno) VALUES
                                              ('08:00:00', '12:00:00'),
                                              ('14:00:00', '18:00:00');

-- =============================================
-- HORARIOS DE COMIDA
-- =============================================
INSERT INTO horario (nombre) VALUES
                                 ('Desayuno'),
                                 ('Snack'),
                                 ('Almuerzo'),
                                 ('Cena');

-- =============================================
-- PLANES DE SUSCRIPCIÓN
-- =============================================
INSERT INTO plansuscripcion (tipo, precio, beneficios_plan, terminos_condiciones) VALUES
                                                                                      ('Plan free', 0.00, 'Acceso a recetas básicas, Calculadora nutricional, Seguimiento básico de progreso', 'Plan gratuito sin compromisos. Funcionalidades limitadas. No incluye asesoría personalizada.'),
                                                                                      ('Plan premium', 95.99, 'Acceso ilimitado a todas las recetas, Asesoría personalizada con nutricionista, Planes alimenticios personalizados, Seguimiento detallado, Videoconsultas', 'Pago mensual. Cancela cuando quieras. Incluye seguimiento nutricional profesional y consultas ilimitadas con nutricionistas certificados.');

-- =============================================
-- PLANES NUTRICIONALES
-- =============================================
INSERT INTO plannutricional (duracion, objetivo) VALUES
                                                     ('3 meses', 'reducir nivel de trigliceridos'),
                                                     ('6 meses', 'reducir nivel de trigliceridos'),
                                                     ('12 meses', 'reducir nivel de trigliceridos'),
                                                     ('3 meses', 'mantener mi salud'),
                                                     ('6 meses', 'mantener mi salud'),
                                                     ('12 meses', 'mantener mi salud');

-- =============================================
-- USUARIOS (Contraseña: password123)
-- =============================================
INSERT INTO usuario (dni, "contraseña", nombre, apellido, correo, genero, estado, idrol, foto_perfil) VALUES
-- Admin
('12345678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Carlos', 'Rodríguez', 'admin@vitalco.com', 'Masculino', 'Activo', 1, 'https://i.pravatar.cc/150?img=12'),

-- Nutricionistas
('23456789', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'María', 'González', 'maria.gonzalez@vitalco.com', 'Femenino', 'Activo', 3, 'https://i.pravatar.cc/150?img=47'),
('34567890', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Roberto', 'Fernández', 'roberto.fernandez@vitalco.com', 'Masculino', 'Activo', 3, 'https://i.pravatar.cc/150?img=33'),
('11223344', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Patricia', 'Torres', 'patricia.torres@vitalco.com', 'Femenino', 'Activo', 3, 'https://i.pravatar.cc/150?img=28'),
('55667788', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Diego', 'Vargas', 'diego.vargas@vitalco.com', 'Masculino', 'Activo', 3, 'https://i.pravatar.cc/150?img=15'),

-- Pacientes Free
('45678901', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ana', 'López', 'ana.lopez@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=5'),
('56789012', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Pedro', 'Martínez', 'pedro.martinez@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=14'),
('87654321', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sofía', 'Mendoza', 'sofia.mendoza@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=9'),
('98765432', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Miguel', 'Castro', 'miguel.castro@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=51'),

-- Pacientes Premium
('67890123', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Laura', 'Sánchez', 'laura.sanchez@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=45'),
('78901234', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jorge', 'Ramírez', 'jorge.ramirez@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=52'),
('11112222', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Valentina', 'Flores', 'valentina.flores@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=44'),
('33334444', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ricardo', 'Morales', 'ricardo.morales@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=13'),
('22223333', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Camila', 'Ruiz', 'camila.ruiz@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=32'),
('44445555', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Andrés', 'Vega', 'andres.vega@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=59'),

-- =============================================
-- NUTRICIONISTAS
-- =============================================
INSERT INTO nutricionista (idusuario, asociaciones, universidad, grado_academico, idturno) VALUES
                                                                                               (2, 'Colegio de Nutricionistas del Perú', 'Universidad Peruana de Ciencias Aplicadas', 'Magister en Nutrición Clínica', 1),
                                                                                               (3, 'Asociación Peruana de Nutrición', 'Universidad Nacional Mayor de San Marcos', 'Licenciado en Nutrición', 2),
                                                                                               (4, 'Colegio de Nutricionistas del Perú, Asociación Latinoamericana de Nutrición', 'Universidad de Lima', 'Magister en Nutrición Deportiva', 1),
                                                                                               (5, 'Asociación Peruana de Nutrición', 'Universidad Cayetano Heredia', 'Especialista en Nutrición Pediátrica', 2);

-- =============================================
-- PACIENTES (Sin planes alimenticios, se generan automáticamente)
-- =============================================
INSERT INTO paciente (idusuario, altura, peso, edad, idplan, trigliceridos, actividad_fisica, idplannutricional) VALUES
-- Pacientes Free
(6, 1.65, 68.5, 28, 1, 180.00, 'Sedentario', 1),
(7, 1.78, 85.0, 35, 1, 220.00, 'Moderadamente activo', 2),
(8, 1.58, 62.0, 25, 1, 190.00, 'Sedentario', 4),
(9, 1.82, 92.0, 40, 1, 240.00, 'Moderadamente activo', 1),

-- Pacientes Premium
(10, 1.60, 72.0, 42, 2, 250.00, 'Sedentario', 1),
(11, 1.75, 90.0, 38, 2, 195.00, 'Muy activo', 3),
(12, 1.68, 75.0, 33, 2, 210.00, 'Moderadamente activo', 2),
(13, 1.80, 88.0, 45, 2, 230.00, 'Sedentario', 1),
(14, 1.62, 65.0, 29, 2, 170.00, 'Muy activo', 5),
(15, 1.77, 82.0, 36, 2, 200.00, 'Moderadamente activo', 2);

-- =============================================
-- RECETAS
-- =============================================
-- Desayunos (15 recetas)
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto) VALUES
                                                                                                                                                               ('Avena con frutas', 'Avena nutritiva con frutas frescas', 15, 45.0, 250.0, 5.0, 8.0, '1/2 taza de avena, 1 plátano, fresas, arándanos, miel', '1. Cocinar la avena con agua. 2. Agregar frutas picadas. 3. Endulzar con miel al gusto.', 1.0, 1, 'https://images.unsplash.com/photo-1517673132405-a56a62b18caf'),
                                                                                                                                                               ('Tostadas integrales con palta', 'Pan integral con palta y tomate', 10, 35.0, 280.0, 15.0, 7.0, '2 rebanadas de pan integral, 1/2 palta, tomate, sal, pimienta', '1. Tostar el pan. 2. Machacar la palta. 3. Untar en el pan y agregar tomate.', 1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),
                                                                                                                                                               ('Yogurt con granola', 'Yogurt natural con granola casera', 5, 40.0, 220.0, 6.0, 12.0, '1 taza de yogurt griego, 1/4 taza de granola, miel, frutos secos', '1. Servir el yogurt. 2. Agregar granola. 3. Decorar con miel y frutos secos.', 1.0, 1, 'https://images.unsplash.com/photo-1488477181946-6428a0291777'),
                                                                                                                                                               ('Huevos revueltos con espinaca', 'Huevos con espinaca fresca', 10, 8.0, 220.0, 14.0, 18.0, '3 huevos, espinaca fresca, cebolla, tomate', '1. Batir los huevos. 2. Saltear espinaca. 3. Agregar huevos y revolver.', 1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),
                                                                                                                                                               ('Panqueques de avena', 'Panqueques saludables de avena', 20, 40.0, 290.0, 8.0, 12.0, '1 taza avena molida, 2 huevos, plátano, canela', '1. Mezclar ingredientes. 2. Cocinar en sartén. 3. Servir con fruta.', 1.0, 1, 'https://images.unsplash.com/photo-1506084868230-bb9d95c24759'),
                                                                                                                                                               ('Batido verde energético', 'Smoothie verde con espinaca y frutas', 10, 32.0, 180.0, 4.0, 6.0, 'Espinaca, plátano, manzana verde, agua de coco, chía', '1. Licuar todos los ingredientes. 2. Servir frío con hielo.', 1.0, 1, 'https://images.unsplash.com/photo-1610970881699-44a5587cabec'),
                                                                                                                                                               ('Omelette de claras', 'Omelette ligero de claras con vegetales', 12, 10.0, 160.0, 5.0, 20.0, '4 claras de huevo, champiñones, pimiento, cebolla', '1. Batir claras. 2. Saltear vegetales. 3. Cocinar omelette.', 1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),
                                                                                                                                                               ('Bowl de açaí', 'Bowl de açaí con granola y frutas', 10, 48.0, 320.0, 10.0, 8.0, 'Pulpa de açaí, plátano, granola, coco rallado, miel', '1. Licuar açaí con plátano. 2. Servir en bowl. 3. Decorar con toppings.', 1.0, 1, 'https://images.unsplash.com/photo-1590301157890-4810ed352733'),
                                                                                                                                                               ('Tamal de quinua', 'Tamal peruano saludable con quinua', 25, 42.0, 280.0, 8.0, 12.0, 'Quinua, pollo desmenuzado, ají amarillo, hojas de plátano', '1. Cocinar quinua. 2. Mezclar con pollo. 3. Envolver y cocinar al vapor.', 1.0, 1, 'https://images.unsplash.com/photo-1628191011227-0d6d2f6d9b8d'),
                                                                                                                                                               ('Pan con chicharrón light', 'Versión saludable del clásico peruano', 20, 38.0, 340.0, 12.0, 25.0, 'Pan integral, lomo de cerdo magro, camote, ensalada criolla', '1. Cocinar lomo al horno. 2. Calentar pan. 3. Armar sandwich.', 1.0, 1, 'https://images.unsplash.com/photo-1509440159596-0249088772ff'),
                                                                                                                                                               ('Quinua con leche', 'Versión peruana del arroz con leche', 30, 45.0, 260.0, 6.0, 10.0, 'Quinua, leche evaporada light, canela, pasas, clavo', '1. Cocinar quinua con leche. 2. Agregar canela y clavo. 3. Endulzar al gusto.', 1.0, 1, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb'),
                                                                                                                                                               ('Desayuno andino', 'Mix de granos andinos con frutas', 15, 50.0, 290.0, 7.0, 11.0, 'Kiwicha, quinua, cañihua, frutas secas, miel', '1. Cocinar granos. 2. Mezclar con frutas. 3. Servir tibio.', 1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),
                                                                                                                                                               ('Jugo de papaya con avena', 'Jugo nutritivo peruano', 8, 38.0, 200.0, 3.0, 6.0, 'Papaya, avena, leche de soya, miel, hielo', '1. Licuar papaya con avena. 2. Agregar leche. 3. Servir frío.', 1.0, 1, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba'),
                                                                                                                                                               ('Chicha morada bowl', 'Bowl inspirado en la bebida tradicional', 20, 44.0, 240.0, 5.0, 7.0, 'Maíz morado, piña, manzana, yogurt griego, granola', '1. Preparar chicha morada. 2. Congelar en cubos. 3. Licuar y servir.', 1.0, 1, 'https://images.unsplash.com/photo-1590301157890-4810ed352733'),
                                                                                                                                                               ('Pan con palta y huevo', 'Desayuno completo peruano', 15, 36.0, 310.0, 16.0, 15.0, 'Pan integral, palta, huevo pochado, tomate, sal', '1. Tostar pan. 2. Preparar huevo pochado. 3. Armar con palta.', 1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8');

-- Snacks (12 recetas)
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto) VALUES
                                                                                                                                                               ('Frutos secos mixtos', 'Mix de almendras, nueces y pasas', 0, 20.0, 180.0, 12.0, 6.0, 'Almendras, nueces, pasas, arándanos secos', '1. Mezclar todos los ingredientes en un bowl.', 1.0, 2, 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32'),
                                                                                                                                                               ('Manzana con mantequilla de maní', 'Manzana fresca con mantequilla de maní natural', 5, 25.0, 200.0, 10.0, 4.0, '1 manzana, 2 cucharadas de mantequilla de maní', '1. Cortar la manzana en rodajas. 2. Untar con mantequilla de maní.', 1.0, 2, 'https://images.unsplash.com/photo-1619546813926-a78fa6372cd2'),
                                                                                                                                                               ('Smoothie de proteína', 'Batido proteico con frutas', 10, 30.0, 250.0, 8.0, 20.0, '1 scoop proteína, 1 plátano, leche de almendras, espinaca', '1. Licuar todos los ingredientes. 2. Servir frío.', 1.0, 2, 'https://images.unsplash.com/photo-1505252585461-04db1eb84625'),
                                                                                                                                                               ('Palitos de zanahoria con hummus', 'Snack saludable y nutritivo', 5, 18.0, 150.0, 7.0, 5.0, 'Zanahorias, garbanzos, tahini, limón, ajo', '1. Cortar zanahorias. 2. Preparar hummus. 3. Servir juntos.', 1.0, 2, 'https://images.unsplash.com/photo-1505253758473-96b7015fcd40'),
                                                                                                                                                               ('Choclo con queso fresco', 'Snack peruano tradicional', 15, 30.0, 220.0, 8.0, 10.0, 'Choclo peruano, queso fresco, sal', '1. Cocinar choclo al vapor. 2. Servir con queso fresco.', 1.0, 2, 'https://images.unsplash.com/photo-1551754655-cd27e38d2076'),
                                                                                                                                                               ('Camote asado', 'Camote horneado con canela', 35, 35.0, 180.0, 2.0, 3.0, 'Camote morado o amarillo, canela, miel opcional', '1. Lavar camote. 2. Hornear a 180°C por 30 min. 3. Espolvorear canela.', 1.0, 2, 'https://images.unsplash.com/photo-1604909052743-94e838986d24'),
                                                                                                                                                               ('Plátano con canela', 'Plátano horneado especiado', 20, 28.0, 140.0, 1.0, 2.0, 'Plátano de isla, canela, miel', '1. Cortar plátano. 2. Hornear con canela. 3. Servir tibio.', 1.0, 2, 'https://images.unsplash.com/photo-1603833665858-e61d17a86224'),
                                                                                                                                                               ('Yogurt con aguaymanto', 'Yogurt con fruta peruana', 5, 26.0, 170.0, 4.0, 10.0, 'Yogurt griego, aguaymanto fresco, miel', '1. Servir yogurt. 2. Agregar aguaymanto. 3. Endulzar.', 1.0, 2, 'https://images.unsplash.com/photo-1488477181946-6428a0291777'),
                                                                                                                                                               ('Mazamorra morada', 'Postre tradicional peruano light', 30, 40.0, 200.0, 2.0, 3.0, 'Maíz morado, piña, manzana, canela, azúcar morena', '1. Cocinar maíz morado. 2. Agregar frutas. 3. Espesar con fécula.', 1.0, 2, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb'),
                                                                                                                                                               ('Barrita de kiwicha', 'Barrita energética andina', 0, 32.0, 190.0, 6.0, 7.0, 'Kiwicha reventada, miel, frutos secos, pasas', '1. Mezclar kiwicha con miel. 2. Moldear barritas. 3. Dejar enfriar.', 1.0, 2, 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32'),
                                                                                                                                                               ('Lúcuma smoothie', 'Batido de lúcuma cremoso', 8, 34.0, 210.0, 5.0, 8.0, 'Pulpa de lúcuma, leche de almendras, plátano, hielo', '1. Licuar todos los ingredientes. 2. Servir bien frío.', 1.0, 2, 'https://images.unsplash.com/photo-1505252585461-04db1eb84625'),
                                                                                                                                                               ('Tejas con pecanas', 'Versión saludable de dulce peruano', 5, 28.0, 180.0, 9.0, 4.0, 'Pecanas, lúcuma, chocolate oscuro', '1. Derretir chocolate. 2. Cubrir pecanas. 3. Dejar enfriar.', 1.0, 2, 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32');

-- Almuerzos (18 recetas)
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto) VALUES
                                                                                                                                                               ('Pollo a la plancha con quinua', 'Pechuga de pollo con quinua y vegetales', 30, 45.0, 400.0, 10.0, 35.0, '150g pechuga de pollo, 1/2 taza quinua, brócoli, zanahoria', '1. Cocinar el pollo a la plancha. 2. Cocinar la quinua. 3. Saltear vegetales.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Pescado al horno con ensalada', 'Filete de pescado con ensalada fresca', 35, 20.0, 350.0, 12.0, 40.0, '150g filete de pescado, lechuga, tomate, cebolla, limón', '1. Hornear el pescado con limón. 2. Preparar ensalada fresca.', 1.0, 3, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Lentejas con arroz integral', 'Guiso de lentejas con arroz', 40, 55.0, 380.0, 8.0, 18.0, '1 taza de lentejas, 1/2 taza arroz integral, cebolla, tomate, ajo', '1. Cocinar las lentejas. 2. Cocinar el arroz. 3. Mezclar y servir.', 1.0, 3, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe'),
                                                                                                                                                               ('Ensalada de atún', 'Ensalada completa con atún', 15, 25.0, 320.0, 14.0, 28.0, '1 lata de atún, lechuga, tomate, palta, aceitunas, limón', '1. Mezclar todos los ingredientes. 2. Aliñar con limón.', 1.0, 3, 'https://images.unsplash.com/photo-1546793665-c74683f339c1'),
                                                                                                                                                               ('Arroz chaufa fitness', 'Versión saludable del arroz chaufa', 25, 50.0, 420.0, 12.0, 30.0, 'Arroz integral, pollo, huevo, cebolla china, sillao light', '1. Cocinar arroz. 2. Saltear ingredientes. 3. Mezclar todo.', 1.0, 3, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b'),
                                                                                                                                                               ('Bowl de pavo con camote', 'Bowl nutritivo con pavo molido', 30, 45.0, 390.0, 11.0, 32.0, 'Pavo molido, camote, brócoli, pimiento', '1. Cocinar pavo. 2. Hornear camote. 3. Saltear vegetales.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Ceviche de pescado clásico', 'Ceviche peruano auténtico', 25, 15.0, 200.0, 3.0, 25.0, 'Pescado fresco, limón, cebolla morada, ají limo, cilantro, camote, choclo', '1. Cortar pescado en cubos. 2. Marinar con limón 10 min. 3. Agregar cebolla y ají.', 1.0, 3, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Causa limeña light', 'Versión saludable de causa', 35, 42.0, 320.0, 8.0, 22.0, 'Papa amarilla, atún, palta, limón, ají amarillo, mayonesa light', '1. Hacer puré de papa. 2. Mezclar atún. 3. Armar capas y refrigerar.', 1.0, 3, 'https://images.unsplash.com/photo-1626074353765-517a4c4e8f69'),
                                                                                                                                                               ('Lomo saltado con quinua', 'Versión fitness del clásico peruano', 30, 48.0, 450.0, 14.0, 38.0, 'Lomo de res, cebolla, tomate, ají amarillo, quinua, sillao light', '1. Saltear carne. 2. Agregar vegetales. 3. Servir con quinua.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Ají de gallina light', 'Versión saludable del ají de gallina', 40, 40.0, 380.0, 12.0, 30.0, 'Pechuga de pollo, ají amarillo, leche evaporada light, pan integral, nueces', '1. Cocinar pollo. 2. Licuar ají con leche. 3. Cocinar salsa y agregar pollo.', 1.0, 3, 'https://images.unsplash.com/photo-1606787366850-de6330128bfc'),
                                                                                                                                                               ('Tacu tacu con pechuga', 'Tacu tacu tradicional fitness', 35, 52.0, 420.0, 10.0, 32.0, 'Frejoles, arroz integral, pechuga a la plancha, cebolla, ajo', '1. Mezclar frejoles y arroz. 2. Formar tacu tacu. 3. Servir con pollo.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Seco de res con frejoles', 'Guiso peruano tradicional', 50, 48.0, 440.0, 15.0, 35.0, 'Carne de res, cilantro, chicha de jora, frejoles, zapallo', '1. Dorar carne. 2. Cocinar con cilantro. 3. Servir con frejoles.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Arroz con mariscos', 'Arroz peruano con frutos del mar', 35, 50.0, 410.0, 11.0, 28.0, 'Arroz, langostinos, calamar, mejillones, ají amarillo, culantro', '1. Saltear mariscos. 2. Cocinar arroz con caldo. 3. Mezclar todo.', 1.0, 3, 'https://images.unsplash.com/photo-1559847844-5315695dadae'),
                                                                                                                                                               ('Escabeche de pescado', 'Pescado en escabeche peruano', 40, 28.0, 340.0, 12.0, 32.0, 'Pescado frito, cebolla, vinagre, ají panca, camote, aceitunas', '1. Freír pescado. 2. Preparar escabeche. 3. Marinar pescado.', 1.0, 3, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Carapulcra con sopa seca', 'Plato tradicional de Chincha', 60, 58.0, 480.0, 16.0, 30.0, 'Papa seca, cerdo, ají panca, fideos, albahaca, culantro', '1. Cocinar carapulcra. 2. Preparar sopa seca. 3. Servir juntos.', 1.0, 3, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9'),
                                                                                                                                                               ('Trucha frita con ensalada', 'Trucha fresca de la sierra', 25, 18.0, 350.0, 14.0, 38.0, 'Trucha fresca, harina, limón, ensalada fresca', '1. Limpiar trucha. 2. Freír ligeramente. 3. Servir con ensalada.', 1.0, 3, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Pollo al cilantro', 'Pollo en salsa verde peruana', 35, 25.0, 370.0, 13.0, 40.0, 'Pechuga de pollo, cilantro, ají verde, cerveza, espinaca', '1. Licuar salsa verde. 2. Cocinar pollo. 3. Servir con arroz.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Arroz con pato', 'Especialidad norteña del Perú', 70, 55.0, 520.0, 18.0, 35.0, 'Pato, cilantro, chicha de jora, arroz, arvejas, pimiento', '1. Cocinar pato. 2. Preparar arroz verde. 3. Servir juntos.', 1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c');
-- Cenas (15 recetas)
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto) VALUES
                                                                                                                                                               ('Tortilla de verduras', 'Tortilla con espinaca y champiñones', 20, 15.0, 250.0, 12.0, 20.0, '3 huevos, espinaca, champiñones, cebolla, sal, pimienta', '1. Saltear verduras. 2. Batir huevos y agregar. 3. Cocinar a fuego medio.', 1.0, 4, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),
                                                                                                                                                               ('Sopa de pollo con vegetales', 'Sopa casera nutritiva', 30, 30.0, 280.0, 8.0, 25.0, 'Pechuga de pollo, zanahoria, apio, cebolla, fideos integrales', '1. Cocinar el pollo. 2. Agregar vegetales. 3. Cocinar fideos.', 1.0, 4, 'https://images.unsplash.com/photo-1547592166-23ac45744acd'),
                                                                                                                                                               ('Salmón con espárragos', 'Salmón al vapor con espárragos', 25, 10.0, 320.0, 18.0, 35.0, '150g salmón, espárragos, limón, aceite de oliva, ajo', '1. Cocinar salmón al vapor. 2. Saltear espárragos. 3. Servir con limón.', 1.0, 4, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288'),
                                                                                                                                                               ('Wrap de pollo', 'Wrap integral con pollo y vegetales', 15, 35.0, 340.0, 10.0, 28.0, '1 tortilla integral, pollo desmenuzado, lechuga, tomate, yogurt', '1. Calentar tortilla. 2. Rellenar con pollo y vegetales. 3. Enrollar.', 1.0, 4, 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f'),
                                                                                                                                                               ('Ensalada César con pollo', 'Ensalada clásica versión light', 20, 20.0, 310.0, 15.0, 28.0, 'Lechuga romana, pollo, parmesano, aderezo light', '1. Cocinar pollo. 2. Preparar ensalada. 3. Mezclar con aderezo.', 1.0, 4, 'https://images.unsplash.com/photo-1546793665-c74683f339c1'),
                                                                                                                                                               ('Ceviche de pescado', 'Ceviche peruano tradicional', 30, 15.0, 200.0, 3.0, 25.0, 'Pescado fresco, limón, cebolla, ají, cilantro', '1. Cortar pescado. 2. Marinar con limón. 3. Agregar cebolla y ají.', 1.0, 4, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Aguadito de pollo', 'Sopa verde peruana reconfortante', 35, 32.0, 290.0, 9.0, 24.0, 'Pollo, arroz, cilantro, arvejas, zanahoria, ají amarillo', '1. Cocinar pollo con verduras. 2. Licuar cilantro. 3. Agregar arroz y cocinar.', 1.0, 4, 'https://images.unsplash.com/photo-1547592166-23ac45744acd'),
                                                                                                                                                               ('Chupe de camarones light', 'Versión ligera del chupe', 40, 35.0, 350.0, 11.0, 28.0, 'Camarones, papa, queso fresco, leche evaporada light, huevo, ají panca', '1. Cocinar papas. 2. Agregar camarones. 3. Añadir leche y huevo.', 1.0, 4, 'https://images.unsplash.com/photo-1559847844-5315695dadae'),
                                                                                                                                                               ('Tiradito de pescado', 'Ceviche estilo japonés-peruano', 20, 12.0, 220.0, 6.0, 28.0, 'Pescado fresco, limón, ají amarillo, aceite de oliva, cilantro', '1. Cortar pescado en láminas. 2. Preparar crema de ají. 3. Bañar pescado.', 1.0, 4, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Sudado de pescado', 'Pescado cocido en su jugo', 30, 20.0, 280.0, 8.0, 32.0, 'Pescado, cebolla, tomate, ají amarillo, culantro, chicha de jora', '1. Saltear cebolla y tomate. 2. Agregar pescado. 3. Cocinar tapado.', 1.0, 4, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),
                                                                                                                                                               ('Parihuela', 'Sopa de mariscos peruana', 40, 28.0, 320.0, 10.0, 30.0, 'Mariscos variados, pescado, ají panca, tomate, cebolla, cilantro', '1. Hacer caldo de pescado. 2. Agregar mariscos. 3. Cocinar brevemente.', 1.0, 4, 'https://images.unsplash.com/photo-1559847844-5315695dadae'),
                                                                                                                                                               ('Enrollado de pollo', 'Pechuga rellena al horno', 45, 18.0, 310.0, 11.0, 35.0, 'Pechuga de pollo, espinaca, queso fresco, zanahoria, pimiento', '1. Aplanar pechuga. 2. Rellenar y enrollar. 3. Hornear 35 min.', 1.0, 4, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Saltado de verduras', 'Salteado criollo vegetariano', 20, 32.0, 240.0, 8.0, 12.0, 'Brócoli, coliflor, zanahoria, vainitas, cebolla, tomate, sillao', '1. Saltear vegetales. 2. Agregar sillao. 3. Servir con arroz.', 1.0, 4, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd'),
                                                                                                                                                               ('Pollo al horno con hierbas', 'Pollo aromático al horno', 50, 15.0, 340.0, 12.0, 38.0, 'Piezas de pollo, romero, tomillo, ajo, limón, aceite de oliva', '1. Marinar pollo con hierbas. 2. Hornear 45 min. 3. Servir con ensalada.', 1.0, 4, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),
                                                                                                                                                               ('Crema de zapallo', 'Sopa cremosa peruana', 25, 28.0, 200.0, 6.0, 8.0, 'Zapallo, cebolla, ajo, caldo de pollo, leche evaporada light, queso', '1. Cocinar zapallo. 2. Licuar con caldo. 3. Servir con queso rallado.', 1.0, 4, 'https://images.unsplash.com/photo-1547592166-23ac45744acd');

-- =============================================
-- CITAS (Solo pacientes Premium pueden tener citas)
-- =============================================
-- Citas entre Laura Sánchez (paciente 10) y María González (nutricionista 2)
INSERT INTO cita (idpaciente, idnutricionista, dia, hora, descripcion, link, estado, asistio_paciente, asistio_nutricionista) VALUES
                                                                                                                                  (1, 1, '2024-12-15', '09:00:00', 'Consulta inicial y evaluación nutricional', 'https://meet.google.com/abc-defg-hij', 'Aceptada', true, true),
                                                                                                                                  (1, 1, '2024-12-22', '09:30:00', 'Seguimiento semanal de progreso', 'https://meet.google.com/xyz-uvwx-yz', 'Aceptada', true, true),
                                                                                                                                  (1, 1, CURRENT_DATE + 2, '10:00:00', 'Revisión de plan alimenticio', 'https://meet.google.com/fut-uro-123', 'Pendiente', false, false),

-- Citas entre Jorge Ramírez (paciente 11) y Roberto Fernández (nutricionista 3)
                                                                                                                                  (2, 2, '2024-12-18', '14:30:00', 'Primera consulta nutricional', 'https://meet.google.com/klm-nopq-rst', 'Aceptada', true, true),
                                                                                                                                  (2, 2, CURRENT_DATE + 1, '15:00:00', 'Control de triglicéridos', 'https://meet.google.com/man-ana-456', 'Pendiente', false, false),

-- Citas entre Valentina Flores (paciente 12) y Patricia Torres (nutricionista 4)
                                                                                                                                  (3, 3, '2024-12-10', '08:30:00', 'Evaluación inicial', 'https://meet.google.com/uvw-xyz-abc', 'Aceptada', true, true),
                                                                                                                                  (3, 3, '2024-12-20', '09:00:00', 'Ajuste de macronutrientes', 'https://meet.google.com/def-ghi-jkl', 'Aceptada', true, true),
                                                                                                                                  (3, 3, CURRENT_DATE + 5, '08:30:00', 'Seguimiento mensual', 'https://meet.google.com/sem-ana-789', 'Pendiente', false, false),

-- Citas entre Ricardo Morales (paciente 13) y Diego Vargas (nutricionista 5)
                                                                                                                                  (4, 4, '2024-12-12', '16:00:00', 'Consulta sobre actividad física', 'https://meet.google.com/mno-pqr-stu', 'Aceptada', true, true),
                                                                                                                                  (4, 4, CURRENT_DATE, '16:30:00', 'Revisión de resultados', 'https://meet.google.com/hoy-dia-012', 'Pendiente', false, false),

-- Citas entre Camila Ruiz (paciente 14) y María González (nutricionista 2)
                                                                                                                                  (5, 1, '2024-12-16', '10:30:00', 'Plan para mantenimiento', 'https://meet.google.com/vwx-yza-bcd', 'Aceptada', true, true),
                                                                                                                                  (5, 1, CURRENT_DATE + 3, '11:00:00', 'Control semanal', 'https://meet.google.com/prox-imo-345', 'Pendiente', false, false),

-- Citas entre Andrés Vega (paciente 15) y Roberto Fernández (nutricionista 3)
                                                                                                                                  (6, 2, '2024-12-14', '15:30:00', 'Evaluación deportiva', 'https://meet.google.com/efg-hij-klm', 'Aceptada', true, true),
                                                                                                                                  (6, 2, '2024-12-21', '16:00:00', 'Ajuste de plan deportivo', 'https://meet.google.com/nop-qrs-tuv', 'Aceptada', false, true),
                                                                                                                                  (6, 2, CURRENT_DATE + 7, '15:30:00', 'Seguimiento mensual', 'https://meet.google.com/fut-uro-678', 'Pendiente', false, false);

-- =============================================
-- PLANES ALIMENTICIOS
-- =============================================
-- Nota: En tu sistema se generan automáticamente, pero aquí algunos ejemplos
-- Los IDs deben coincidir con los pacientes

-- Plan para Ana López (paciente 1 - Free)
INSERT INTO planalimenticio (idpaciente, fechainicio, fechafin, grasas_diaria, carbohidratos_diaria, proteinas_diaria, calorias_diaria, idplannutricional, fecha_creacion) VALUES
                                                                                                                                                                               (1, '2024-11-01', '2025-02-01', 45.0, 180.0, 85.0, 1650.0, 1, '2024-11-01'),

-- Plan para Pedro Martínez (paciente 2 - Free)
                                                                                                                                                                               (2, '2024-11-05', '2025-05-05', 55.0, 200.0, 95.0, 1900.0, 2, '2024-11-05'),

-- Plan para Sofía Mendoza (paciente 3 - Free)
                                                                                                                                                                               (3, '2024-11-10', '2025-02-10', 42.0, 175.0, 80.0, 1600.0, 4, '2024-11-10'),

-- Plan para Miguel Castro (paciente 4 - Free)
                                                                                                                                                                               (4, '2024-11-15', '2025-02-15', 58.0, 210.0, 100.0, 2000.0, 1, '2024-11-15'),

-- Plan para Laura Sánchez (paciente 5 - Premium)
                                                                                                                                                                               (5, '2024-10-20', '2025-01-20', 50.0, 185.0, 90.0, 1750.0, 1, '2024-10-20'),

-- Plan para Jorge Ramírez (paciente 6 - Premium)
                                                                                                                                                                               (6, '2024-10-25', '2025-10-25', 60.0, 220.0, 110.0, 2100.0, 3, '2024-10-25'),

-- Plan para Valentina Flores (paciente 7 - Premium)
                                                                                                                                                                               (7, '2024-11-01', '2025-05-01', 48.0, 190.0, 88.0, 1800.0, 2, '2024-11-01'),

-- Plan para Ricardo Morales (paciente 8 - Premium)
                                                                                                                                                                               (8, '2024-11-08', '2025-02-08', 52.0, 195.0, 92.0, 1850.0, 1, '2024-11-08'),

-- Plan para Camila Ruiz (paciente 9 - Premium)
                                                                                                                                                                               (9, '2024-11-12', '2025-05-12', 46.0, 182.0, 87.0, 1700.0, 5, '2024-11-12'),

-- Plan para Andrés Vega (paciente 10 - Premium)
                                                                                                                                                                               (10, '2024-11-18', '2025-05-18', 56.0, 205.0, 98.0, 1950.0, 2, '2024-11-18');

-- =============================================
-- PLAN RECETA (uno por cada plan alimenticio)
-- =============================================
INSERT INTO planreceta (idplanalimenticio, favorito, fecharegistro) VALUES
                                                                        (1, false, '2024-11-01'),
                                                                        (2, false, '2024-11-05'),
                                                                        (3, false, '2024-11-10'),
                                                                        (4, false, '2024-11-15'),
                                                                        (5, false, '2024-10-20'),
                                                                        (6, false, '2024-10-25'),
                                                                        (7, false, '2024-11-01'),
                                                                        (8, false, '2024-11-08'),
                                                                        (9, false, '2024-11-12'),
                                                                        (10, false, '2024-11-18');

-- =============================================
-- PLAN RECETA RECETA (Relación muchos a muchos)
-- =============================================
-- Para Plan Receta 1 (Ana - Free, máximo 15 recetas)
INSERT INTO planreceta_receta (idplanreceta, idreceta, fecharegistro, favorito) VALUES
                                                                                    (1, 1, '2024-11-01', true),   -- Avena con frutas (favorito)
                                                                                    (1, 2, '2024-11-01', false),  -- Tostadas integrales
                                                                                    (1, 3, '2024-11-01', false),  -- Yogurt con granola
                                                                                    (1, 16, '2024-11-01', false), -- Frutos secos
                                                                                    (1, 17, '2024-11-01', true),  -- Manzana con mantequilla (favorito)
                                                                                    (1, 28, '2024-11-01', false), -- Pollo a la plancha
                                                                                    (1, 29, '2024-11-01', false), -- Pescado al horno
                                                                                    (1, 30, '2024-11-01', false), -- Lentejas con arroz
                                                                                    (1, 43, '2024-11-01', false), -- Tortilla de verduras
                                                                                    (1, 44, '2024-11-01', false), -- Sopa de pollo
                                                                                    (1, 45, '2024-11-01', true),  -- Salmón con espárragos (favorito)
                                                                                    (1, 5, '2024-11-01', false),  -- Panqueques de avena
                                                                                    (1, 18, '2024-11-01', false), -- Smoothie de proteína
                                                                                    (1, 31, '2024-11-01', false), -- Ensalada de atún
                                                                                    (1, 46, '2024-11-01', false), -- Wrap de pollo

-- Para Plan Receta 2 (Pedro - Free, máximo 15 recetas)
                                                                                    (2, 2, '2024-11-05', false),
                                                                                    (2, 4, '2024-11-05', true),
                                                                                    (2, 6, '2024-11-05', false),
                                                                                    (2, 16, '2024-11-05', false),
                                                                                    (2, 17, '2024-11-05', false),
                                                                                    (2, 19, '2024-11-05', false),
                                                                                    (2, 28, '2024-11-05', false),
                                                                                    (2, 30, '2024-11-05', true),
                                                                                    (2, 32, '2024-11-05', false),
                                                                                    (2, 34, '2024-11-05', false),
                                                                                    (2, 43, '2024-11-05', false),
                                                                                    (2, 44, '2024-11-05', false),
                                                                                    (2, 47, '2024-11-05', false),
                                                                                    (2, 50, '2024-11-05', false),
                                                                                    (2, 51, '2024-11-05', false),

-- Para Plan Receta 3 (Sofía - Free, máximo 15 recetas)
                                                                                    (3, 1, '2024-11-10', true),
                                                                                    (3, 3, '2024-11-10', false),
                                                                                    (3, 7, '2024-11-10', false),
                                                                                    (3, 16, '2024-11-10', false),
                                                                                    (3, 18, '2024-11-10', true),
                                                                                    (3, 20, '2024-11-10', false),
                                                                                    (3, 29, '2024-11-10', false),
                                                                                    (3, 31, '2024-11-10', false),
                                                                                    (3, 35, '2024-11-10', false),
                                                                                    (3, 43, '2024-11-10', false),
                                                                                    (3, 45, '2024-11-10', false),
                                                                                    (3, 48, '2024-11-10', false),
                                                                                    (3, 52, '2024-11-10', false),
                                                                                    (3, 54, '2024-11-10', false),
                                                                                    (3, 56, '2024-11-10', false),

-- Para Plan Receta 4 (Miguel - Free, máximo 15 recetas)
                                                                                    (4, 2, '2024-11-15', false),
                                                                                    (4, 5, '2024-11-15', true),
                                                                                    (4, 8, '2024-11-15', false),
                                                                                    (4, 17, '2024-11-15', false),
                                                                                    (4, 19, '2024-11-15', false),
                                                                                    (4, 21, '2024-11-15', false),
                                                                                    (4, 28, '2024-11-15', false),
                                                                                    (4, 32, '2024-11-15', true),
                                                                                    (4, 36, '2024-11-15', false),
                                                                                    (4, 44, '2024-11-15', false),
                                                                                    (4, 46, '2024-11-15', false),
                                                                                    (4, 49, '2024-11-15', false),
                                                                                    (4, 53, '2024-11-15', false),
                                                                                    (4, 55, '2024-11-15', false),
                                                                                    (4, 57, '2024-11-15', false),

-- Para Plan Receta 5 (Laura - Premium, todas las recetas disponibles)
                                                                                    (5, 1, '2024-10-20', true),
                                                                                    (5, 2, '2024-10-20', false),
                                                                                    (5, 3, '2024-10-20', true),
                                                                                    (5, 4, '2024-10-20', false),
                                                                                    (5, 5, '2024-10-20', false),
                                                                                    (5, 6, '2024-10-20', false),
                                                                                    (5, 7, '2024-10-20', false),
                                                                                    (5, 16, '2024-10-20', true),
                                                                                    (5, 17, '2024-10-20', false),
                                                                                    (5, 18, '2024-10-20', false),
                                                                                    (5, 19, '2024-10-20', false),
                                                                                    (5, 20, '2024-10-20', false),
                                                                                    (5, 28, '2024-10-20', true),
                                                                                    (5, 29, '2024-10-20', false),
                                                                                    (5, 30, '2024-10-20', false),
                                                                                    (5, 31, '2024-10-20', false),
                                                                                    (5, 32, '2024-10-20', false),
                                                                                    (5, 34, '2024-10-20', false),
                                                                                    (5, 43, '2024-10-20', false),
                                                                                    (5, 44, '2024-10-20', false),
                                                                                    (5, 45, '2024-10-20', true),
                                                                                    (5, 46, '2024-10-20', false),
                                                                                    (5, 47, '2024-10-20', false),
                                                                                    (5, 48, '2024-10-20', false),

-- Para Plan Receta 6 (Jorge - Premium)
                                                                                    (6, 1, '2024-10-25', false),
                                                                                    (6, 4, '2024-10-25', true),
                                                                                    (6, 5, '2024-10-25', false),
                                                                                    (6, 8, '2024-10-25', false),
                                                                                    (6, 11, '2024-10-25', false),
                                                                                    (6, 16, '2024-10-25', false),
                                                                                    (6, 18, '2024-10-25', true),
                                                                                    (6, 19, '2024-10-25', false),
                                                                                    (6, 22, '2024-10-25', false),
                                                                                    (6, 28, '2024-10-25', false),
                                                                                    (6, 32, '2024-10-25', true),
                                                                                    (6, 33, '2024-10-25', false),
                                                                                    (6, 36, '2024-10-25', false),
                                                                                    (6, 38, '2024-10-25', false),
                                                                                    (6, 43, '2024-10-25', false),
                                                                                    (6, 44, '2024-10-25', false),
                                                                                    (6, 46, '2024-10-25', false),
                                                                                    (6, 49, '2024-10-25', false),
                                                                                    (6, 51, '2024-10-25', false),
                                                                                    (6, 54, '2024-10-25', false),

-- Para Plan Receta 7 (Valentina - Premium)
                                                                                    (7, 2, '2024-11-01', true),
                                                                                    (7, 3, '2024-11-01', false),
                                                                                    (7, 6, '2024-11-01', false),
                                                                                    (7, 9, '2024-11-01', false),
                                                                                    (7, 12, '2024-11-01', false),
                                                                                    (7, 17, '2024-11-01', true),
                                                                                    (7, 18, '2024-11-01', false),
                                                                                    (7, 20, '2024-11-01', false),
                                                                                    (7, 23, '2024-11-01', false),
                                                                                    (7, 29, '2024-11-01', false),
                                                                                    (7, 30, '2024-11-01', false),
                                                                                    (7, 31, '2024-11-01', true),
                                                                                    (7, 35, '2024-11-01', false),
                                                                                    (7, 39, '2024-11-01', false),
                                                                                    (7, 43, '2024-11-01', false),
                                                                                    (7, 45, '2024-11-01', false),
                                                                                    (7, 47, '2024-11-01', false),
                                                                                    (7, 50, '2024-11-01', false),
                                                                                    (7, 52, '2024-11-01', false),
                                                                                    (7, 55, '2024-11-01', false),

-- Para Plan Receta 8 (Ricardo - Premium)
                                                                                    (8, 1, '2024-11-08', false),
                                                                                    (8, 5, '2024-11-08', true),
                                                                                    (8, 7, '2024-11-08', false),
                                                                                    (8, 10, '2024-11-08', false),
                                                                                    (8, 13, '2024-11-08', false),
                                                                                    (8, 16, '2024-11-08', false),
                                                                                    (8, 19, '2024-11-08', false),
                                                                                    (8, 21, '2024-11-08', true),
                                                                                    (8, 24, '2024-11-08', false),
                                                                                    (8, 28, '2024-11-08', false),
                                                                                    (8, 32, '2024-11-08', true),
                                                                                    (8, 34, '2024-11-08', false),
                                                                                    (8, 37, '2024-11-08', false),
                                                                                    (8, 40, '2024-11-08', false),
                                                                                    (8, 44, '2024-11-08', false),
                                                                                    (8, 46, '2024-11-08', false),
                                                                                    (8, 48, '2024-11-08', false),
                                                                                    (8, 51, '2024-11-08', false),
                                                                                    (8, 53, '2024-11-08', false),
                                                                                    (8, 56, '2024-11-08', false),

-- Para Plan Receta 9 (Camila - Premium)
                                                                                    (9, 3, '2024-11-12', true),
                                                                                    (9, 4, '2024-11-12', false),
                                                                                    (9, 8, '2024-11-12', false),
                                                                                    (9, 11, '2024-11-12', false),
                                                                                    (9, 14, '2024-11-12', false),
                                                                                    (9, 17, '2024-11-12', false),
                                                                                    (9, 18, '2024-11-12', true),
                                                                                    (9, 22, '2024-11-12', false),
                                                                                    (9, 25, '2024-11-12', false),
                                                                                    (9, 29, '2024-11-12', false),
                                                                                    (9, 31, '2024-11-12', false),
                                                                                    (9, 33, '2024-11-12', true),
                                                                                    (9, 36, '2024-11-12', false),
                                                                                    (9, 41, '2024-11-12', false),
                                                                                    (9, 43, '2024-11-12', false),
                                                                                    (9, 45, '2024-11-12', false),
                                                                                    (9, 49, '2024-11-12', false),
                                                                                    (9, 52, '2024-11-12', false),
                                                                                    (9, 54, '2024-11-12', false),
                                                                                    (9, 57, '2024-11-12', false),

-- Para Plan Receta 10 (Andrés - Premium)
                                                                                    (10, 2, '2024-11-18', false),
                                                                                    (10, 5, '2024-11-18', true),
                                                                                    (10, 9, '2024-11-18', false),
                                                                                    (10, 12, '2024-11-18', false),
                                                                                    (10, 15, '2024-11-18', false),
                                                                                    (10, 16, '2024-11-18', false),
                                                                                    (10, 19, '2024-11-18', true),
                                                                                    (10, 23, '2024-11-18', false),
                                                                                    (10, 26, '2024-11-18', false),
                                                                                    (10, 28, '2024-11-18', false),
                                                                                    (10, 32, '2024-11-18', true),
                                                                                    (10, 35, '2024-11-18', false),
                                                                                    (10, 38, '2024-11-18', false),
                                                                                    (10, 42, '2024-11-18', false),
                                                                                    (10, 44, '2024-11-18', false),
                                                                                    (10, 46, '2024-11-18', false),
                                                                                    (10, 50, '2024-11-18', false),
                                                                                    (10, 53, '2024-11-18', false),
                                                                                    (10, 55, '2024-11-18', false),
                                                                                    (10, 58, '2024-11-18', false);

-- =============================================
-- SEGUIMIENTO (Registro de consumo de recetas)
-- =============================================
-- Seguimientos para Laura (paciente premium 5) - Última semana
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot) VALUES
-- Día 2024-12-20
(89, '2024-12-20', 250.0, 8.0, 5.0, 45.0, true, 'reducir nivel de trigliceridos'),  -- Avena con frutas
(92, '2024-12-20', 180.0, 6.0, 12.0, 20.0, true, 'reducir nivel de trigliceridos'), -- Frutos secos
(97, '2024-12-20', 400.0, 35.0, 10.0, 45.0, true, 'reducir nivel de trigliceridos'), -- Pollo a la plancha
(103, '2024-12-20', 320.0, 35.0, 18.0, 10.0, true, 'reducir nivel de trigliceridos'), -- Salmón con espárragos

-- Día 2024-12-21
(90, '2024-12-21', 280.0, 7.0, 15.0, 35.0, true, 'reducir nivel de trigliceridos'),  -- Tostadas con palta
(93, '2024-12-21', 200.0, 4.0, 10.0, 25.0, true, 'reducir nivel de trigliceridos'), -- Manzana con mantequilla
(98, '2024-12-21', 350.0, 40.0, 12.0, 20.0, true, 'reducir nivel de trigliceridos'), -- Pescado al horno
(101, '2024-12-21', 250.0, 20.0, 12.0, 15.0, true, 'reducir nivel de trigliceridos'), -- Tortilla de verduras

-- Día 2024-12-22
(91, '2024-12-22', 220.0, 12.0, 6.0, 40.0, true, 'reducir nivel de trigliceridos'),  -- Yogurt con granola
(94, '2024-12-22', 250.0, 20.0, 8.0, 30.0, true, 'reducir nivel de trigliceridos'), -- Smoothie de proteína
(99, '2024-12-22', 380.0, 18.0, 8.0, 55.0, true, 'reducir nivel de trigliceridos'), -- Lentejas con arroz
(104, '2024-12-22', 280.0, 25.0, 8.0, 30.0, true, 'reducir nivel de trigliceridos'), -- Sopa de pollo

-- Día CURRENT_DATE - 2 (hace 2 días)
(89, CURRENT_DATE - 2, 250.0, 8.0, 5.0, 45.0, true, 'reducir nivel de trigliceridos'),
(95, CURRENT_DATE - 2, 150.0, 5.0, 7.0, 18.0, true, 'reducir nivel de trigliceridos'),
(97, CURRENT_DATE - 2, 400.0, 35.0, 10.0, 45.0, true, 'reducir nivel de trigliceridos'),
(105, CURRENT_DATE - 2, 340.0, 28.0, 10.0, 35.0, true, 'reducir nivel de trigliceridos'),

-- Día CURRENT_DATE - 1 (ayer)
(91, CURRENT_DATE - 1, 220.0, 12.0, 6.0, 40.0, true, 'reducir nivel de trigliceridos'),
(92, CURRENT_DATE - 1, 180.0, 6.0, 12.0, 20.0, true, 'reducir nivel de trigliceridos'),
(98, CURRENT_DATE - 1, 350.0, 40.0, 12.0, 20.0, true, 'reducir nivel de trigliceridos'),
(103, CURRENT_DATE - 1, 320.0, 35.0, 18.0, 10.0, true, 'reducir nivel de trigliceridos'),

-- Día CURRENT_DATE (hoy)
(89, CURRENT_DATE, 250.0, 8.0, 5.0, 45.0, false, 'reducir nivel de trigliceridos'),
(93, CURRENT_DATE, 200.0, 4.0, 10.0, 25.0, false, 'reducir nivel de trigliceridos'),
(97, CURRENT_DATE, 400.0, 35.0, 10.0, 45.0, false, 'reducir nivel de trigliceridos'),

-- Seguimientos para Jorge (paciente premium 6)
(109, '2024-12-18', 220.0, 18.0, 14.0, 8.0, true, 'reducir nivel de trigliceridos'),
(112, '2024-12-18', 180.0, 6.0, 12.0, 20.0, true, 'reducir nivel de trigliceridos'),
(116, '2024-12-18', 400.0, 35.0, 10.0, 45.0, true, 'reducir nivel de trigliceridos'),
(124, '2024-12-18', 280.0, 25.0, 8.0, 30.0, true, 'reducir nivel de trigliceridos'),

(110, '2024-12-19', 290.0, 12.0, 8.0, 40.0, true, 'reducir nivel de trigliceridos'),
(113, '2024-12-19', 250.0, 20.0, 8.0, 30.0, true, 'reducir nivel de trigliceridos'),
(117, '2024-12-19', 420.0, 30.0, 12.0, 50.0, true, 'reducir nivel de trigliceridos'),
(125, '2024-12-19', 340.0, 28.0, 10.0, 35.0, true, 'reducir nivel de trigliceridos'),

(109, CURRENT_DATE - 1, 220.0, 18.0, 14.0, 8.0, true, 'reducir nivel de trigliceridos'),
(114, CURRENT_DATE - 1, 150.0, 5.0, 7.0, 18.0, true, 'reducir nivel de trigliceridos'),
(118, CURRENT_DATE - 1, 380.0, 18.0, 8.0, 55.0, true, 'reducir nivel de trigliceridos'),
(127, CURRENT_DATE - 1, 250.0, 20.0, 12.0, 15.0, true, 'reducir nivel de trigliceridos'),

(111, CURRENT_DATE, 290.0, 12.0, 8.0, 40.0, false, 'reducir nivel de trigliceridos'),
(112, CURRENT_DATE, 180.0, 6.0, 12.0, 20.0, false, 'reducir nivel de trigliceridos'),

-- Seguimientos para Valentina (paciente premium 7)
(129, '2024-12-15', 280.0, 7.0, 15.0, 35.0, true, 'mantener mi salud'),
(135, '2024-12-15', 250.0, 20.0, 8.0, 30.0, true, 'mantener mi salud'),
(139, '2024-12-15', 350.0, 40.0, 12.0, 20.0, true, 'mantener mi salud'),
(145, '2024-12-15', 320.0, 35.0, 18.0, 10.0, true, 'mantener mi salud'),

(130, '2024-12-16', 220.0, 12.0, 6.0, 40.0, true, 'mantener mi salud'),
(136, '2024-12-16', 200.0, 4.0, 10.0, 25.0, true, 'mantener mi salud'),
(141, '2024-12-16', 320.0, 28.0, 14.0, 25.0, true, 'mantener mi salud'),
(147, '2024-12-16', 340.0, 28.0, 10.0, 35.0, true, 'mantener mi salud'),

(129, CURRENT_DATE, 280.0, 7.0, 15.0, 35.0, false, 'mantener mi salud'),
(137, CURRENT_DATE, 150.0, 5.0, 7.0, 18.0, false, 'mantener mi salud'),
(139, CURRENT_DATE, 350.0, 40.0, 12.0, 20.0, false, 'mantener mi salud'),

-- Seguimientos para Ricardo (paciente premium 8)
(149, '2024-12-17', 250.0, 8.0, 5.0, 45.0, true, 'reducir nivel de trigliceridos'),
(155, '2024-12-17', 180.0, 6.0, 12.0, 20.0, true, 'reducir nivel de trigliceridos'),
(161, '2024-12-17', 400.0, 35.0, 10.0, 45.0, true, 'reducir nivel de trigliceridos'),
(167, '2024-12-17', 340.0, 28.0, 10.0, 35.0, true, 'reducir nivel de trigliceridos'),
(150, CURRENT_DATE - 1, 290.0, 12.0, 8.0, 40.0, true, 'reducir nivel de trigliceridos'),
(156, CURRENT_DATE - 1, 150.0, 5.0, 7.0, 18.0, true, 'reducir nivel de trigliceridos'),
(162, CURRENT_DATE - 1, 420.0, 30.0, 12.0, 50.0, true, 'reducir nivel de trigliceridos'),
(168, CURRENT_DATE - 1, 310.0, 28.0, 15.0, 20.0, true, 'reducir nivel de trigliceridos'),
(149, CURRENT_DATE, 250.0, 8.0, 5.0, 45.0, false, 'reducir nivel de trigliceridos'),
(157, CURRENT_DATE, 200.0, 4.0, 10.0, 25.0, false, 'reducir nivel de trigliceridos'),
-- Seguimientos para Camila (paciente premium 9)
(169, '2024-12-19', 220.0, 12.0, 6.0, 40.0, true, 'mantener mi salud'),
(175, '2024-12-19', 250.0, 20.0, 8.0, 30.0, true, 'mantener mi salud'),
(181, '2024-12-19', 350.0, 40.0, 12.0, 20.0, true, 'mantener mi salud'),
(170, CURRENT_DATE - 1, 220.0, 18.0, 14.0, 8.0, true, 'mantener mi salud'),
(177, CURRENT_DATE - 1, 150.0, 5.0, 7.0, 18.0, true, 'mantener mi salud'),
(182, CURRENT_DATE - 1, 320.0, 28.0, 14.0, 25.0, true, 'mantener mi salud'),
(169, CURRENT_DATE, 220.0, 12.0, 6.0, 40.0, false, 'mantener mi salud'),
(176, CURRENT_DATE, 180.0, 6.0, 12.0, 20.0, false, 'mantener mi salud');
-- =============================================
-- FIN DEL SCRIPT
-- =============================================
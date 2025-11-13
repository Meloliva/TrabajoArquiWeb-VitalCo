-- =============================================
-- ROLES
-- =============================================
INSERT INTO roles (tipo) VALUES ('ADMIN');
INSERT INTO roles (tipo) VALUES ('PACIENTE');
INSERT INTO roles (tipo) VALUES ('NUTRICIONISTA');

-- =============================================
-- TURNOS (horarios de trabajo del nutricionista)
-- =============================================

INSERT INTO turno (inicioturno, finturno) VALUES ('08:00:00', '12:00:00');
INSERT INTO turno (inicioturno, finturno) VALUES ('14:00:00', '16:00:00');
-- =============================================
-- HORARIOS DE COMIDA
-- =============================================
INSERT INTO horario (nombre) VALUES ('Desayuno');
INSERT INTO horario (nombre) VALUES ('Snack');
INSERT INTO horario (nombre) VALUES ('Almuerzo');
INSERT INTO horario (nombre) VALUES ('Cena');

-- =============================================
-- PLANES DE SUSCRIPCIÓN
-- =============================================
INSERT INTO plansuscripcion (tipo, precio, beneficios_plan, terminos_condiciones)VALUES ('Plan free', 0.00,'Acceso a recetas básicas, Calculadora nutricional, Seguimiento básico de progreso','Plan gratuito sin compromisos. Funcionalidades limitadas. No incluye asesoría personalizada.');

INSERT INTO plansuscripcion (tipo, precio, beneficios_plan, terminos_condiciones)VALUES ('Plan premium', 95.99,'Acceso ilimitado a todas las recetas, Asesoría personalizada con nutricionista, Planes alimenticios personalizados, Seguimiento detallado, Videoconsultas','Pago mensual. Cancela cuando quieras. Incluye seguimiento nutricional profesional y consultas ilimitadas con nutricionistas certificados.');

-- =============================================
-- PLANES NUTRICIONALES
-- =============================================
INSERT INTO plannutricional (duracion, objetivo) VALUES ('3 meses', 'bajar trigliceridos');
INSERT INTO plannutricional (duracion, objetivo) VALUES ('6 meses', 'bajar trigliceridos');
INSERT INTO plannutricional (duracion, objetivo) VALUES ('12 meses', 'bajar trigliceridos');
-- Planes para mantener tu salud
INSERT INTO plannutricional (duracion, objetivo) VALUES ('3 meses', 'mantener tu salud');
INSERT INTO plannutricional (duracion, objetivo) VALUES ('6 meses', 'mantener tu salud');
INSERT INTO plannutricional (duracion, objetivo) VALUES ('12 meses', 'mantener tu salud');

-- =============================================
-- USUARIOS
-- =============================================
-- Contraseña para todos: "password123" (ya encriptada con BCrypt)

-- Admin
INSERT INTO usuario (dni, "contraseña", nombre, apellido, correo, genero, estado, idrol, foto_perfil)VALUES('12345678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Carlos', 'Rodríguez', 'admin@vitalco.com', 'Masculino', 'Activo', 1, 'https://i.pravatar.cc/150?img=12');

-- Nutricionistas
INSERT INTO usuario (dni, "contraseña", nombre, apellido, correo, genero, estado, idrol, foto_perfil)VALUES('23456789', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','María', 'González','maria.gonzalez@vitalco.com', 'Femenino', 'Activo', 3,'https://i.pravatar.cc/150?img=47'),('34567890', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Roberto', 'Fernández','roberto.fernandez@vitalco.com', 'Masculino', 'Activo', 3,'https://i.pravatar.cc/150?img=33');

-- Pacientes Free
INSERT INTO usuario (dni, "contraseña", nombre, apellido, correo, genero, estado, idrol, foto_perfil)VALUES('45678901', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Ana', 'López', 'ana.lopez@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=5'),('56789012', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Pedro', 'Martínez', 'pedro.martinez@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=14');

-- Pacientes Premium
INSERT INTO usuario (dni, "contraseña", nombre, apellido, correo, genero, estado, idrol, foto_perfil)VALUES('67890123', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Laura', 'Sánchez', 'laura.sanchez@gmail.com', 'Femenino', 'Activo', 2, 'https://i.pravatar.cc/150?img=45'),('78901234', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Jorge', 'Ramírez', 'jorge.ramirez@gmail.com', 'Masculino', 'Activo', 2, 'https://i.pravatar.cc/150?img=52');

-- =============================================
-- NUTRICIONISTAS
-- =============================================
INSERT INTO nutricionista (idusuario, asociaciones, universidad, grado_academico, idturno)VALUES(2, 'Colegio de Nutricionistas del Perú', 'Universidad Peruana de Ciencias Aplicadas', 'Magister en Nutrición Clínica', 1),(3, 'Asociación Peruana de Nutrición', 'Universidad Nacional Mayor de San Marcos', 'Licenciado en Nutrición', 2);

-- =============================================
-- PACIENTES
-- =============================================
-- Pacientes Free
INSERT INTO paciente (idusuario, altura, peso, edad, idplan, trigliceridos, actividad_fisica, idplannutricional)VALUES(4, 1.65, 68.5, 28, 1, 180.00, 'Sedentario', 1),    (5, 1.78, 85.0, 35, 1, 220.00, 'Moderadamente activo', 2);
-- Pacientes Premium
INSERT INTO paciente (idusuario, altura, peso, edad, idplan, trigliceridos, actividad_fisica, idplannutricional)VALUES(6, 1.60, 72.0, 42, 2, 250.00, 'Sedentario', 1),(7, 1.75, 90.0, 38, 2, 195.00, 'Muy activo', 3);

-- =============================================
-- RECETAS
-- =============================================
-- Desayunos
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES('Avena con frutas', 'Avena nutritiva con frutas frescas', 15, 45.0, 250.0, 5.0, 8.0,'1/2 taza de avena, 1 plátano, fresas, arándanos, miel','1. Cocinar la avena con agua. 2. Agregar frutas picadas. 3. Endulzar con miel al gusto.',1.0, 1, 'https://images.unsplash.com/photo-1517673132405-a56a62b18caf'),('Tostadas integrales con palta', 'Pan integral con palta y tomate', 10, 35.0, 280.0, 15.0, 7.0,'2 rebanadas de pan integral, 1/2 palta, tomate, sal, pimienta','1. Tostar el pan. 2. Machacar la palta. 3. Untar en el pan y agregar tomate.',     1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),    ('Yogurt con granola', 'Yogurt natural con granola casera', 5, 40.0, 220.0, 6.0, 12.0,     '1 taza de yogurt griego, 1/4 taza de granola, miel, frutos secos',     '1. Servir el yogurt. 2. Agregar granola. 3. Decorar con miel y frutos secos.',     1.0, 1, 'https://images.unsplash.com/photo-1488477181946-6428a0291777');

-- Snacks
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES    ('Frutos secos mixtos', 'Mix de almendras, nueces y pasas', 0, 20.0, 180.0, 12.0, 6.0,     'Almendras, nueces, pasas, arándanos secos',     '1. Mezclar todos los ingredientes en un bowl.',     1.0, 2, 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32'),    ('Manzana con mantequilla de maní', 'Manzana fresca con mantequilla de maní natural', 5, 25.0, 200.0, 10.0, 4.0,     '1 manzana, 2 cucharadas de mantequilla de maní',     '1. Cortar la manzana en rodajas. 2. Untar con mantequilla de maní.',     1.0, 2, 'https://images.unsplash.com/photo-1619546813926-a78fa6372cd2'),    ('Smoothie de proteína', 'Batido proteico con frutas', 10, 30.0, 250.0, 8.0, 20.0,     '1 scoop proteína, 1 plátano, leche de almendras, espinaca',     '1. Licuar todos los ingredientes. 2. Servir frío.',     1.0, 2, 'https://images.unsplash.com/photo-1505252585461-04db1eb84625');

-- Almuerzos
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES    ('Pollo a la plancha con quinua', 'Pechuga de pollo con quinua y vegetales', 30, 45.0, 400.0, 10.0, 35.0,     '150g pechuga de pollo, 1/2 taza quinua, brócoli, zanahoria',     '1. Cocinar el pollo a la plancha. 2. Cocinar la quinua. 3. Saltear vegetales.',     1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'),    ('Pescado al horno con ensalada', 'Filete de pescado con ensalada fresca', 35, 20.0, 350.0, 12.0, 40.0,     '150g filete de pescado, lechuga, tomate, cebolla, limón',     '1. Hornear el pescado con limón. 2. Preparar ensalada fresca.',     1.0, 3, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2'),    ('Lentejas con arroz integral', 'Guiso de lentejas con arroz', 40, 55.0, 380.0, 8.0, 18.0,     '1 taza de lentejas, 1/2 taza arroz integral, cebolla, tomate, ajo',     '1. Cocinar las lentejas. 2. Cocinar el arroz. 3. Mezclar y servir.',     1.0, 3, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe'),    ('Ensalada de atún', 'Ensalada completa con atún', 15, 25.0, 320.0, 14.0, 28.0,     '1 lata de atún, lechuga, tomate, palta, aceitunas, limón',     '1. Mezclar todos los ingredientes. 2. Aliñar con limón.',     1.0, 3, 'https://images.unsplash.com/photo-1546793665-c74683f339c1');

-- Cenas
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES    ('Tortilla de verduras', 'Tortilla con espinaca y champiñones', 20, 15.0, 250.0, 12.0, 20.0,     '3 huevos, espinaca, champiñones, cebolla, sal, pimienta',     '1. Saltear verduras. 2. Batir huevos y agregar. 3. Cocinar a fuego medio.',     1.0, 4, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),    ('Sopa de pollo con vegetales', 'Sopa casera nutritiva', 30, 30.0, 280.0, 8.0, 25.0,     'Pechuga de pollo, zanahoria, apio, cebolla, fideos integrales',     '1. Cocinar el pollo. 2. Agregar vegetales. 3. Cocinar fideos.',     1.0, 4, 'https://images.unsplash.com/photo-1547592166-23ac45744acd'),    ('Salmón con espárragos', 'Salmón al vapor con espárragos', 25, 10.0, 320.0, 18.0, 35.0,     '150g salmón, espárragos, limón, aceite de oliva, ajo',     '1. Cocinar salmón al vapor. 2. Saltear espárragos. 3. Servir con limón.',     1.0, 4, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288'),    ('Wrap de pollo', 'Wrap integral con pollo y vegetales', 15, 35.0, 340.0, 10.0, 28.0,     '1 tortilla integral, pollo desmenuzado, lechuga, tomate, yogurt',     '1. Calentar tortilla. 2. Rellenar con pollo y vegetales. 3. Enrollar.',     1.0, 4, 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f');

-- Recetas adicionales para variedad
-- Más desayunos
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES('Huevos revueltos con espinaca', 'Huevos con espinaca fresca', 10, 8.0, 220.0, 14.0, 18.0,'3 huevos, espinaca fresca, cebolla, tomate',     '1. Batir los huevos. 2. Saltear espinaca. 3. Agregar huevos y revolver.',     1.0, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8'),    ('Panqueques de avena', 'Panqueques saludables de avena', 20, 40.0, 290.0, 8.0, 12.0,     '1 taza avena molida, 2 huevos, plátano, canela',     '1. Mezclar ingredientes. 2. Cocinar en sartén. 3. Servir con fruta.',     1.0, 1, 'https://images.unsplash.com/photo-1506084868230-bb9d95c24759');

-- Más snacks
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES('Palitos de zanahoria con hummus', 'Snack saludable y nutritivo', 5, 18.0, 150.0, 7.0, 5.0,     'Zanahorias, garbanzos, tahini, limón, ajo',     '1. Cortar zanahorias. 2. Preparar hummus. 3. Servir juntos.',     1.0, 2, 'https://images.unsplash.com/photo-1505253758473-96b7015fcd40');

-- Más almuerzos
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES    ('Arroz chaufa fitness', 'Versión saludable del arroz chaufa', 25, 50.0, 420.0, 12.0, 30.0,     'Arroz integral, pollo, huevo, cebolla china, sillao light',     '1. Cocinar arroz. 2. Saltear ingredientes. 3. Mezclar todo.',     1.0, 3, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b'),    ('Bowl de pavo con camote', 'Bowl nutritivo con pavo molido', 30, 45.0, 390.0, 11.0, 32.0,     'Pavo molido, camote, brócoli, pimiento',     '1. Cocinar pavo. 2. Hornear camote. 3. Saltear vegetales.',     1.0, 3, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c');

-- Más cenas
INSERT INTO receta (nombre, descripcion, tiempo, carbohidratos, calorias, grasas, proteinas, ingredientes, preparacion, cantidad_porcion, idhorario, foto)VALUES    ('Ensalada César con pollo', 'Ensalada clásica versión light', 20, 20.0, 310.0, 15.0, 28.0,     'Lechuga romana, pollo, parmesano, aderezo light',     '1. Cocinar pollo. 2. Preparar ensalada. 3. Mezclar con aderezo.',     1.0, 4, 'https://images.unsplash.com/photo-1546793665-c74683f339c1'),    ('Ceviche de pescado', 'Ceviche peruano tradicional', 30, 15.0, 200.0, 3.0, 25.0,     'Pescado fresco, limón, cebolla, ají, cilantro',     '1. Cortar pescado. 2. Marinar con limón. 3. Agregar cebolla y ají.',     1.0, 4, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2');

-- =============================================
-- PLANES ALIMENTICIOS (se generan automáticamente)
-- =============================================
-- Los planes alimenticios se crean automáticamente al registrar pacientes
-- pero aquí están los datos para los pacientes existentes

INSERT INTO planalimenticio (idpaciente, fechainicio, fechafin, grasas_diaria, carbohidratos_diaria, proteinas_diaria, calorias_diaria)VALUES    (1, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 months', 45.0, 180.0, 90.0, 1650.0),(2, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', 55.0, 220.0, 110.0, 1950.0),(3, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 months', 42.0, 170.0, 85.0, 1580.0),(4, CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months', 60.0, 240.0, 120.0, 2100.0);

-- =============================================
-- PLAN RECETAS
-- =============================================
INSERT INTO planreceta (idplanalimenticio, favorito, fecharegistro)VALUES(1, false, CURRENT_DATE),    (2, false, CURRENT_DATE),    (3, true, CURRENT_DATE),    (4, false, CURRENT_DATE);

-- =============================================
-- PLAN RECETA - RECETA (relaciones)
-- =============================================
-- Plan 1 (Paciente Free - 15 recetas)
INSERT INTO planreceta_receta (idplanreceta, idreceta, fecharegistro)VALUES    (1, 1, CURRENT_DATE), (1, 2, CURRENT_DATE), (1, 3, CURRENT_DATE),    (1, 4, CURRENT_DATE), (1, 5, CURRENT_DATE), (1, 6, CURRENT_DATE),    (1, 7, CURRENT_DATE), (1, 8, CURRENT_DATE), (1, 9, CURRENT_DATE),    (1, 10, CURRENT_DATE), (1, 11, CURRENT_DATE), (1, 12, CURRENT_DATE),    (1, 13, CURRENT_DATE), (1, 14, CURRENT_DATE), (1, 15, CURRENT_DATE);

-- Plan 2 (Paciente Free - 15 recetas)
INSERT INTO planreceta_receta (idplanreceta, idreceta, fecharegistro)VALUES    (2, 1, CURRENT_DATE), (2, 3, CURRENT_DATE), (2, 5, CURRENT_DATE),    (2, 7, CURRENT_DATE), (2, 9, CURRENT_DATE), (2, 11, CURRENT_DATE),    (2, 13, CURRENT_DATE), (2, 15, CURRENT_DATE), (2, 2, CURRENT_DATE),    (2, 4, CURRENT_DATE), (2, 6, CURRENT_DATE), (2, 8, CURRENT_DATE),    (2, 10, CURRENT_DATE), (2, 12, CURRENT_DATE), (2, 14, CURRENT_DATE);

-- Plan 3 (Paciente Premium - todas las recetas)
INSERT INTO planreceta_receta (idplanreceta, idreceta, fecharegistro)VALUES    (3, 1, CURRENT_DATE), (3, 2, CURRENT_DATE), (3, 3, CURRENT_DATE),    (3, 4, CURRENT_DATE), (3, 5, CURRENT_DATE), (3, 6, CURRENT_DATE),    (3, 7, CURRENT_DATE), (3, 8, CURRENT_DATE), (3, 9, CURRENT_DATE),    (3, 10, CURRENT_DATE), (3, 11, CURRENT_DATE), (3, 12, CURRENT_DATE),    (3, 13, CURRENT_DATE), (3, 14, CURRENT_DATE), (3, 15, CURRENT_DATE),    (3, 16, CURRENT_DATE), (3, 17, CURRENT_DATE), (3, 18, CURRENT_DATE),    (3, 19, CURRENT_DATE), (3, 20, CURRENT_DATE);

-- Plan 4 (Paciente Premium - todas las recetas)
INSERT INTO planreceta_receta (idplanreceta, idreceta, fecharegistro)VALUES    (4, 1, CURRENT_DATE), (4, 2, CURRENT_DATE), (4, 3, CURRENT_DATE),    (4, 4, CURRENT_DATE), (4, 5, CURRENT_DATE), (4, 6, CURRENT_DATE),    (4, 7, CURRENT_DATE), (4, 8, CURRENT_DATE), (4, 9, CURRENT_DATE),    (4, 10, CURRENT_DATE), (4, 11, CURRENT_DATE), (4, 12, CURRENT_DATE),    (4, 13, CURRENT_DATE), (4, 14, CURRENT_DATE), (4, 15, CURRENT_DATE),    (4, 16, CURRENT_DATE), (4, 17, CURRENT_DATE), (4, 18, CURRENT_DATE),    (4, 19, CURRENT_DATE), (4, 20, CURRENT_DATE);

-- =============================================
-- CITAS (según horario del nutricionista)
-- =============================================
-- Citas para pacientes premium con nutricionistas
-- Citas con María González (turno mañana: 08:00-16:00)
INSERT INTO cita (idpaciente, idnutricionista, dia, hora, descripcion, link, estado)VALUES(3, 1, CURRENT_DATE, '09:00:00', 'Consulta inicial de evaluación nutricional', 'https://meet.google.com/abc-defg-hij', 'Aceptada'),    (3, 1, CURRENT_DATE + 7, '10:30:00', 'Seguimiento semanal del plan alimenticio', 'https://meet.google.com/klm-nopq-rst', 'Pendiente'),    (4, 1, CURRENT_DATE + 1, '14:00:00', 'Revisión de análisis clínicos', 'https://meet.google.com/uvw-xyza-bcd', 'Pendiente');
-- Citas con Roberto Fernández (turno tarde: 14:00-22:00)
INSERT INTO cita (idpaciente, idnutricionista, dia, hora, descripcion, link, estado)VALUES    (4, 2, CURRENT_DATE, '16:00:00', 'Primera consulta nutricional', 'https://meet.google.com/efg-hijk-lmn', 'Aceptada'),    (3, 2, CURRENT_DATE + 3, '18:30:00', 'Control mensual de peso y medidas', 'https://meet.google.com/opq-rstu-vwx', 'Pendiente'),    (4, 2, CURRENT_DATE + 14, '20:00:00', 'Ajuste de plan por triglicéridos', 'https://meet.google.com/yza-bcde-fgh', 'Pendiente');

-- =============================================
-- SEGUIMIENTO
-- =============================================
-- Seguimientos del día actual para paciente 3
-- Desayuno
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (41, CURRENT_DATE, 250.0, 8.0, 5.0, 45.0, false, 'reducir nivel de trigliceridos');
-- Snack
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (44, CURRENT_DATE, 180.0, 6.0, 12.0, 20.0, false, 'reducir nivel de trigliceridos');
-- Almuerzo
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (47, CURRENT_DATE, 400.0, 35.0, 10.0, 45.0, false, 'reducir nivel de trigliceridos');

-- Seguimientos del día actual para paciente 4
-- Desayuno
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (61, CURRENT_DATE, 290.0, 12.0, 8.0, 40.0, false, 'reducir nivel de trigliceridos');
-- Snack
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (64, CURRENT_DATE, 200.0, 4.0, 10.0, 25.0, false, 'reducir nivel de trigliceridos');
-- Almuerzo
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (67, CURRENT_DATE, 380.0, 18.0, 8.0, 55.0, false, 'reducir nivel de trigliceridos');
-- Cena
INSERT INTO seguimiento (idplanrecetareceta, fecharegistro, calorias, proteinas, grasas, carbohidratos, cumplio, objetivo_snapshot)VALUES    (70, CURRENT_DATE, 250.0, 20.0, 12.0, 15.0, false, 'reducir nivel de trigliceridos');

-- =============================================
-- NOTAS FINALES
-- =============================================
-- Contraseña para todos los usuarios: password123
-- Los pacientes 1 y 2 son Free (solo 15 recetas)
-- Los pacientes 3 y 4 son Premium (todas las recetas + citas)
-- Las citas respetan los horarios de trabajo de cada nutricionista
-- Los seguimientos están registrados para el día actual
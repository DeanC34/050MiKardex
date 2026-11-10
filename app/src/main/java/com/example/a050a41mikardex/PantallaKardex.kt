package com.example.a050a41mikardex

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMiCardex(dao: MateriaDao) {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. SHARED PREFERENCES ESTÁTICO
    val sharedPrefs = contexto.getSharedPreferences("MisDatosCardex", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        sharedPrefs.edit()
            .putString("nombre", "Ángel Soto Pérez")
            .putString("control", "23270050")
            .apply()
    }

    val nombreAlumno = sharedPrefs.getString("nombre", "") ?: ""
    val numControl = sharedPrefs.getString("control", "") ?: ""

    // 2. ESTADOS DE INTERFAZ Y FORMULARIO
    var tabSeleccionada by remember { mutableStateOf(0) }
    val titulosTabs = listOf("Registro", "Mi Historial")

    var materiaNombre by remember { mutableStateOf("") }
    var periodoStr by remember { mutableStateOf("") } // NUEVA VARIABLE
    var semestreStr by remember { mutableStateOf("") }
    var calificacionStr by remember { mutableStateOf("") }
    var tipoOportunidad by remember { mutableStateOf("Normal") }
    var filtroSemestre by remember { mutableStateOf("") }

    val listaMaterias by dao.obtenerTodas().collectAsState(initial = emptyList())

    // 3. ESTRUCTURA VISUAL (Pestañas abajo y bordes seguros)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EstilosCardex.gradienteFondo)
            .systemBarsPadding()
    ) {

        Box(modifier = Modifier.weight(1f)) {
            if (tabSeleccionada == 0) {
                // ==========================================
                // PESTAÑA 1: DATOS Y REGISTRO
                // ==========================================
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    item {
                        Column(modifier = EstilosCardex.modifierTarjeta) {
                            Text("Mis Datos Personales", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nombre: $nombreAlumno", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("No. Control: $numControl", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Column(modifier = EstilosCardex.modifierTarjeta) {
                            Text("Registrar Materia", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = materiaNombre,
                                onValueChange = { materiaNombre = it },
                                label = { Text("Nombre de la materia") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // NUEVO CAMPO DE TEXTO PARA EL PERIODO
                            OutlinedTextField(
                                value = periodoStr,
                                onValueChange = { periodoStr = it },
                                label = { Text("Periodo (Ej. AGOSTO-DICIEMBRE/2026)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = semestreStr, onValueChange = { semestreStr = it }, label = { Text("Semestre (Ej. 1)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                                OutlinedTextField(value = calificacionStr, onValueChange = { calificacionStr = it }, label = { Text("Calificación") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                            }

                            Text("Oportunidad evaluada:", modifier = Modifier.padding(top = 12.dp), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth().selectableGroup(), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = tipoOportunidad == "Normal", onClick = { tipoOportunidad = "Normal" })
                                Text("Nor", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))

                                RadioButton(selected = tipoOportunidad == "Repeticion", onClick = { tipoOportunidad = "Repeticion" })
                                Text("Rep", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))

                                RadioButton(selected = tipoOportunidad == "Especial", onClick = { tipoOportunidad = "Especial" })
                                Text("Esp", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val sem = semestreStr.toIntOrNull() ?: 0
                                    val califRaw = calificacionStr.toDoubleOrNull()

                                    // Validación: Nombre no vacío, semestre válido y calificación presente
                                    if (materiaNombre.trim().isNotBlank() && sem > 0 && califRaw != null) {

                                        // REGLA: Si es menor a 70, se va a 0 automáticamente
                                        val califFinal = if (califRaw < 70.0) 0.0 else califRaw

                                        val n = if (tipoOportunidad == "Normal") califFinal else null
                                        val r = if (tipoOportunidad == "Repeticion") califFinal else null
                                        val e = if (tipoOportunidad == "Especial") califFinal else null

                                        coroutineScope.launch {
                                            dao.insertar(
                                                Materia(
                                                    nombre = materiaNombre.trim(),
                                                    semestre = sem,
                                                    periodo = periodoStr.trim().ifEmpty { "Semestre $sem" }, // CAMBIO APLICADO
                                                    normal = n,
                                                    repeticion = r,
                                                    especial = e
                                                )
                                            )
                                        }
                                        materiaNombre = ""; periodoStr = ""; semestreStr = ""; calificacionStr = ""; tipoOportunidad = "Normal"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00838F))
                            ) { Text("Guardar Calificación") }
                        }
                    }
                }
            } else {
                // ==========================================
                // PESTAÑA 2: CONSULTAS Y KARDEX AGRUPADO
                // ==========================================
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    item {
                        Column(modifier = EstilosCardex.modifierTarjeta) {
                            Text("Consultas Académicas", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                            Spacer(modifier = Modifier.height(8.dp))

                            val validGrades = listaMaterias.mapNotNull { it.normal ?: it.repeticion ?: it.especial }
                            val promedioGeneral = if (validGrades.isNotEmpty()) validGrades.average() else 0.0

                            Text("Promedio General: ${if (promedioGeneral > 0) String.format("%.2f", promedioGeneral) else "0.00"}", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = filtroSemestre, onValueChange = { filtroSemestre = it }, label = { Text("Filtrar por Semestre") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                            val semFiltro = filtroSemestre.toIntOrNull()
                            if (semFiltro != null) {
                                val materiasSemestre = listaMaterias.filter { it.semestre == semFiltro }
                                val gradesSemestre = materiasSemestre.mapNotNull { it.normal ?: it.repeticion ?: it.especial }
                                val promedioSemestre = if (gradesSemestre.isNotEmpty()) gradesSemestre.average() else 0.0
                                Text("Promedio Semestre $semFiltro: ${if (promedioSemestre > 0) String.format("%.2f", promedioSemestre) else "0.00"}", fontSize = 16.sp, color = Color(0xFF006064))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text("Historial Completo", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF006064))
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // --- AQUÍ SUCEDE LA MAGIA DE LA AGRUPACIÓN ---
                    // Agrupamos la lista por número de semestre y la ordenamos de menor a mayor (1, 2, 3...)
                    val materiasAgrupadas = listaMaterias.groupBy { it.semestre }.toSortedMap()

                    materiasAgrupadas.forEach { (semestre, materiasDelSemestre) ->

                        // Extraemos el periodo para mostrarlo como título de la sección
                        val periodoMostrar = materiasDelSemestre.firstOrNull()?.periodo ?: "Semestre $semestre"

                        // Imprimimos el Título del Periodo/Semestre
                        item {
                            Text(
                                text = periodoMostrar,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color(0xFF00838F),
                                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                            )
                        }

                        // Imprimimos las tarjetas de las materias que pertenecen a ese semestre
                        items(materiasDelSemestre) { materia ->
                            Column(modifier = EstilosCardex.modifierTarjeta.padding(bottom = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Materia: ${materia.nombre.ifBlank { "Sin Nombre" }}", fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(onClick = { coroutineScope.launch { dao.eliminar(materia) } }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Materia", tint = Color.Red.copy(alpha = 0.8f))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                val formatCalif = { calif: Double? ->
                                    if (calif == null) "-" else if (calif == 0.0) "NA" else calif.toString()
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Nor: ${formatCalif(materia.normal)}")
                                    Text("Rep: ${formatCalif(materia.repeticion)}")
                                    Text("Esp: ${formatCalif(materia.especial)}")
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- BARRA DE PESTAÑAS (TABS) ABAJO ---
        TabRow(
            selectedTabIndex = tabSeleccionada,
            containerColor = Color.White.copy(alpha = 0.4f),
            contentColor = Color(0xFF006064)
        ) {
            titulosTabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = { tabSeleccionada = index },
                    text = { Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                )
            }
        }
    }
}
package com.example.a050a41mikardex

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "materias_kardex")
data class Materia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val semestre: Int,       // Lo usamos solo internamente para ordenar (1 al 8)
    val periodo: String,     // NUEVO: Lo usaremos en la pantalla visual ("VERANO/2026")
    val normal: Double?,
    val repeticion: Double?,
    val especial: Double?
)

@Dao
interface MateriaDao {
    @Insert
    suspend fun insertar(materia: Materia)

    @Delete
    suspend fun eliminar(materia: Materia)

    @Query("SELECT * FROM materias_kardex ORDER BY semestre ASC")
    fun obtenerTodas(): Flow<List<Materia>>
}

@Database(entities = [Materia::class], version = 1, exportSchema = false)
abstract class CardexDatabase : RoomDatabase() {
    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile
        private var INSTANCE: CardexDatabase? = null

        fun getDatabase(context: Context): CardexDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CardexDatabase::class.java,
                    "cardex_db"
                )
                    // 1. Agregamos el Callback para llenar los datos iniciales
                    .addCallback(CardexDatabaseCallback())
                    .build().also { INSTANCE = it }
            }
        }
    }

    // 2. Definimos el Callback que reacciona cuando se crea la BD
    private class CardexDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Lanzamos una corrutina porque las inserciones no pueden ir en el hilo principal
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    popularBaseDeDatos(database.materiaDao())
                }
            }
        }
    }
}

// 3. Función suspendida que inserta todo tu historial del TecNM
suspend fun popularBaseDeDatos(dao: MateriaDao) {
    val kardexAngel = listOf(
        // Semestre 1 (Enero-Junio 2023)
        Materia(nombre = "CALC. DIFER.", semestre = 1, periodo = "ENERO-JUNIO/2023", normal = 78.0, repeticion = null, especial = null),
        Materia(nombre = "FUND. DE PROG.", semestre = 1, periodo = "ENERO-JUNIO/2023", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "TALL. DE ETICA", semestre = 1, periodo = "ENERO-JUNIO/2023", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "MAT. DISCRETAS", semestre = 1, periodo = "ENERO-JUNIO/2023", normal = 85.0, repeticion = null, especial = null),
        Materia(nombre = "TALL. ADMON.", semestre = 1, periodo = "ENERO-JUNIO/2023", normal = 89.0, repeticion = null, especial = null),
        Materia(nombre = "FUND. INVEST.", semestre = 1, periodo = "ENERO-JUNIO/2023", normal = 94.0, repeticion = null, especial = null),

        // Semestre 2 (Agosto-Diciembre 2023)
        Materia(nombre = "PROG. ORIEN. A OBJ.", semestre = 2, periodo = "AGOSTO-DICIEMBRE/2023", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "CALC. INTEGRAL", semestre = 2, periodo = "AGOSTO-DICIEMBRE/2023", normal = 82.0, repeticion = null, especial = null),
        Materia(nombre = "CONTAB. FINANC.", semestre = 2, periodo = "AGOSTO-DICIEMBRE/2023", normal = 99.0, repeticion = null, especial = null),
        Materia(nombre = "QUIMICA", semestre = 2, periodo = "AGOSTO-DICIEMBRE/2023", normal = 92.0, repeticion = null, especial = null),
        Materia(nombre = "FISICA GRAL.", semestre = 2, periodo = "AGOSTO-DICIEMBRE/2023", normal = 94.0, repeticion = null, especial = null),
        Materia(nombre = "DESARROLLO SUSTENTABLE", semestre = 2, periodo = "AGOSTO-DICIEMBRE/2023", normal = 95.0, repeticion = null, especial = null),

        // Semestre 3 (Enero-Junio 2024)
        Materia(nombre = "EST. DATOS", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 85.0, repeticion = null, especial = null),
        Materia(nombre = "TALLER DE SIST. OPERA", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 80.0, repeticion = null, especial = null),
        Materia(nombre = "INV. DE OPERAC.", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 82.0, repeticion = null, especial = null),
        Materia(nombre = "FUND. BASES. DATOS", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "PROB. Y ESTAD.", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 90.0, repeticion = null, especial = null),
        Materia(nombre = "PRINC. ELEC. APLIC. DIG", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 90.0, repeticion = null, especial = null),
        Materia(nombre = "CALC. VECTORIAL", semestre = 3, periodo = "ENERO-JUNIO/2024", normal = 90.0, repeticion = null, especial = null),

        // Semestre 4 (Agosto-Diciembre 2024)
        Materia(nombre = "TOP. AVANZ. DE PROG.", semestre = 4, periodo = "AGOSTO-DICIEMBRE/2024", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "TALLER. BASES. DATOS", semestre = 4, periodo = "AGOSTO-DICIEMBRE/2024", normal = 97.0, repeticion = null, especial = null),
        Materia(nombre = "SIMULACION", semestre = 4, periodo = "AGOSTO-DICIEMBRE/2024", normal = 93.0, repeticion = null, especial = null),
        Materia(nombre = "ARQ. DE COMPUTADORAS", semestre = 4, periodo = "AGOSTO-DICIEMBRE/2024", normal = 90.0, repeticion = null, especial = null),
        Materia(nombre = "FUND. DE TELEC.", semestre = 4, periodo = "AGOSTO-DICIEMBRE/2024", normal = 95.0, repeticion = null, especial = null),
        Materia(nombre = "METOD. NUMERICOS", semestre = 4, periodo = "AGOSTO-DICIEMBRE/2024", normal = 95.0, repeticion = null, especial = null),

        // Semestre 5 (Enero-Junio 2025)
        Materia(nombre = "REDES DE COMP.", semestre = 5, periodo = "ENERO-JUNIO/2025", normal = 96.0, repeticion = null, especial = null),
        Materia(nombre = "ADMON. BASE D.", semestre = 5, periodo = "ENERO-JUNIO/2025", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "LENG. Y AUTOM. I", semestre = 5, periodo = "ENERO-JUNIO/2025", normal = 96.0, repeticion = null, especial = null),
        Materia(nombre = "PROG. WEB", semestre = 5, periodo = "ENERO-JUNIO/2025", normal = 98.0, repeticion = null, especial = null),
        Materia(nombre = "FUND. DE. ING. SOFT.", semestre = 5, periodo = "ENERO-JUNIO/2025", normal = 83.0, repeticion = null, especial = null),
        Materia(nombre = "SIST. OPERATIVOS", semestre = 5, periodo = "ENERO-JUNIO/2025", normal = 100.0, repeticion = null, especial = null),

        // Semestre 6 (Agosto-Diciembre 2025)
        Materia(nombre = "ACTIVIDADES COMPLEMENTARIAS", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "LENGUAJES Y AUT, II", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "CONM. ENRUT. RED. DATOS", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "CULT. EMPRES.", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "ING. DE SOFTWARE", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "LENGUAJES DE INTERFAZ", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 93.0, repeticion = null, especial = null),
        Materia(nombre = "GRAFICACION", semestre = 6, periodo = "AGOSTO-DICIEMBRE/2025", normal = 100.0, repeticion = null, especial = null),

        // Semestre 7 (Enero-Junio 2026)
        Materia(nombre = "PRO. PROY. Y PLNS. TECNOLOGICOS", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 98.0, repeticion = null, especial = null),
        Materia(nombre = "ADMON. DE REDES", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "E-BUSINESS", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 100.0, repeticion = null, especial = null),
        Materia(nombre = "TALL. INVEST. I", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 92.0, repeticion = null, especial = null),
        Materia(nombre = "PROG. LOG. Y FUNCIONAL", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 99.0, repeticion = null, especial = null),
        Materia(nombre = "SISTEMAS PROGR.", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 95.0, repeticion = null, especial = null),
        Materia(nombre = "GEST. PROY. SOFT.", semestre = 7, periodo = "ENERO-JUNIO/2026", normal = 88.0, repeticion = null, especial = null),

        // Verano 2026 (Lo mapeamos como semestre 8 para mantener el orden numérico en el DAO)
        Materia(nombre = "ECUAC. DIFER.", semestre = 8, periodo = "VERANO/2026", normal = 90.0, repeticion = null, especial = null)
    )

    // Insertamos todas las materias iterando sobre la lista
    kardexAngel.forEach { materia ->
        dao.insertar(materia)
    }
}
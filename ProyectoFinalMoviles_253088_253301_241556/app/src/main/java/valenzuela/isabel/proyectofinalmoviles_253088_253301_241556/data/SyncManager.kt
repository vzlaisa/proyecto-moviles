package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.InscripcionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.UsuarioDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Clase encargada de sincronizar los datos locales de Room
 * con Firebase Firestore en tiempo real. Escucha cambios en las colecciones
 *
 * Cuando Firestore detecta cambios, estos se reflejan automáticamente en la base de datos local.
 */
class SyncManager(
    private val actividadDAO: ActividadDAO,
    private val usuarioDAO: UsuarioDAO,
    private val inscripcionDAO: InscripcionDAO
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val listeners = mutableListOf<ListenerRegistration>()

    /**
     * Scope global del SyncManager.
     *
     * Se utiliza SupervisorJob para evitar que si una
     * sincronización falla, las demás se cancelen.
     */
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    /**
     * Inicia todos los listeners de sincronización.
     *
     * Primero elimina listeners previos para evitar duplicados.
     */
    fun iniciar() {
        listeners.forEach { it.remove() }
        listeners.clear()

        listeners.add(escucharActividades())
        listeners.add(escucharUsuarios())
        listeners.add(escucharInscripciones())
    }

    /**
     * Detiene todos los listeners activos.
     */
    fun detener() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    /**
     * Escucha cambios en la colección "actividades".
     *
     * Cada vez que hay cambios en Firestore:
     * - obtiene los documentos
     * - los convierte a ActividadEntity
     * - los guarda en Room
     */
    private fun escucharActividades(): ListenerRegistration {
        return firestore.collection("actividades")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                scope.launch {
                    for (document in snapshot.documents) {
                        document.id.toIntOrNull() ?: continue
                        val id = document.getLong("id")?.toInt() ?: continue
                        val idCreador = document.getLong("idCreador")?.toInt() ?: continue
                        val idInteres = document.getLong("idInteres")?.toInt() ?: continue

                        try {

                            val actividad = ActividadEntity(
                                id = id,
                                nombre = document.getString("nombre") ?: "Sin nombre",
                                descripcion = document.getString("descripcion") ?: "",
                                fechaHora = parseFecha(document.getString("fechaHora")),
                                fechaLimite = document.getString("fechaLimite")
                                    ?.let { parseFecha(it) },
                                fechaCreacion = document.getString("fechaCreacion")
                                    ?.let { parseFecha(it) }
                                    ?: LocalDateTime.now(),
                                ubicacion = document.getString("ubicacion") ?: "",
                                latitud = document.getDouble("latitud") ?: 0.0,
                                longitud = document.getDouble("longitud") ?: 0.0,
                                maxParticipantes = document.getLong("maxParticipantes")?.toInt()
                                    ?: 10,
                                publica = document.getBoolean("publica") ?: true,
                                recurrente = document.getBoolean("recurrente") ?: false,
                                imageUrl = document.getString("imageUrl"),
                                idCreador = idCreador,
                                idInteres = idInteres
                            )

                            actividadDAO.insertActividad(actividad)
                        } catch (e: SQLiteConstraintException) {
                            Log.w("SYNC", "Actividad $id ignorada — creador $idCreador no existe aún")
                        }
                    }
                }
            }
    }

    /**
     * Escucha cambios en la colección "usuarios".
     *
     * Sincroniza:
     * - datos del usuario
     * - intereses del usuario
     */
    private fun escucharUsuarios(): ListenerRegistration {
        return firestore.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                scope.launch {
                    for (document in snapshot.documents) {
                        val id = document.getLong("id")?.toInt() ?: continue

                        try {
                            usuarioDAO.insertUsuario(
                                UsuarioEntity(
                                    id = id,
                                    nombre = document.getString("nombre") ?: "",
                                    apellidoPaterno = document.getString("apellidoPaterno") ?: "",
                                    apellidoMaterno = document.getString("apellidoMaterno"),
                                    nickname = document.getString("nickname") ?: "",
                                    correo = document.getString("correo") ?: "",
                                    contrasenia = document.getString("contrasenia") ?: "",
                                    genero = Genero.valueOf(document.getString("genero") ?: "OTRO"),
                                    ocupacion = document.getString("ocupacion") ?: "",
                                    fechaNacimiento = LocalDate.parse(document.getString("fechaNacimiento")),
                                    fotoPerfil = document.getString("fotoPerfil"),
                                    huellaActiva = document.getBoolean("huellaActiva") ?: false,
                                    fechaRegistro = LocalDateTime.parse(document.getString("fechaRegistro")),
                                    esPrimerLogin = document.getBoolean("esPrimerLogin") ?: true
                                )
                            )

                            val interesesStr = document.get("intereses") as? List<String> ?: emptyList()
                            usuarioDAO.deleteInteresesByUsuarioId(id)
                            usuarioDAO.insertCrossRefs(interesesStr.map {
                                UsuarioInteresCrossRef(
                                    idUsuario = id,
                                    idInteres = Interes.valueOf(it).ordinal + 1
                                )
                            })
                        } catch (e: Exception) {
                            Log.w("SYNC", "Usuario $id ignorado: ${e.message}")
                        }
                    }
                }
            }
    }

    // Falta probar
    private fun escucharInscripciones(): ListenerRegistration {
        return firestore.collection("inscripciones")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                scope.launch {
                    for (document in snapshot.documents) {
                        val idUsuario = document.getLong("idUsuario")?.toInt() ?: continue
                        val idActividad = document.getLong("idActividad")?.toInt() ?: continue
                        val estadoStr = document.getString("estado") ?: continue
                        val estado = try {
                            EstadoInscripcion.valueOf(estadoStr)
                        } catch (e: Exception) { continue }

                        try {
                            inscripcionDAO.insertar(
                                InscripcionEntity(
                                    idUsuario = idUsuario,
                                    idActividad = idActividad,
                                    estado = estado,
                                    fechaInscripcion = document.getString("fechaInscripcion")?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
                                )
                            )
                        } catch (e: SQLiteConstraintException) {
                            Log.w("SYNC", "Inscripción ignorada (fk faltante)")
                        }
                    }
                }
            }
    }

    private fun parseFecha(str: String?): LocalDateTime {
        return try {
            if (str != null) LocalDateTime.parse(str) else LocalDateTime.now()
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
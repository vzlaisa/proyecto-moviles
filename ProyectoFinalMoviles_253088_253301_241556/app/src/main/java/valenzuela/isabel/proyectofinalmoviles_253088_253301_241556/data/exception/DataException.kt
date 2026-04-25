package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception

sealed class DataException(message: String) : Exception(message)

class ValidationException(message: String) : DataException(message)

class UsuarioYaExisteException :
    DataException("El usuario ya existe")

class DatabaseException(cause: Throwable) :
    DataException("Error en base de datos")
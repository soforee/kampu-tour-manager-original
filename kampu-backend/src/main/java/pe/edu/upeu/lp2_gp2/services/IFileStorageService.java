package pe.edu.upeu.lp2_gp2.services;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    void init(); // Crea la carpeta si no existe
    String store(MultipartFile file, String carpeta); // Guarda el archivo y devuelve la ruta relativa (carpeta/nombre)
    void delete(String ruta); // Borra el archivo (útil al actualizar o eliminar un paquete)
}

package pe.edu.upeu.lp2_gp2.services;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.lp2_gp2.exception.BusinessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");

    @Value("${storage.location}")
    private String storageLocation;

    private Path raiz() {
        return Paths.get(storageLocation).toAbsolutePath().normalize();
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(raiz());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de imágenes");
        }
    }

    @Override
    public String store(MultipartFile file, String carpeta) {
        if (file == null || file.isEmpty()) throw new BusinessException("Selecciona una imagen.");
        String extension = EXTENSIONES.get(file.getContentType());
        if (extension == null) throw new BusinessException("El archivo debe ser una imagen (JPG, PNG o WebP).");
        try {
            // Nombre único (uuid.ext); no se usa el nombre original para evitar espacios y rutas maliciosas
            String ruta = carpeta + "/" + UUID.randomUUID() + extension;
            Path destino = raiz().resolve(ruta).normalize();
            Files.createDirectories(destino.getParent());
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return ruta;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    @Override
    public void delete(String ruta) {
        if (ruta == null || ruta.isBlank()) return;
        Path archivo = raiz().resolve(ruta).normalize();
        if (!archivo.startsWith(raiz())) return; // nunca borrar fuera de la carpeta de imágenes
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            log.warn("No se pudo borrar el archivo: {}", ruta);
        }
    }
}

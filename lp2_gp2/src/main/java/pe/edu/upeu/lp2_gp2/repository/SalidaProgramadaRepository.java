package pe.edu.upeu.lp2_gp2.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.lp2_gp2.entity.SalidaProgramada;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalidaProgramadaRepository extends JpaRepository<SalidaProgramada, Long> {

    boolean existsByPaqueteId(Long paqueteId);
    boolean existsByGuiaId(Long guiaId);
    boolean existsByTransporteId(Long transporteId);

    /** Bloquea la fila (SELECT ... FOR UPDATE) para descontar/liberar cupos sin carreras. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SalidaProgramada s where s.salidaId = :id")
    Optional<SalidaProgramada> findByIdParaActualizar(@Param("id") Long id);

    /** Salidas activas (PROGRAMADA/CONFIRMADA) del guía que se cruzan con el rango, excluyendo una salida. */
    @Query("""
            select count(s) from SalidaProgramada s
            where s.guiaId = :guiaId and s.estado in ('PROGRAMADA', 'CONFIRMADA')
              and s.fechaInicio <= :fin and s.fechaFin >= :inicio
              and (:excluirId is null or s.salidaId <> :excluirId)
            """)
    long contarCrucesGuia(@Param("guiaId") Long guiaId, @Param("inicio") LocalDate inicio,
                          @Param("fin") LocalDate fin, @Param("excluirId") Long excluirId);

    @Query("""
            select count(s) from SalidaProgramada s
            where s.transporteId = :transporteId and s.estado in ('PROGRAMADA', 'CONFIRMADA')
              and s.fechaInicio <= :fin and s.fechaFin >= :inicio
              and (:excluirId is null or s.salidaId <> :excluirId)
            """)
    long contarCrucesTransporte(@Param("transporteId") Long transporteId, @Param("inicio") LocalDate inicio,
                                @Param("fin") LocalDate fin, @Param("excluirId") Long excluirId);
}

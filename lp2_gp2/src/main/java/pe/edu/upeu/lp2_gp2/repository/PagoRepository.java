package pe.edu.upeu.lp2_gp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.lp2_gp2.entity.Pago;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReservaIdOrderByPagoId(Long reservaId);
    List<Pago> findByReservaIdAndEstadoTransaccion(Long reservaId, String estadoTransaccion);
}

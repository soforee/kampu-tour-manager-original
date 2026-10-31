package pe.edu.upeu.lp2_gp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.lp2_gp2.entity.PasajeroReserva;

import java.util.List;

@Repository
public interface PasajeroReservaRepository extends JpaRepository<PasajeroReserva, Long> {
    List<PasajeroReserva> findByReservaIdOrderByPasajeroId(Long reservaId);
}

package pe.edu.upeu.lp2_gp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.lp2_gp2.entity.Transporte;

import java.util.Optional;

@Repository
public interface TransporteRepository extends JpaRepository<Transporte, Long> {
    Optional<Transporte> findByPlacaIgnoreCase(String placa);
}

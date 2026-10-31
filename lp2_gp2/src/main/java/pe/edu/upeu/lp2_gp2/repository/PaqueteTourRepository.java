package pe.edu.upeu.lp2_gp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.lp2_gp2.entity.PaqueteTour;

@Repository
public interface PaqueteTourRepository extends JpaRepository<PaqueteTour, Long> {
}

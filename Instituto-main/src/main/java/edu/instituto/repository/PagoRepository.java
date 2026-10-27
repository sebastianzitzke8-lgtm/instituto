package edu.instituto.repository;

import edu.instituto.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByCuota_IdOrderByFechaPagoAscIdAsc(Integer idCuota);
    List<Pago> findByCuota_Matricula_Alumno_IdOrderByFechaPagoDescIdDesc(Integer idAlumno);
    boolean existsByReferencia(String referencia);
    boolean existsByCuota_Id(Integer idCuota);

    @Query("select coalesce(sum(p.monto), 0) from Pago p where p.cuota.id = :idCuota")
    BigDecimal totalPagadoPorCuota(@Param("idCuota") Integer idCuota);
}

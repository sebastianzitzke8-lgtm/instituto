package edu.instituto.service;

import edu.instituto.model.Cuota;
import edu.instituto.model.Pago;
import edu.instituto.repository.CuotaRepository;
import edu.instituto.repository.PagoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PagoService {
    private static final Set<String> MEDIOS = Set.of("EFECTIVO", "TRANSFERENCIA", "TARJETA", "OTRO");
    private final PagoRepository pagos;
    private final CuotaRepository cuotas;

    public PagoService(PagoRepository pagos, CuotaRepository cuotas) {
        this.pagos = pagos;
        this.cuotas = cuotas;
    }


    // Devuelve todos los pagos registrados.
    @Transactional(readOnly = true)
    public List<Pago> listar() {
        return pagos.findAll();
    }


    @Transactional(readOnly = true)
    public Pago buscar(Integer id) {
        validarId(id);
        return pagos.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Pago> porCuota(Integer idCuota) {
        validarId(idCuota);
        if (!cuotas.existsById(idCuota)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota no encontrada");
        return pagos.findByCuota_IdOrderByFechaPagoAscIdAsc(idCuota);
    }

    @Transactional(readOnly = true)
    public List<Pago> porAlumno(Integer idAlumno) {
        validarId(idAlumno);
        return pagos.findByCuota_Matricula_Alumno_IdOrderByFechaPagoDescIdDesc(idAlumno);
    }

    @Transactional
    public Pago registrar(LocalDate fecha, BigDecimal monto, String medio, String referencia, Integer idCuota) {
        validarId(idCuota);
        if (fecha == null || fecha.isAfter(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha es obligatoria y no puede ser futura");
        if (monto == null || monto.signum() <= 0 || monto.scale() > 2 || monto.precision() - monto.scale() > 10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto positivo con hasta 2 decimales y 10 enteros");
        if (medio == null || !MEDIOS.contains(medio.trim().toUpperCase(Locale.ROOT)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medio: EFECTIVO, TRANSFERENCIA, TARJETA u OTRO");
        String referenciaLimpia = referencia == null || referencia.isBlank() ? null : referencia.trim();
        if (referenciaLimpia != null && referenciaLimpia.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referencia demasiado larga");

        // Bloquea la cuota durante el cálculo del saldo y el INSERT.
        Cuota cuota = cuotas.buscarConBloqueo(idCuota)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota no encontrada"));
        if ("ANULADA".equalsIgnoreCase(cuota.getEstado()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede pagar una cuota anulada");
        if ("CANCELADA".equalsIgnoreCase(cuota.getMatricula().getEstado()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La matrícula está cancelada");
        if (referenciaLimpia != null && pagos.existsByReferencia(referenciaLimpia))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Referencia de pago ya registrada");

        BigDecimal abonado = pagos.totalPagadoPorCuota(idCuota);
        BigDecimal saldo = cuota.getImporte().subtract(abonado);
        if (monto.compareTo(saldo) > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El pago supera el saldo de la cuota: " + saldo);

        Pago pago = new Pago();
        pago.setCuota(cuota);
        pago.setFechaPago(fecha);
        pago.setMonto(monto);
        pago.setMedioPago(medio.trim().toUpperCase(Locale.ROOT));
        pago.setReferencia(referenciaLimpia);
        Pago guardado = pagos.saveAndFlush(pago);

        // Una cuota parcialmente abonada mantiene PENDIENTE/VENCIDA.
        if (monto.compareTo(saldo) == 0) {
            cuota.setEstado("PAGADA");
            cuotas.save(cuota);
        }
        return guardado;
    }

    private void validarId(Integer id) {
        if (id == null || id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido");
    }
}

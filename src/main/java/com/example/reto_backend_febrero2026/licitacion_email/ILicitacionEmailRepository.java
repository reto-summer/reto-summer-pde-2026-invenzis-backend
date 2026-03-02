package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ILicitacionEmailRepository extends JpaRepository<LicitacionEmail, LicitacionEmail.LicitacionEmailId> {

    List<LicitacionEmail> findByIdLicitacionIn(List<Integer> idLicitaciones);

    @Modifying
    @Query("""
        UPDATE LicitacionEmail le 
        SET le.enviado = true, le.fechaEnvio = CURRENT_TIMESTAMP 
        WHERE le.idLicitacion IN :licitacionIds 
        AND le.direccionEmail IN :emails
    """)
    int updateEnviado(@Param("licitacionIds") List<Integer> licitacionIds, @Param("emails") Set<String> emails);

    // 2. Verificación de duplicados

    @Query("""
    SELECT CONCAT(le.id.idLicitacion, '_', le.id.email) 
    FROM LicitacionEmail le 
    WHERE le.id.idLicitacion IN :ids 
    AND le.id.email IN :emails
""")
    Set<String> findByLicitacionesAndEmails(@Param("ids") List<Integer> ids, @Param("emails") Set<String> emails);

    List<LicitacionEmail> findByEnviadoFalse();
}

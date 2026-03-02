package com.example.reto_backend_febrero2026.channel.licitacion_email;

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
    int updateEnviado(
            @Param("licitacionIds") List<Integer> licitacionIds,
            @Param("emails") Set<String> emails
    );


    @Query("""
    SELECT CONCAT(le.idLicitacion, '_', le.direccionEmail)
    FROM LicitacionEmail le
    WHERE le.idLicitacion IN :ids
    AND le.direccionEmail IN :emails
""")
    Set<String> findByLicitacionesAndEmails(
            @Param("ids") List<Integer> ids,
            @Param("emails") Set<String> emails
    );
    List<LicitacionEmail> findByEnviadoFalse();


}

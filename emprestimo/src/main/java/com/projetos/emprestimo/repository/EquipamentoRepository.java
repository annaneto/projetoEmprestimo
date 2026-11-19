package com.projetos.emprestimo.repository;
import com.projetos.emprestimo.model.Equipamento;
import com.projetos.emprestimo.model.TipoEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    boolean existsByPatrimonio(String patrimonio);



    List<Equipamento> findByTipo(TipoEquipamento tipo);

    List<Equipamento> findByDisponivel(TipoEquipamento tipo);

    List<Equipamento> findByTipoAndDisponivel(TipoEquipamento tipo, boolean disponivel);

}

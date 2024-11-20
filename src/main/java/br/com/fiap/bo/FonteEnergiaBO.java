package br.com.fiap.bo;

import br.com.fiap.dao.FonteEnergiaDAO;
import br.com.fiap.to.FonteEnergiaTO;
import br.com.fiap.exception.FonteEnergiaNotFoundException;
import br.com.fiap.exception.InvalidFonteEnergiaException;

import java.util.ArrayList;

public class FonteEnergiaBO {
    private final FonteEnergiaDAO fonteEnergiaDAO;

    public FonteEnergiaBO() {
        this.fonteEnergiaDAO = new FonteEnergiaDAO();
    }
    public double calcularCapacidadeTotalPorMicrogrid(Long idMicrogrid) {
        return fonteEnergiaDAO.calcularCapacidadeTotalPorMicrogrid(idMicrogrid);
    }

    /**
     * Busca todas as fontes de energia.
     *
     * @return Lista de todas as fontes de energia.
     */
    public ArrayList<FonteEnergiaTO> findAll() {
        ArrayList<FonteEnergiaTO> fontes = fonteEnergiaDAO.findAll();
        if (fontes.isEmpty()) {
            throw new FonteEnergiaNotFoundException("Nenhuma fonte de energia encontrada.");
        }
        return fontes;
    }

    /**
     * Busca uma fonte de energia pelo ID.
     *
     * @param idFonte ID da fonte a ser buscada.
     * @return Fonte de energia correspondente ao ID.
     */
    public FonteEnergiaTO findById(Long idFonte) {
        if (idFonte == null) {
            throw new InvalidFonteEnergiaException("ID da fonte não pode ser nulo.");
        }
        FonteEnergiaTO fonte = fonteEnergiaDAO.findById(idFonte);
        if (fonte == null) {
            throw new FonteEnergiaNotFoundException("Fonte de energia não encontrada para o ID informado.");
        }
        return fonte;
    }

    /**
     * Salva uma nova fonte de energia.
     *
     * @param fonte Fonte de energia a ser salva.
     * @return Fonte de energia salva.
     */
    public FonteEnergiaTO save(FonteEnergiaTO fonte) {
        validateFonteEnergia(fonte);
        FonteEnergiaTO savedFonte = fonteEnergiaDAO.save(fonte);
        if (savedFonte == null) {
            throw new InvalidFonteEnergiaException("Erro ao salvar fonte de energia. Tente novamente.");
        }
        return savedFonte;
    }

    /**
     * Exclui uma fonte de energia pelo ID.
     *
     * @param idFonte ID da fonte a ser excluída.
     * @return true se a exclusão for bem-sucedida, false caso contrário.
     */
    public boolean delete(Long idFonte) {
        if (idFonte == null) {
            throw new InvalidFonteEnergiaException("ID da fonte é obrigatório.");
        }
        boolean isDeleted = fonteEnergiaDAO.delete(idFonte);
        if (!isDeleted) {
            throw new FonteEnergiaNotFoundException("Fonte de energia não encontrada para exclusão.");
        }
        return isDeleted;
    }

    /**
     * Atualiza uma fonte de energia.
     *
     * @param fonte Fonte de energia a ser atualizada.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public boolean update(FonteEnergiaTO fonte) {
        validateFonteEnergia(fonte);
        boolean isUpdated = fonteEnergiaDAO.update(fonte);
        if (!isUpdated) {
            throw new FonteEnergiaNotFoundException("Erro ao atualizar: Fonte de energia não encontrada.");
        }
        return isUpdated;
    }

    /**
     * Valida os dados de uma fonte de energia antes de salvá-la ou atualizá-la.
     *
     * @param fonte Fonte de energia a ser validada.
     */
    private void validateFonteEnergia(FonteEnergiaTO fonte) {
        if (fonte.getIdMicrogrid() == null) {
            throw new InvalidFonteEnergiaException("ID da microgrid é obrigatório.");
        }
        if (fonte.getTipo() == null || fonte.getTipo().trim().isEmpty()) {
            throw new InvalidFonteEnergiaException("O tipo da fonte é obrigatório.");
        }
        if (fonte.getCapacidadeInstalada() <= 0) {
            throw new InvalidFonteEnergiaException("A capacidade instalada deve ser maior que zero.");
        }
        if (fonte.getStatus() == null || fonte.getStatus().trim().isEmpty()) {
            throw new InvalidFonteEnergiaException("O status da fonte é obrigatório.");
        }
    }

    /**
     * Verifica se todas as fontes de uma microgrid estão dentro do limite de capacidade instalada.
     *
     * @param idMicrogrid ID da microgrid.
     * @param limite      Limite de capacidade instalada.
     * @return true se todas as fontes estiverem dentro do limite, false caso contrário.
     */
    public boolean verificarFontesDentroDoLimite(Long idMicrogrid, double limite) {
        ArrayList<FonteEnergiaTO> fontes = fonteEnergiaDAO.findByMicrogrid(idMicrogrid);
        if (fontes.isEmpty()) {
            throw new FonteEnergiaNotFoundException("Nenhuma fonte de energia encontrada para a microgrid informada.");
        }
        return fontes.stream().allMatch(fonte -> fonte.getCapacidadeInstalada() <= limite);
    }

    /**
     * Calcula a capacidade total instalada de todas as fontes em uma microgrid.
     *
     * @param idMicrogrid ID da microgrid.
     * @return Capacidade total instalada.
     */
    public double calcularCapacidadeTotal(Long idMicrogrid) {
        ArrayList<FonteEnergiaTO> fontes = fonteEnergiaDAO.findByMicrogrid(idMicrogrid);
        if (fontes.isEmpty()) {
            throw new FonteEnergiaNotFoundException("Nenhuma fonte de energia encontrada para a microgrid informada.");
        }
        return fontes.stream().mapToDouble(FonteEnergiaTO::getCapacidadeInstalada).sum();
    }

    public ArrayList<FonteEnergiaTO> findByMicrogrid(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidFonteEnergiaException("ID da microgrid é obrigatório.");
        }
        ArrayList<FonteEnergiaTO> fontes = fonteEnergiaDAO.findByMicrogrid(idMicrogrid);
        if (fontes.isEmpty()) {
            throw new FonteEnergiaNotFoundException("Nenhuma fonte de energia encontrada para a microgrid informada.");
        }
        return fontes;
    }

}

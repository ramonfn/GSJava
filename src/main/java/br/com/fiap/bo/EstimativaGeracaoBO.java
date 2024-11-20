package br.com.fiap.bo;

import br.com.fiap.dao.EstimativaGeracaoDAO;
import br.com.fiap.to.EstimativaGeracaoTO;
import br.com.fiap.exception.EstimativaGeracaoNotFoundException;
import br.com.fiap.exception.InvalidEstimativaGeracaoException;

import java.util.ArrayList;

public class EstimativaGeracaoBO {
    private final EstimativaGeracaoDAO estimativaGeracaoDAO;

    public EstimativaGeracaoBO() {
        this.estimativaGeracaoDAO = new EstimativaGeracaoDAO();
    }
    public ArrayList<EstimativaGeracaoTO> findAll() {
        return estimativaGeracaoDAO.findAll();
    }

    // Adicionado método findById
    public EstimativaGeracaoTO findById(Long idEstimativa) {
        if (idEstimativa == null) {
            throw new InvalidEstimativaGeracaoException("ID da estimativa é obrigatório.");
        }
        EstimativaGeracaoTO estimativa = estimativaGeracaoDAO.findById(idEstimativa);
        if (estimativa == null) {
            throw new EstimativaGeracaoNotFoundException("Estimativa não encontrada para o ID informado.");
        }
        return estimativa;
    }


    // Salva uma nova estimativa após validação
    public EstimativaGeracaoTO save(EstimativaGeracaoTO estimativa) {
        validateEstimativa(estimativa);
        EstimativaGeracaoTO savedEstimativa = estimativaGeracaoDAO.save(estimativa);
        if (savedEstimativa == null) {
            throw new InvalidEstimativaGeracaoException("Erro ao salvar estimativa. Tente novamente.");
        }
        return savedEstimativa;
    }

    // Encontra estimativas associadas a uma microgrid específica
    public ArrayList<EstimativaGeracaoTO> findByMicrogrid(Long idMicrogrid) {
        validateMicrogridId(idMicrogrid);

        ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>(estimativaGeracaoDAO.findByMicrogrid(idMicrogrid));
        if (estimativas.isEmpty()) {
            throw new EstimativaGeracaoNotFoundException(
                    "Nenhuma estimativa encontrada para a microgrid informada."
            );
        }
        return estimativas;
    }

    // Exclui uma estimativa pelo ID
    public boolean delete(Long idEstimativa) {
        if (idEstimativa == null) {
            throw new InvalidEstimativaGeracaoException("ID da estimativa é obrigatório.");
        }

        boolean isDeleted = estimativaGeracaoDAO.delete(idEstimativa);
        if (!isDeleted) {
            throw new EstimativaGeracaoNotFoundException("Estimativa não encontrada para exclusão.");
        }
        return isDeleted;
    }

    // Validações auxiliares
    private void validateEstimativa(EstimativaGeracaoTO estimativa) {
        if (estimativa.getIdMicrogrid() == null) {
            throw new InvalidEstimativaGeracaoException("ID da microgrid é obrigatório.");
        }
        if (estimativa.getAno() == null || estimativa.getAno() < 1000 || estimativa.getAno() > 9999) {
            throw new InvalidEstimativaGeracaoException("Ano inválido. Deve conter exatamente 4 dígitos.");
        }
        if (estimativa.getMes() == null || estimativa.getMes() < 1 || estimativa.getMes() > 12) {
            throw new InvalidEstimativaGeracaoException("Mês inválido. Deve estar entre 1 e 12.");
        }
        if (estimativa.getWattsEstimados() <= 0) {
            throw new InvalidEstimativaGeracaoException("Watts estimados devem ser maiores que zero.");
        }
    }

    private void validateMicrogridId(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidEstimativaGeracaoException("ID da microgrid é obrigatório.");
        }
    }

    public double calcularMediaDeEstimativas(ArrayList<EstimativaGeracaoTO> estimativas) {
        EstimativaGeracaoTO util = new EstimativaGeracaoTO();
        return util.calcularMediaWattsEstimados(estimativas);
    }
}

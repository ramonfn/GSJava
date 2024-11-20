package br.com.fiap.bo;

import br.com.fiap.dao.EstimativaGeracaoDAO;
import br.com.fiap.to.EstimativaGeracaoTO;
import br.com.fiap.exception.EstimativaGeracaoNotFoundException;
import br.com.fiap.exception.InvalidEstimativaGeracaoException;

import java.util.ArrayList;
import java.util.OptionalDouble;

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

    // Verifica se a média de watts excede o limite informado
    public boolean verificarSeMediaExcede(double limite) {
        ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>(estimativaGeracaoDAO.findAll());
        if (estimativas.isEmpty()) {
            throw new EstimativaGeracaoNotFoundException("Nenhuma estimativa encontrada.");
        }

        OptionalDouble media = estimativas.stream()
                .mapToDouble(EstimativaGeracaoTO::getWattsEstimados)
                .average();

        return media.isPresent() && media.getAsDouble() > limite;
    }

    // Calcula a média de watts estimados para uma microgrid específica
    public double calcularMediaWattsPorMicrogrid(Long idMicrogrid) {
        validateMicrogridId(idMicrogrid);

        ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>(estimativaGeracaoDAO.findByMicrogrid(idMicrogrid));
        return estimativas.stream()
                .mapToDouble(EstimativaGeracaoTO::getWattsEstimados)
                .average()
                .orElseThrow(() -> new EstimativaGeracaoNotFoundException(
                        "Nenhuma estimativa encontrada para a microgrid informada."
                ));
    }

    // Projeta a geração anual total de watts para uma microgrid específica
    public double projetarGeracaoAnual(Long idMicrogrid) {
        validateMicrogridId(idMicrogrid);

        ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>(estimativaGeracaoDAO.findByMicrogrid(idMicrogrid));
        return estimativas.stream()
                .mapToDouble(EstimativaGeracaoTO::getWattsEstimados)
                .sum();
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

    public double calcularMediaWattsEstimados(ArrayList<EstimativaGeracaoTO> estimativas) {
        if (estimativas == null || estimativas.isEmpty()) {
            throw new IllegalArgumentException("A lista de estimativas não pode estar vazia.");
        }
        return estimativas.stream()
                .mapToDouble(EstimativaGeracaoTO::getWattsEstimados)
                .average()
                .orElse(0.0);
    }

    public ArrayList<EstimativaGeracaoTO> buscarEstimativasPorAno(ArrayList<EstimativaGeracaoTO> estimativas, int ano) {
        if (ano < 1000 || ano > 9999) {
            throw new IllegalArgumentException("O ano deve conter exatamente 4 dígitos.");
        }
        return EstimativaGeracaoTO.filtrarPorAno(estimativas, ano);
    }
    public double calcularMediaDeEstimativas(ArrayList<EstimativaGeracaoTO> estimativas) {
        EstimativaGeracaoTO util = new EstimativaGeracaoTO();
        return util.calcularMediaWattsEstimados(estimativas);
    }
    public void gerarRelatorioMedia(ArrayList<EstimativaGeracaoTO> estimativas) {
        double media = calcularMediaDeEstimativas(estimativas);
        System.out.println("A média das estimativas é: " + media);
    }



}

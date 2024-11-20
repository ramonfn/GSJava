package br.com.fiap.bo;

import br.com.fiap.dao.GeracaoConsumoMensalDAO;
import br.com.fiap.dao.EstimativaGeracaoDAO;
import br.com.fiap.to.GeracaoConsumoMensalTO;
import br.com.fiap.to.EstimativaGeracaoTO;
import br.com.fiap.exception.GeracaoConsumoMensalNotFoundException;
import br.com.fiap.exception.InvalidGeracaoConsumoMensalException;

import java.util.ArrayList;

public class GeracaoConsumoMensalBO {
    private final GeracaoConsumoMensalDAO geracaoConsumoMensalDAO;
    private final EstimativaGeracaoDAO estimativaGeracaoDAO;

    public GeracaoConsumoMensalBO() {
        this.geracaoConsumoMensalDAO = new GeracaoConsumoMensalDAO();
        this.estimativaGeracaoDAO = new EstimativaGeracaoDAO();
    }

    public ArrayList<GeracaoConsumoMensalTO> findAll() {
        ArrayList<GeracaoConsumoMensalTO> registros = geracaoConsumoMensalDAO.findAll();
        if (registros.isEmpty()) {
            throw new GeracaoConsumoMensalNotFoundException("Nenhum registro encontrado.");
        }
        return registros;
    }
    public GeracaoConsumoMensalTO findById(Long idRegistro) {
        if (idRegistro == null) {
            throw new InvalidGeracaoConsumoMensalException("ID do registro é obrigatório.");
        }

        // Log para depuração
        System.out.println("Buscando registro com ID: " + idRegistro);

        GeracaoConsumoMensalTO registro = geracaoConsumoMensalDAO.findById(idRegistro);

        if (registro == null) {
            System.out.println("Nenhum registro encontrado para o ID: " + idRegistro);
            throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para o ID fornecido: " + idRegistro);
        }

        // Log para sucesso
        System.out.println("Registro encontrado: " + registro);
        return registro;
    }



    public GeracaoConsumoMensalTO save(GeracaoConsumoMensalTO registro) {
        validateRegistro(registro);
        GeracaoConsumoMensalTO savedRegistro = geracaoConsumoMensalDAO.save(registro);
        calcularEGuardarEstimativa(savedRegistro);
        return savedRegistro;
    }

    public boolean delete(Long idRegistro) {
        if (idRegistro == null) {
            throw new InvalidGeracaoConsumoMensalException("ID do registro não pode ser nulo.");
        }

        // Busca o registro para obter informações necessárias
        GeracaoConsumoMensalTO registro = geracaoConsumoMensalDAO.findById(idRegistro);
        if (registro == null) {
            throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para exclusão.");
        }

        // Exclui o registro em GeracaoConsumoMensal
        boolean isDeleted = geracaoConsumoMensalDAO.delete(idRegistro);

        if (isDeleted) {
            // Exclui as estimativas relacionadas
            estimativaGeracaoDAO.deleteByMicrogridAnoMes(
                    registro.getIdMicrogrid(),
                    registro.getAno(),
                    registro.getMes() + 1 // Ajuste para estimativas relacionadas ao próximo mês
            );
        }

        return isDeleted;
    }


    public boolean update(GeracaoConsumoMensalTO registro) {
        validateRegistro(registro);
        boolean isUpdated = geracaoConsumoMensalDAO.update(registro);
        if (isUpdated) {
            recalcularEAtualizarEstimativa(registro);
        }
        return isUpdated;
    }

    private void recalcularEAtualizarEstimativa(GeracaoConsumoMensalTO registro) {
        double novaEstimativaWatts = registro.getWattsGerados() * 1.1;
        EstimativaGeracaoTO estimativaAtualizada = new EstimativaGeracaoTO(
                registro.getIdMicrogrid(),
                registro.getAno(),
                registro.getMes() + 1,
                novaEstimativaWatts
        );
        estimativaGeracaoDAO.update(estimativaAtualizada);
    }

    private void calcularEGuardarEstimativa(GeracaoConsumoMensalTO registro) {
        double estimativaWatts = registro.getWattsGerados() * 1.1;
        EstimativaGeracaoTO estimativa = new EstimativaGeracaoTO(
                registro.getIdMicrogrid(),
                registro.getAno(),
                registro.getMes() + 1,
                estimativaWatts
        );
        estimativaGeracaoDAO.save(estimativa);
    }

    private void validateRegistro(GeracaoConsumoMensalTO registro) {
        if (registro.getIdMicrogrid() == null) {
            throw new InvalidGeracaoConsumoMensalException("ID da microgrid é obrigatório.");
        }
        if (registro.getAno() == null || String.valueOf(registro.getAno()).length() != 4) {
            throw new InvalidGeracaoConsumoMensalException("Ano inválido. Deve conter exatamente 4 dígitos.");
        }
        if (registro.getMes() == null || registro.getMes() < 1 || registro.getMes() > 12) {
            throw new InvalidGeracaoConsumoMensalException("Mês inválido. Deve estar entre 1 e 12.");
        }
        if (registro.getWattsGerados() <= 0) {
            throw new InvalidGeracaoConsumoMensalException("Watts gerados deve ser maior que zero.");
        }
        if (registro.getWattsConsumidos() <= 0) {
            throw new InvalidGeracaoConsumoMensalException("Watts consumidos deve ser maior que zero.");
        }
    }


}

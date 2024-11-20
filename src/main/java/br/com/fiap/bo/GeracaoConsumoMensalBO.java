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
        GeracaoConsumoMensalTO registro = geracaoConsumoMensalDAO.findById(idRegistro);
        if (registro == null) {
            throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para o ID fornecido: " + idRegistro);
        }
        return registro;
    }


    public GeracaoConsumoMensalTO save(GeracaoConsumoMensalTO registro) {
        validateRegistro(registro);
        GeracaoConsumoMensalTO savedRegistro = geracaoConsumoMensalDAO.save(registro);
        calcularEGuardarEstimativa(savedRegistro);
        gerarRelatoriosAutomatizados(savedRegistro.getIdMicrogrid());
        return savedRegistro;
    }

    public boolean delete(Long idRegistro) {
        if (idRegistro == null) {
            throw new InvalidGeracaoConsumoMensalException("ID do registro não pode ser nulo.");
        }
        GeracaoConsumoMensalTO registro = geracaoConsumoMensalDAO.findById(idRegistro);
        if (registro == null) {
            throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para exclusão.");
        }

        // Calcular mês e ano ajustados
        int mesAjustado = registro.getMes() + 1;
        int anoAjustado = registro.getAno();
        if (mesAjustado > 12) {
            mesAjustado = 1;
            anoAjustado++;
        }

        boolean isDeleted = geracaoConsumoMensalDAO.delete(idRegistro);
        if (isDeleted) {
            estimativaGeracaoDAO.deleteByMicrogridAnoMes(
                    registro.getIdMicrogrid(),
                    anoAjustado,
                    mesAjustado
            );
        }
        gerarRelatoriosAutomatizados(registro.getIdMicrogrid());
        return isDeleted;
    }


    public boolean update(GeracaoConsumoMensalTO registro) {
        validateRegistro(registro);
        boolean isUpdated = geracaoConsumoMensalDAO.update(registro);
        if (isUpdated) {
            recalcularEAtualizarEstimativa(registro);
            gerarRelatoriosAutomatizados(registro.getIdMicrogrid());
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

    public double calcularMediaDiferencaWatts(Long idMicrogrid) {
        ArrayList<GeracaoConsumoMensalTO> registros = geracaoConsumoMensalDAO.findByMicrogrid(idMicrogrid);
        return registros.stream()
                .mapToDouble(GeracaoConsumoMensalTO::calcularDiferencaWatts)
                .average()
                .orElse(0.0);
    }

    public double calcularProporcaoGeracaoConsumo(Long idMicrogrid) {
        ArrayList<GeracaoConsumoMensalTO> registros = geracaoConsumoMensalDAO.findByMicrogrid(idMicrogrid);
        double totalGerado = registros.stream().mapToDouble(GeracaoConsumoMensalTO::getWattsGerados).sum();
        double totalConsumido = registros.stream().mapToDouble(GeracaoConsumoMensalTO::getWattsConsumidos).sum();
        if (totalConsumido == 0) {
            throw new InvalidGeracaoConsumoMensalException("Total consumido não pode ser zero.");
        }
        return totalGerado / totalConsumido;
    }
    public String gerarRelatorioProporcaoGeracaoConsumo(Long idMicrogrid) {
        double proporcao = calcularProporcaoGeracaoConsumo(idMicrogrid);
        return String.format("Proporção geração/consumo para a microgrid %d: %.2f", idMicrogrid, proporcao);
    }
    public String gerarAnaliseMediaDiferencaWatts(Long idMicrogrid) {
        double media = calcularMediaDiferencaWatts(idMicrogrid);
        return String.format("Média da diferença entre watts gerados e consumidos: %.2f", media);
    }
    public void gerarRelatoriosAutomatizados(Long idMicrogrid) {
        String analise = gerarAnaliseMediaDiferencaWatts(idMicrogrid);
        String proporcao = gerarRelatorioProporcaoGeracaoConsumo(idMicrogrid);
        System.out.println("Relatórios Gerados:");
        System.out.println(analise);
        System.out.println(proporcao);
    }

}

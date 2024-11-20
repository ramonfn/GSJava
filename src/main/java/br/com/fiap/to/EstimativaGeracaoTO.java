package br.com.fiap.to;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;

/**
 * Classe que representa uma estimativa de geração de energia.
 */
public class EstimativaGeracaoTO {

    @NotNull(message = "O ID da estimativa não pode ser nulo.")
    private Long idEstimativa;

    @NotNull(message = "O ID da microgrid é obrigatório.")
    private Long idMicrogrid;

    @NotNull(message = "O ano é obrigatório.")
    @Size(min = 4, max = 4, message = "O ano deve conter exatamente 4 dígitos.")
    private Integer ano;

    @Positive(message = "O mês deve ser um número positivo.")
    private Integer mes;

    @NotNull(message = "A estimativa de watts é obrigatória.")
    @Positive(message = "Os watts estimados devem ser maiores que zero.")
    private double wattsEstimados;

    public EstimativaGeracaoTO() {
    }

    public EstimativaGeracaoTO(Long idMicrogrid, Integer ano, Integer mes, double wattsEstimados) {
        if (idMicrogrid == null || ano == null || mes == null || wattsEstimados <= 0) {
            throw new IllegalArgumentException("Parâmetros inválidos para criar a estimativa.");
        }
        this.idMicrogrid = idMicrogrid;
        this.ano = ano;
        this.mes = mes;
        this.wattsEstimados = wattsEstimados;
    }

    // Getters e Setters
    public Long getIdEstimativa() {
        return idEstimativa;
    }

    public void setIdEstimativa(Long idEstimativa) {
        this.idEstimativa = idEstimativa;
    }

    public Long getIdMicrogrid() {
        return idMicrogrid;
    }

    public void setIdMicrogrid(Long idMicrogrid) {
        this.idMicrogrid = idMicrogrid;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public double getWattsEstimados() {
        return wattsEstimados;
    }

    public void setWattsEstimados(double wattsEstimados) {
        if (wattsEstimados <= 0) {
            throw new IllegalArgumentException("Os watts estimados devem ser maiores que zero.");
        }
        this.wattsEstimados = wattsEstimados;
    }

    /**
     * Calcula a média dos watts estimados com base em uma lista de estimativas.
     *
     * @param estimativas Lista de estimativas de geração.
     * @return Média dos watts estimados ou 0.0 se a lista estiver vazia.
     */
    public double calcularMediaWattsEstimados(ArrayList<EstimativaGeracaoTO> estimativas) {
        if (estimativas == null || estimativas.isEmpty()) {
            return 0.0;
        }
        double soma = 0.0;
        for (EstimativaGeracaoTO estimativa : estimativas) {
            soma += estimativa.getWattsEstimados();
        }
        return soma / estimativas.size();
    }

    /**
     * Filtra uma lista de estimativas pelo ano fornecido.
     *
     * @param estimativas Lista de estimativas de geração.
     * @param ano Ano para filtro.
     * @return Lista de estimativas do ano especificado.
     */
    public static ArrayList<EstimativaGeracaoTO> filtrarPorAno(ArrayList<EstimativaGeracaoTO> estimativas, Integer ano) {
        if (estimativas == null || ano == null) {
            throw new IllegalArgumentException("A lista de estimativas e o ano não podem ser nulos.");
        }
        ArrayList<EstimativaGeracaoTO> resultado = new ArrayList<>();
        for (EstimativaGeracaoTO estimativa : estimativas) {
            if (ano.equals(estimativa.getAno())) {
                resultado.add(estimativa);
            }
        }
        return resultado;
    }
}
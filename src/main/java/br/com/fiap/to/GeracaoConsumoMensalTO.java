package br.com.fiap.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class GeracaoConsumoMensalTO {

    @NotNull
    private Long idRegistro;

    @NotNull
    private Long idMicrogrid;

    @NotNull
    @Size(min = 4, max = 4)
    private Integer ano;

    @NotNull
    @Positive
    private Integer mes;

    @NotNull
    @Positive
    private double wattsGerados;

    @NotBlank
    @Size(max = 10)
    private String unidadeGeracao = "kWh";

    @NotNull
    @Positive
    private double wattsConsumidos;

    @NotBlank
    @Size(max = 10)
    private String unidadeConsumo = "kWh";

    /**
     * Construtor vazio necessário para frameworks
     */
    @SuppressWarnings("unused")
    public GeracaoConsumoMensalTO() {
    }

    /**
     * Construtor com parâmetros.
     */
    public GeracaoConsumoMensalTO(Long idMicrogrid,Long idRegistro, Integer ano, Integer mes,
                                  double wattsGerados, String unidadeGeracao,
                                  double wattsConsumidos, String unidadeConsumo) {
        this.idRegistro = idRegistro;
        this.idMicrogrid = idMicrogrid;
        this.ano = ano;
        this.mes = mes;
        this.wattsGerados = wattsGerados;
        this.unidadeGeracao = unidadeGeracao != null ? unidadeGeracao : "kWh";
        this.wattsConsumidos = wattsConsumidos;
        this.unidadeConsumo = unidadeConsumo != null ? unidadeConsumo : "kWh";
    }

    // Getters e Setters
    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
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
        if (ano == null || ano.toString().length() != 4) {
            throw new IllegalArgumentException("O ano deve conter exatamente 4 dígitos.");
        }
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve estar entre 1 e 12.");
        }
        this.mes = mes;
    }

    public double getWattsGerados() {
        return wattsGerados;
    }

    public void setWattsGerados(double wattsGerados) {
        if (wattsGerados <= 0) {
            throw new IllegalArgumentException("Watts gerados devem ser maiores que zero.");
        }
        this.wattsGerados = wattsGerados;
    }

    public String getUnidadeGeracao() {
        return unidadeGeracao;
    }

    public void setUnidadeGeracao(String unidadeGeracao) {
        if (unidadeGeracao == null || unidadeGeracao.trim().isEmpty()) {
            throw new IllegalArgumentException("Unidade de geração não pode ser nula ou vazia.");
        }
        this.unidadeGeracao = unidadeGeracao;
    }

    public double getWattsConsumidos() {
        return wattsConsumidos;
    }

    public void setWattsConsumidos(double wattsConsumidos) {
        if (wattsConsumidos <= 0) {
            throw new IllegalArgumentException("Watts consumidos devem ser maiores que zero.");
        }
        this.wattsConsumidos = wattsConsumidos;
    }

    public String getUnidadeConsumo() {
        return unidadeConsumo;
    }

    public void setUnidadeConsumo(String unidadeConsumo) {
        if (unidadeConsumo == null || unidadeConsumo.trim().isEmpty()) {
            throw new IllegalArgumentException("Unidade de consumo não pode ser nula ou vazia.");
        }
        this.unidadeConsumo = unidadeConsumo;
    }

    public double calcularDiferencaWatts() {
        return this.wattsGerados - this.wattsConsumidos;
    }

    @Override
    public String toString() {
        return String.format(
                "GeracaoConsumoMensalTO [ID: %d, Microgrid: %d, Ano: %d, Mes: %d, Gerados: %.2f %s, Consumidos: %.2f %s, Diferença: %.2f]",
                idRegistro, idMicrogrid, ano, mes, wattsGerados, unidadeGeracao,
                wattsConsumidos, unidadeConsumo, calcularDiferencaWatts());
    }

}
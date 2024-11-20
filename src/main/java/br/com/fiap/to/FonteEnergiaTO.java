package br.com.fiap.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class FonteEnergiaTO {
    @NotNull
    private Long idFonte;

    @NotNull
    private Long idMicrogrid;

    @NotBlank
    @Size(max = 50)
    private String tipo;

    @Positive
    private double capacidadeInstalada;

    @NotBlank
    @Size(max = 10)
    private String unidadeCapacidade = "kW";

    private LocalDate dataInstalacao;

    @NotBlank
    @Size(max = 20)
    private String status = "Operacional";

    // Construtor vazio
    public FonteEnergiaTO() {
    }

    // Construtor com parâmetros
    public FonteEnergiaTO(Long idMicrogrid, String tipo, double capacidadeInstalada, String unidadeCapacidade, LocalDate dataInstalacao, String status) {
        this.idMicrogrid = idMicrogrid;
        this.tipo = tipo;
        this.capacidadeInstalada = capacidadeInstalada;
        this.unidadeCapacidade = unidadeCapacidade != null ? unidadeCapacidade : "kW";
        this.dataInstalacao = dataInstalacao;
        this.status = status != null ? status : "Operacional";
    }

    // Getters e Setters
    public Long getIdFonte() {
        return idFonte;
    }

    public void setIdFonte(Long idFonte) {
        this.idFonte = idFonte;
    }

    public Long getIdMicrogrid() {
        return idMicrogrid;
    }

    public void setIdMicrogrid(Long idMicrogrid) {
        this.idMicrogrid = idMicrogrid;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCapacidadeInstalada() {
        return capacidadeInstalada;
    }

    public void setCapacidadeInstalada(double capacidadeInstalada) {
        this.capacidadeInstalada = capacidadeInstalada;
    }

    public String getUnidadeCapacidade() {
        return unidadeCapacidade;
    }

    public void setUnidadeCapacidade(String unidadeCapacidade) {
        this.unidadeCapacidade = unidadeCapacidade;
    }

    public LocalDate getDataInstalacao() {
        return dataInstalacao;
    }

    public void setDataInstalacao(LocalDate dataInstalacao) {
        this.dataInstalacao = dataInstalacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Lógica adicional

    /**
     * Verifica se a capacidade instalada está dentro do limite especificado.
     *
     * @param limite Limite de capacidade instalada.
     * @return true se a capacidade instalada for menor ou igual ao limite; false caso contrário.
     */
    public boolean verificarCapacidadeInstalada(double limite) {
        return this.capacidadeInstalada <= limite;
    }

    /**
     * Calcula o tempo de operação da fonte de energia em anos.
     *
     * @return Anos de operação desde a data de instalação.
     */
    public int calcularTempoOperacao() {
        if (this.dataInstalacao == null) {
            return 0;
        }
        return LocalDate.now().getYear() - this.dataInstalacao.getYear();
    }

    /**
     * Gera uma descrição detalhada da fonte de energia.
     *
     * @return String com informações completas da fonte.
     */
    public String gerarDescricaoDetalhada() {
        return String.format("Fonte de Energia [Tipo: %s, Capacidade: %.2f %s, Status: %s, Data de Instalação: %s]",
                this.tipo, this.capacidadeInstalada, this.unidadeCapacidade, this.status,
                this.dataInstalacao != null ? this.dataInstalacao.toString() : "N/A");
    }
}

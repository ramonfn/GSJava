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

}
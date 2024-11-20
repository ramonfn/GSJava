package br.com.fiap.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class MicrogridTO {
    @NotNull
    private Long idMicrogrid;

    @NotBlank
    private String nome;

    private String endereco;

    @PositiveOrZero
    private int totalResidencias;

    @PositiveOrZero
    private int totalHabitantes;

    // Construtor vazio
    public MicrogridTO() {
    }

    // Construtor com parâmetros
    public MicrogridTO(String nome, String endereco, int totalResidencias, int totalHabitantes) {
        this.nome = nome;
        this.endereco = endereco;
        this.totalResidencias = totalResidencias;
        this.totalHabitantes = totalHabitantes;
    }

    // Getters e Setters
    public Long getIdMicrogrid() {
        return idMicrogrid;
    }

    public void setIdMicrogrid(Long idMicrogrid) {
        this.idMicrogrid = idMicrogrid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getTotalResidencias() {
        return totalResidencias;
    }

    public void setTotalResidencias(int totalResidencias) {
        this.totalResidencias = totalResidencias;
    }

    public int getTotalHabitantes() {
        return totalHabitantes;
    }

    public void setTotalHabitantes(int totalHabitantes) {
        this.totalHabitantes = totalHabitantes;
    }

}

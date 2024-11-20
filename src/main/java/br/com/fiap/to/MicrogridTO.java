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

    // Lógica adicional

    /**
     * Calcula a densidade de habitantes por residência.
     *
     * @return Densidade de habitantes por residência.
     */
    public double calcularDensidadeHabitantesPorResidencia() {
        if (this.totalResidencias <= 0) {
            throw new IllegalArgumentException("O total de residências deve ser maior que zero.");
        }
        return (double) this.totalHabitantes / this.totalResidencias;
    }

    /**
     * Verifica se a densidade de habitantes está dentro de um limite aceitável.
     *
     * @param limite Limite máximo de densidade.
     * @return true se estiver dentro do limite; false caso contrário.
     */
    public boolean verificarDensidadeDentroDoLimite(double limite) {
        return this.calcularDensidadeHabitantesPorResidencia() <= limite;
    }

    /**
     * Gera uma descrição detalhada da microgrid.
     *
     * @return String com os detalhes da microgrid.
     */
    public String gerarDescricaoDetalhada() {
        return String.format(
                "Microgrid [ID: %d, Nome: %s, Endereço: %s, Total de Residências: %d, Total de Habitantes: %d, Densidade: %.2f habitantes/residência]",
                this.idMicrogrid, this.nome, this.endereco, this.totalResidencias,
                this.totalHabitantes, this.totalResidencias > 0 ? this.calcularDensidadeHabitantesPorResidencia() : 0.0
        );
    }
}

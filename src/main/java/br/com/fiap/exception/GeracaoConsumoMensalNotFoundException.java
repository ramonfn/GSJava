package br.com.fiap.exception;

/**
 * Exceção lançada quando um registro de geração e consumo mensal não é encontrado.
 */
public class GeracaoConsumoMensalNotFoundException extends RuntimeException {
    public GeracaoConsumoMensalNotFoundException(String message) {
        super(message);
    }
}

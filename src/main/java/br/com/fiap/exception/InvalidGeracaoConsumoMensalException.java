package br.com.fiap.exception;

/**
 * Exceção lançada quando há algum erro de validação no registro de geração e consumo mensal.
 */
public class InvalidGeracaoConsumoMensalException extends RuntimeException {
    public InvalidGeracaoConsumoMensalException(String message) {
        super(message);
    }
}


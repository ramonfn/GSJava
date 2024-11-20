package br.com.fiap.exception;

/**
 * Exceção lançada quando há algum erro de validação na estimativa de geração.
 */
public class InvalidEstimativaGeracaoException extends RuntimeException {
    public InvalidEstimativaGeracaoException(String message) {
        super(message);
    }
}

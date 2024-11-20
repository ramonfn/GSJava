package br.com.fiap.exception;

/**
 * Exceção lançada quando uma estimativa de geração não é encontrada.
 */
public class EstimativaGeracaoNotFoundException extends RuntimeException {
    public EstimativaGeracaoNotFoundException(String message) {
        super(message);
    }
}
package br.com.fiap.exception;

/**
 * Exceção lançada quando uma microgrid não é encontrada.
 */
public class MicrogridNotFoundException extends RuntimeException {
    public MicrogridNotFoundException(String message) {
        super(message);
    }
}

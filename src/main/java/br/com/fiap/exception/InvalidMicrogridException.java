package br.com.fiap.exception;

/**
 * Exceção lançada quando há algum erro de validação nos dados da microgrid.
 */
public class InvalidMicrogridException extends RuntimeException {
    public InvalidMicrogridException(String message) {
        super(message);
    }
}

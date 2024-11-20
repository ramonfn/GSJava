package br.com.fiap.exception;

/**
 * Exceção para quando uma Fonte de Energia for inválida.
 */
public class InvalidFonteEnergiaException extends FonteEnergiaException {
    public InvalidFonteEnergiaException(String message) {
        super(message);
    }
}

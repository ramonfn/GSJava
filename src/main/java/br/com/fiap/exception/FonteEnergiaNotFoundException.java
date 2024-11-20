package br.com.fiap.exception;

/**
 * Exceção para quando uma Fonte de Energia não for encontrada.
 */
public class FonteEnergiaNotFoundException extends FonteEnergiaException {
    public FonteEnergiaNotFoundException(String message) {
        super(message);
    }
}

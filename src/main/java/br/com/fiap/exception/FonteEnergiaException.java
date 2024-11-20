package br.com.fiap.exception;

/**
 * Exceção genérica para erros relacionados a Fonte de Energia.
 */
public class FonteEnergiaException extends RuntimeException {
    public FonteEnergiaException(String message) {
        super(message);
    }
}

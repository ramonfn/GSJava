package br.com.fiap.bo;

import br.com.fiap.dao.MicrogridDAO;
import br.com.fiap.exception.MicrogridNotFoundException;
import br.com.fiap.exception.InvalidMicrogridException;
import br.com.fiap.to.MicrogridTO;

import java.util.ArrayList;

public class MicrogridBO {
    private final MicrogridDAO microgridDAO;

    public MicrogridBO() {
        this.microgridDAO = new MicrogridDAO();
    }

    /**
     * Busca todas as microgrids.
     *
     * @return Lista de todas as microgrids.
     */
    public ArrayList<MicrogridTO> findAll() {
        return microgridDAO.findAll();
    }

    /**
     * Salva uma nova microgrid ou atualiza se já existir.
     *
     * @param microgrid Objeto da microgrid a ser salvo.
     * @return Microgrid salva.
     * @throws InvalidMicrogridException Se a validação falhar ou o nome já existir.
     */
    public MicrogridTO save(MicrogridTO microgrid) {
        validateMicrogrid(microgrid);

        try {
            // Se o nome já existir, retorna erro
            findByNome(microgrid.getNome().trim());
            throw new InvalidMicrogridException("Microgrid já existe com o nome informado.");
        } catch (MicrogridNotFoundException e) {
            // Prossegue para salvar caso não exista
            MicrogridTO savedMicrogrid = microgridDAO.save(microgrid);
            if (savedMicrogrid == null) {
                throw new InvalidMicrogridException("Erro ao salvar a microgrid. Tente novamente.");
            }
            return savedMicrogrid;
        }
    }

    /**
     * Busca uma microgrid pelo nome.
     *
     * @param nome Nome da microgrid.
     * @return Microgrid correspondente ao nome informado.
     */
    public MicrogridTO findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new InvalidMicrogridException("O nome da microgrid não pode ser vazio.");
        }
        return microgridDAO.findByNome(nome.trim());
    }

    /**
     * Busca uma microgrid pelo ID.
     *
     * @param idMicrogrid ID da microgrid.
     * @return Microgrid correspondente ao ID informado.
     */
    public MicrogridTO findById(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidMicrogridException("O ID da microgrid não pode ser nulo.");
        }
        return microgridDAO.findById(idMicrogrid);
    }

    /**
     * Exclui uma microgrid pelo ID.
     *
     * @param idMicrogrid ID da microgrid a ser excluída.
     * @return true se a exclusão for bem-sucedida.
     */
    public boolean delete(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidMicrogridException("O ID da microgrid não pode ser nulo.");
        }
        return microgridDAO.delete(idMicrogrid);
    }

    /**
     * Atualiza uma microgrid existente.
     *
     * @param microgrid Objeto da microgrid a ser atualizado.
     * @return true se a atualização for bem-sucedida.
     */
    public boolean update(MicrogridTO microgrid) {
        validateMicrogrid(microgrid);
        if (microgrid.getIdMicrogrid() == null || microgridDAO.findById(microgrid.getIdMicrogrid()) == null) {
            throw new MicrogridNotFoundException("Microgrid não encontrada para o ID informado.");
        }
        return microgridDAO.update(microgrid);
    }

    /**
     * Valida os dados de uma microgrid.
     *
     * @param microgrid Objeto da microgrid a ser validado.
     * @throws InvalidMicrogridException Se algum dado for inválido.
     */
    private void validateMicrogrid(MicrogridTO microgrid) {
        if (microgrid.getNome() == null || microgrid.getNome().trim().isEmpty()) {
            throw new InvalidMicrogridException("O nome da microgrid não pode ser vazio.");
        }
        if (microgrid.getTotalResidencias() < 0) {
            throw new InvalidMicrogridException("O total de residências não pode ser negativo.");
        }
        if (microgrid.getTotalHabitantes() < 0) {
            throw new InvalidMicrogridException("O total de habitantes não pode ser negativo.");
        }
    }
}

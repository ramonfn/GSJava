package br.com.fiap.dao;

import br.com.fiap.to.MicrogridTO;
import br.com.fiap.exception.MicrogridNotFoundException;
import br.com.fiap.exception.InvalidMicrogridException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MicrogridDAO extends Repository {

    /**
     * Busca todas as microgrids do banco de dados.
     *
     * @return Lista de microgrids ou uma lista vazia se nenhuma for encontrada.
     */
    public ArrayList<MicrogridTO> findAll() {
        ArrayList<MicrogridTO> microgrids = new ArrayList<>();
        String sql = "SELECT * FROM MICROGRID ORDER BY NOME";
        System.out.println("Executando query: " + sql);
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MicrogridTO microgrid = populateMicrogrid(rs);
                System.out.println("Microgrid encontrada: " + microgrid.getNome());
                microgrids.add(microgrid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidMicrogridException("Erro ao buscar todas as microgrids: " + e.getMessage());
        }

        if (microgrids.isEmpty()) {
            System.out.println("Nenhuma microgrid encontrada.");
            throw new MicrogridNotFoundException("Nenhuma microgrid encontrada.");
        }

        return microgrids;
    }

    /**
     * Busca uma microgrid pelo nome.
     *
     * @param nome Nome da microgrid.
     * @return Objeto MicrogridTO.
     */
    public MicrogridTO findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new InvalidMicrogridException("O nome da microgrid não pode ser vazio.");
        }

        String sql = "SELECT * FROM MICROGRID WHERE TRIM(NOME) = TRIM(?)";
        System.out.println("Executando query para buscar microgrid por nome: " + nome);

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, nome.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return populateMicrogrid(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidMicrogridException("Erro ao buscar microgrid por nome: " + e.getMessage());
        }

        System.out.println("Microgrid não encontrada com o nome: " + nome);
        throw new MicrogridNotFoundException("Microgrid não encontrada com o nome informado.");
    }

    /**
     * Salva uma nova microgrid no banco de dados.
     *
     * @param microgrid Objeto MicrogridTO contendo os dados a serem salvos.
     * @return Microgrid salva.
     */
    public MicrogridTO save(MicrogridTO microgrid) {
        String sql = "INSERT INTO MICROGRID (NOME, ENDERECO, TOTAL_RESIDENCIAS, TOTAL_HABITANTES) VALUES (?, ?, ?, ?)";
        System.out.println("Executando query para salvar microgrid: " + microgrid.getNome());

        try (PreparedStatement ps = getConnection().prepareStatement(sql, new String[]{"ID_MICROGRID"})) {
            ps.setString(1, microgrid.getNome());
            ps.setString(2, microgrid.getEndereco());
            ps.setInt(3, microgrid.getTotalResidencias());
            ps.setInt(4, microgrid.getTotalHabitantes());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        microgrid.setIdMicrogrid(rs.getLong(1)); // Define o ID gerado
                    }
                }
                System.out.println("Microgrid salva com sucesso: " + microgrid.getNome());
                return microgrid;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) { // Lida com violação de restrição única
                throw new InvalidMicrogridException("Já existe uma microgrid com o nome informado.");
            }
            e.printStackTrace();
            throw new InvalidMicrogridException("Erro ao salvar microgrid: " + e.getMessage());
        }
        throw new InvalidMicrogridException("Falha ao salvar a microgrid.");
    }


    /**
     * Busca uma microgrid pelo ID.
     *
     * @param idMicrogrid ID da microgrid.
     * @return Microgrid encontrada.
     */
    public MicrogridTO findById(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidMicrogridException("O ID da microgrid não pode ser nulo.");
        }

        String sql = "SELECT * FROM MICROGRID WHERE ID_MICROGRID = ?";
        System.out.println("Executando query para buscar microgrid por ID: " + idMicrogrid);

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return populateMicrogrid(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidMicrogridException("Erro ao buscar microgrid por ID: " + e.getMessage());
        }

        System.out.println("Microgrid não encontrada para o ID: " + idMicrogrid);
        throw new MicrogridNotFoundException("Microgrid não encontrada para o ID informado.");
    }

    /**
     * Exclui uma microgrid pelo ID.
     *
     * @param idMicrogrid ID da microgrid.
     * @return true se a exclusão for bem-sucedida.
     */
    public boolean delete(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidMicrogridException("O ID da microgrid não pode ser nulo.");
        }

        String sql = "DELETE FROM MICROGRID WHERE ID_MICROGRID = ?";
        System.out.println("Executando query para excluir microgrid com ID: " + idMicrogrid);

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid); // Configura o parâmetro corretamente
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Microgrid excluída com sucesso. ID: " + idMicrogrid);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidMicrogridException("Erro ao excluir microgrid: " + e.getMessage());
        }

        throw new MicrogridNotFoundException("Microgrid não encontrada para exclusão.");
    }


    /**
     * Atualiza os dados de uma microgrid existente no banco de dados.
     *
     * @param microgrid Objeto MicrogridTO com os dados atualizados.
     * @return true se a atualização for bem-sucedida.
     */
    public boolean update(MicrogridTO microgrid) {
        if (microgrid == null || microgrid.getIdMicrogrid() == null) {
            throw new InvalidMicrogridException("A microgrid ou o ID da microgrid não podem ser nulos.");
        }

        String sql = "UPDATE MICROGRID SET ENDERECO = ?, TOTAL_RESIDENCIAS = ?, TOTAL_HABITANTES = ? WHERE ID_MICROGRID = ?";
        System.out.println("Executando query para atualizar microgrid com ID: " + microgrid.getIdMicrogrid());

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, microgrid.getEndereco());
            ps.setInt(2, microgrid.getTotalResidencias());
            ps.setInt(3, microgrid.getTotalHabitantes());
            ps.setLong(4, microgrid.getIdMicrogrid());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Microgrid atualizada com sucesso. ID: " + microgrid.getIdMicrogrid());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidMicrogridException("Erro ao atualizar microgrid: " + e.getMessage());
        }

        throw new MicrogridNotFoundException("Microgrid não encontrada para atualização.");
    }

    /**
     * Preenche os dados de um objeto MicrogridTO a partir de um ResultSet.
     *
     * @param rs ResultSet com os dados da consulta.
     * @return Objeto MicrogridTO preenchido.
     * @throws SQLException Em caso de erro na manipulação do ResultSet.
     */
    private MicrogridTO populateMicrogrid(ResultSet rs) throws SQLException {
        MicrogridTO microgrid = new MicrogridTO();
        microgrid.setIdMicrogrid(rs.getLong("ID_MICROGRID"));
        microgrid.setNome(rs.getString("NOME"));
        microgrid.setEndereco(rs.getString("ENDERECO"));
        microgrid.setTotalResidencias(rs.getInt("TOTAL_RESIDENCIAS"));
        microgrid.setTotalHabitantes(rs.getInt("TOTAL_HABITANTES"));
        return microgrid;
    }
}

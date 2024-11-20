package br.com.fiap.dao;

import br.com.fiap.to.FonteEnergiaTO;
import br.com.fiap.exception.FonteEnergiaNotFoundException;
import br.com.fiap.exception.InvalidFonteEnergiaException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FonteEnergiaDAO extends Repository {

    /**
     * Busca todas as fontes de energia no banco de dados.
     *
     * @return Lista de fontes de energia ou lista vazia caso nenhuma fonte seja encontrada.
     */
    public ArrayList<FonteEnergiaTO> findAll() {
        ArrayList<FonteEnergiaTO> fontes = new ArrayList<>();
        String sql = "SELECT * FROM FONTE_ENERGIA ORDER BY ID_FONTE";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fontes.add(populateFonte(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as fontes de energia: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        if (fontes.isEmpty()) {
            throw new FonteEnergiaNotFoundException("Nenhuma fonte de energia encontrada.");
        }
        return fontes;
    }

    /**
     * Busca uma fonte de energia pelo ID.
     *
     * @param idFonte ID da fonte de energia.
     * @return Objeto FonteEnergiaTO.
     * @throws FonteEnergiaNotFoundException Se a fonte não for encontrada.
     */
    public FonteEnergiaTO findById(Long idFonte) {
        if (idFonte == null) {
            throw new InvalidFonteEnergiaException("ID da fonte não pode ser nulo.");
        }
        String sql = "SELECT * FROM FONTE_ENERGIA WHERE ID_FONTE = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idFonte);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return populateFonte(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fonte de energia por ID: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        throw new FonteEnergiaNotFoundException("Fonte de energia não encontrada para o ID informado.");
    }

    /**
     * Salva uma nova fonte de energia no banco de dados.
     *
     * @param fonte Objeto FonteEnergiaTO contendo os dados da fonte.
     * @return Objeto FonteEnergiaTO salvo.
     * @throws InvalidFonteEnergiaException Se ocorrer erro ao salvar.
     */
    public FonteEnergiaTO save(FonteEnergiaTO fonte) {
        String sql = "INSERT INTO FONTE_ENERGIA (ID_MICROGRID, TIPO, CAPACIDADE_INSTALADA, UNIDADE_CAPACIDADE, DATA_INSTALACAO, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, new String[]{"ID_FONTE"})) {
            ps.setLong(1, fonte.getIdMicrogrid());
            ps.setString(2, fonte.getTipo());
            ps.setDouble(3, fonte.getCapacidadeInstalada());
            ps.setString(4, fonte.getUnidadeCapacidade());
            ps.setDate(5, fonte.getDataInstalacao() != null ? java.sql.Date.valueOf(fonte.getDataInstalacao()) : null);
            ps.setString(6, fonte.getStatus());

            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    fonte.setIdFonte(rs.getLong(1)); // Define o ID gerado.
                }
                return fonte;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar fonte de energia: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        throw new InvalidFonteEnergiaException("Erro ao salvar fonte de energia.");
    }

    /**
     * Exclui uma fonte de energia pelo ID.
     *
     * @param idFonte ID da fonte de energia.
     * @return true se a exclusão for bem-sucedida.
     * @throws FonteEnergiaNotFoundException Se a fonte não for encontrada.
     */
    public boolean delete(Long idFonte) {
        if (idFonte == null) {
            throw new InvalidFonteEnergiaException("ID da fonte não pode ser nulo.");
        }
        String sql = "DELETE FROM FONTE_ENERGIA WHERE ID_FONTE = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idFonte);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir fonte de energia: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        throw new FonteEnergiaNotFoundException("Fonte de energia não encontrada para exclusão.");
    }

    /**
     * Atualiza os dados de uma fonte de energia.
     *
     * @param fonte Objeto FonteEnergiaTO com os dados atualizados.
     * @return true se a atualização for bem-sucedida.
     * @throws FonteEnergiaNotFoundException Se a fonte não for encontrada.
     */
    public boolean update(FonteEnergiaTO fonte) {
        String sql = "UPDATE FONTE_ENERGIA SET TIPO = ?, CAPACIDADE_INSTALADA = ?, UNIDADE_CAPACIDADE = ?, DATA_INSTALACAO = ?, STATUS = ? WHERE ID_FONTE = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, fonte.getTipo());
            ps.setDouble(2, fonte.getCapacidadeInstalada());
            ps.setString(3, fonte.getUnidadeCapacidade());
            ps.setDate(4, fonte.getDataInstalacao() != null ? java.sql.Date.valueOf(fonte.getDataInstalacao()) : null);
            ps.setString(5, fonte.getStatus());
            ps.setLong(6, fonte.getIdFonte());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fonte de energia: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        throw new FonteEnergiaNotFoundException("Fonte de energia não encontrada para atualização.");
    }

    /**
     * Busca todas as fontes de energia relacionadas a uma microgrid.
     *
     * @param idMicrogrid ID da microgrid.
     * @return Lista de fontes de energia.
     * @throws FonteEnergiaNotFoundException Se nenhuma fonte for encontrada.
     */
    public ArrayList<FonteEnergiaTO> findByMicrogrid(Long idMicrogrid) {
        ArrayList<FonteEnergiaTO> fontes = new ArrayList<>();
        String sql = "SELECT * FROM FONTE_ENERGIA WHERE ID_MICROGRID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fontes.add(populateFonte(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fontes de energia por microgrid: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        if (fontes.isEmpty()) {
            throw new FonteEnergiaNotFoundException("Nenhuma fonte de energia encontrada para a microgrid informada.");
        }
        return fontes;
    }

    /**
     * Preenche os dados de um objeto FonteEnergiaTO a partir de um ResultSet.
     *
     * @param rs ResultSet com os dados da consulta.
     * @return Objeto FonteEnergiaTO preenchido.
     * @throws SQLException Em caso de erro na manipulação do ResultSet.
     */
    private FonteEnergiaTO populateFonte(ResultSet rs) throws SQLException {
        FonteEnergiaTO fonte = new FonteEnergiaTO();
        fonte.setIdFonte(rs.getLong("ID_FONTE"));
        fonte.setIdMicrogrid(rs.getLong("ID_MICROGRID"));
        fonte.setTipo(rs.getString("TIPO"));
        fonte.setCapacidadeInstalada(rs.getDouble("CAPACIDADE_INSTALADA"));
        fonte.setUnidadeCapacidade(rs.getString("UNIDADE_CAPACIDADE"));
        fonte.setDataInstalacao(rs.getDate("DATA_INSTALACAO") != null ? rs.getDate("DATA_INSTALACAO").toLocalDate() : null);
        fonte.setStatus(rs.getString("STATUS"));
        return fonte;
    }


    public double calcularCapacidadeTotalPorMicrogrid(Long idMicrogrid) {
        String sql = "SELECT SUM(CAPACIDADE_INSTALADA) AS TOTAL FROM FONTE_ENERGIA WHERE ID_MICROGRID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TOTAL");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular capacidade total: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
        return 0;
    }

}

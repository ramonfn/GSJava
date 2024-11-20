package br.com.fiap.dao;

import br.com.fiap.to.EstimativaGeracaoTO;
import br.com.fiap.exception.InvalidEstimativaGeracaoException;
import br.com.fiap.exception.EstimativaGeracaoNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstimativaGeracaoDAO extends Repository {

    // Salva uma nova estimativa
    public EstimativaGeracaoTO save(EstimativaGeracaoTO estimativa) {
        String sql = "INSERT INTO ESTIMATIVA_GERACAO (ID_MICROGRID, ANO, MES, WATTS_ESTIMADOS) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, new String[]{"ID_ESTIMATIVA"})) {
            ps.setLong(1, estimativa.getIdMicrogrid());
            ps.setInt(2, estimativa.getAno());
            ps.setInt(3, estimativa.getMes());
            ps.setDouble(4, estimativa.getWattsEstimados());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        estimativa.setIdEstimativa(rs.getLong(1));
                    }
                }
                return estimativa;
            }
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao salvar estimativa: " + e.getMessage());
        } finally {
            closeConnection();
        }
        throw new InvalidEstimativaGeracaoException("Falha ao salvar estimativa.");
    }

    // Busca todas as estimativas
    public ArrayList<EstimativaGeracaoTO> findAll() {
        String sql = "SELECT * FROM ESTIMATIVA_GERACAO ORDER BY ANO, MES";
        ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                estimativas.add(populateEstimativa(rs));
            }
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao buscar estimativas: " + e.getMessage());
        } finally {
            closeConnection();
        }
        if (estimativas.isEmpty()) {
            throw new EstimativaGeracaoNotFoundException("Nenhuma estimativa encontrada.");
        }
        return estimativas;
    }

    // Busca estimativas por ID de microgrid
    public ArrayList<EstimativaGeracaoTO> findByMicrogrid(Long idMicrogrid) {
        validateMicrogridId(idMicrogrid);
        String sql = "SELECT * FROM ESTIMATIVA_GERACAO WHERE ID_MICROGRID = ? ORDER BY ANO, MES";
        ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estimativas.add(populateEstimativa(rs));
                }
            }
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao buscar estimativas por microgrid: " + e.getMessage());
        } finally {
            closeConnection();
        }
        if (estimativas.isEmpty()) {
            throw new EstimativaGeracaoNotFoundException("Nenhuma estimativa encontrada para a microgrid.");
        }
        return estimativas;
    }

    // Busca estimativa pelo ID
    public EstimativaGeracaoTO findById(Long idEstimativa) {
        if (idEstimativa == null) {
            throw new InvalidEstimativaGeracaoException("ID da estimativa é obrigatório.");
        }
        String sql = "SELECT * FROM ESTIMATIVA_GERACAO WHERE ID_ESTIMATIVA = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idEstimativa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return populateEstimativa(rs);
                }
            }
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao buscar estimativa por ID: " + e.getMessage());
        } finally {
            closeConnection();
        }
        throw new EstimativaGeracaoNotFoundException("Estimativa não encontrada para o ID informado.");
    }

    // Atualiza uma estimativa
    public boolean update(EstimativaGeracaoTO estimativa) {
        validateEstimativa(estimativa);
        String sql = "UPDATE ESTIMATIVA_GERACAO SET WATTS_ESTIMADOS = ? WHERE ID_MICROGRID = ? AND ANO = ? AND MES = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, estimativa.getWattsEstimados());
            ps.setLong(2, estimativa.getIdMicrogrid());
            ps.setInt(3, estimativa.getAno());
            ps.setInt(4, estimativa.getMes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao atualizar estimativa: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    // Exclui uma estimativa pelo ID
    public boolean delete(Long idEstimativa) {
        if (idEstimativa == null) {
            throw new InvalidEstimativaGeracaoException("ID da estimativa é obrigatório.");
        }
        String sql = "DELETE FROM ESTIMATIVA_GERACAO WHERE ID_ESTIMATIVA = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idEstimativa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao excluir estimativa: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    // Exclui estimativas por ID da microgrid, ano e mês
    public boolean deleteByMicrogridAnoMes(Long idMicrogrid, Integer ano, Integer mes) {
        if (idMicrogrid == null || ano == null || mes == null) {
            throw new InvalidEstimativaGeracaoException("ID da microgrid, ano e mês são obrigatórios para exclusão.");
        }

        String sql = "DELETE FROM ESTIMATIVA_GERACAO WHERE ID_MICROGRID = ? AND ANO = ? AND MES = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);
            ps.setInt(2, ano);
            ps.setInt(3, mes);
            return ps.executeUpdate() > 0; // Retorna true se uma ou mais linhas forem afetadas
        } catch (SQLException e) {
            throw new InvalidEstimativaGeracaoException("Erro ao excluir estimativa por microgrid, ano e mês: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }


    // Preenche o objeto EstimativaGeracaoTO com os dados do ResultSet
    private EstimativaGeracaoTO populateEstimativa(ResultSet rs) throws SQLException {
        EstimativaGeracaoTO estimativa = new EstimativaGeracaoTO();
        estimativa.setIdEstimativa(rs.getLong("ID_ESTIMATIVA"));
        estimativa.setIdMicrogrid(rs.getLong("ID_MICROGRID"));
        estimativa.setAno(rs.getInt("ANO"));
        estimativa.setMes(rs.getInt("MES"));
        estimativa.setWattsEstimados(rs.getDouble("WATTS_ESTIMADOS"));
        return estimativa;
    }

    // Validações auxiliares
    private void validateMicrogridId(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidEstimativaGeracaoException("ID da microgrid não pode ser nulo.");
        }
    }

    private void validateEstimativa(EstimativaGeracaoTO estimativa) {
        validateMicrogridId(estimativa.getIdMicrogrid());
        if (estimativa.getAno() == null || estimativa.getAno() < 1000 || estimativa.getAno() > 9999) {
            throw new InvalidEstimativaGeracaoException("Ano inválido. Deve conter exatamente 4 dígitos.");
        }
        if (estimativa.getMes() == null || estimativa.getMes() < 1 || estimativa.getMes() > 12) {
            throw new InvalidEstimativaGeracaoException("Mês inválido. Deve estar entre 1 e 12.");
        }
        if (estimativa.getWattsEstimados() <= 0) {
            throw new InvalidEstimativaGeracaoException("Watts estimados devem ser maiores que zero.");
        }
    }
}

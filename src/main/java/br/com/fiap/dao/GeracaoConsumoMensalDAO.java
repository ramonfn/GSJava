package br.com.fiap.dao;

import br.com.fiap.to.GeracaoConsumoMensalTO;
import br.com.fiap.exception.GeracaoConsumoMensalNotFoundException;
import br.com.fiap.exception.InvalidGeracaoConsumoMensalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeracaoConsumoMensalDAO extends Repository {

    public ArrayList<GeracaoConsumoMensalTO> findAll() {
        ArrayList<GeracaoConsumoMensalTO> registros = new ArrayList<>();
        String sql = "SELECT ID_REGISTRO, ID_MICROGRID, ANO, MES, WATTS_GERADOS, UNIDADE_GERACAO, WATTS_CONSUMIDOS, UNIDADE_CONSUMO FROM GERACAO_CONSUMO_MENSAL ORDER BY ANO, MES";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                registros.add(populateRegistro(rs));
            }
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao buscar registros: " + e.getMessage());
        } finally {
            closeConnection();
        }

        if (registros.isEmpty()) {
            throw new GeracaoConsumoMensalNotFoundException("Nenhum registro encontrado.");
        }
        return registros;
    }

    private GeracaoConsumoMensalTO populateRegistro(ResultSet rs) throws SQLException {
        return new GeracaoConsumoMensalTO(
                rs.getLong("ID_REGISTRO"),        // Certifique-se de que o nome existe no banco
                rs.getLong("ID_MICROGRID"),      // Nome correto da coluna no banco
                rs.getInt("ANO"),                // Nome correto da coluna no banco
                rs.getInt("MES"),                // Nome correto da coluna no banco
                rs.getDouble("WATTS_GERADOS"),   // Corrigido para "WATTS_GERADOS"
                rs.getString("UNIDADE_GERACAO"), // Nome correto da coluna no banco
                rs.getDouble("WATTS_CONSUMIDOS"),// Corrigido para "WATTS_CONSUMIDOS"
                rs.getString("UNIDADE_CONSUMO")  // Nome correto da coluna no banco
        );
    }


    public GeracaoConsumoMensalTO findByAnoMes(Long idMicrogrid, Integer ano, Integer mes) {
        validateIdAnoMes(idMicrogrid, ano, mes);

        String sql = "SELECT * FROM GERACAO_CONSUMO_MENSAL WHERE ID_MICROGRID = ? AND ANO = ? AND MES = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);
            ps.setInt(2, ano);
            ps.setInt(3, mes);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return populateRegistro(rs);
                }
            }
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao buscar registro por ano e mês: " + e.getMessage());
        } finally {
            closeConnection();
        }

        throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para o ano e mês especificados.");
    }

    public GeracaoConsumoMensalTO findById(Long idRegistro) {
        if (idRegistro == null) {
            throw new InvalidGeracaoConsumoMensalException("ID do registro não pode ser nulo.");
        }

        String sql = "SELECT * FROM GERACAO_CONSUMO_MENSAL WHERE ID_REGISTRO = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return populateRegistro(rs);
                }
            }
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao buscar registro por ID: " + e.getMessage());
        } finally {
            closeConnection();
        }

        throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para o ID especificado.");
    }

    public ArrayList<GeracaoConsumoMensalTO> findByMicrogrid(Long idMicrogrid) {
        if (idMicrogrid == null) {
            throw new InvalidGeracaoConsumoMensalException("ID da microgrid não pode ser nulo.");
        }

        ArrayList<GeracaoConsumoMensalTO> registros = new ArrayList<>();
        String sql = "SELECT * FROM GERACAO_CONSUMO_MENSAL WHERE ID_MICROGRID = ? ORDER BY ANO, MES";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idMicrogrid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(populateRegistro(rs));
                }
            }
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao buscar registros por microgrid: " + e.getMessage());
        } finally {
            closeConnection();
        }

        if (registros.isEmpty()) {
            throw new GeracaoConsumoMensalNotFoundException("Nenhum registro encontrado para a microgrid especificada.");
        }
        return registros;
    }

    public GeracaoConsumoMensalTO save(GeracaoConsumoMensalTO registro) {
        String sql = "INSERT INTO GERACAO_CONSUMO_MENSAL (ID_MICROGRID, ANO, MES, WATS_GERADOS, UNIDADE_GERACAO, WATS_CONSUMIDOS, UNIDADE_CONSUMO) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, new String[]{"ID_REGISTRO"})) {
            ps.setLong(1, registro.getIdMicrogrid());
            ps.setInt(2, registro.getAno());
            ps.setInt(3, registro.getMes());
            ps.setDouble(4, registro.getWattsGerados());
            ps.setString(5, registro.getUnidadeGeracao());
            ps.setDouble(6, registro.getWattsConsumidos());
            ps.setString(7, registro.getUnidadeConsumo());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        registro.setIdRegistro(rs.getLong(1));
                    }
                }
                return registro;
            }
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao salvar registro: " + e.getMessage());
        } finally {
            closeConnection();
        }
        throw new InvalidGeracaoConsumoMensalException("Falha ao salvar o registro.");
    }

    public boolean delete(Long idRegistro) {
        if (idRegistro == null) {
            throw new InvalidGeracaoConsumoMensalException("ID do registro não pode ser nulo.");
        }

        String sql = "DELETE FROM GERACAO_CONSUMO_MENSAL WHERE ID_REGISTRO = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idRegistro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao excluir registro: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }



    public boolean update(GeracaoConsumoMensalTO registro) {
        String sql = "UPDATE GERACAO_CONSUMO_MENSAL SET WATS_GERADOS = ?, UNIDADE_GERACAO = ?, WATS_CONSUMIDOS = ?, UNIDADE_CONSUMO = ? WHERE ID_REGISTRO = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, registro.getWattsGerados());
            ps.setString(2, registro.getUnidadeGeracao());
            ps.setDouble(3, registro.getWattsConsumidos());
            ps.setString(4, registro.getUnidadeConsumo());
            ps.setLong(5, registro.getIdRegistro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new InvalidGeracaoConsumoMensalException("Erro ao atualizar registro: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void validateIdAnoMes(Long idMicrogrid, Integer ano, Integer mes) {
        if (idMicrogrid == null || ano == null || mes == null) {
            throw new InvalidGeracaoConsumoMensalException("ID da microgrid, ano e mês são obrigatórios.");
        }
    }
}

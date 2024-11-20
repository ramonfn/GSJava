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
        GeracaoConsumoMensalTO registro = new GeracaoConsumoMensalTO();
        registro.setIdRegistro(rs.getLong("ID_REGISTRO"));
        registro.setIdMicrogrid(rs.getLong("ID_MICROGRID"));
        registro.setAno(rs.getInt("ANO"));
        registro.setMes(rs.getInt("MES"));
        registro.setUnidadeConsumo(rs.getString("UNIDADE_CONSUMO"));
        registro.setUnidadeGeracao(rs.getString("UNIDADE_GERACAO"));
        registro.setWattsConsumidos(rs.getDouble("WATTS_CONSUMIDOS"));
        registro.setWattsGerados(rs.getDouble("WATTS_GERADOS"));
        return registro;
    }


    public GeracaoConsumoMensalTO findById(Long idRegistro) {
        if (idRegistro == null) {
            throw new InvalidGeracaoConsumoMensalException("ID do registro não pode ser nulo.");
        }

        String sql = "SELECT * FROM GERACAO_CONSUMO_MENSAL WHERE ID_REGISTRO = ?";
        System.out.println("Executando query para buscar registro por ID: " + idRegistro);

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, idRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Registro encontrado: " + idRegistro);
                    return populateRegistro(rs);
                } else {
                    System.out.println("Registro não encontrado: " + idRegistro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar registro por ID: " + idRegistro + " - " + e.getMessage());
            throw new InvalidGeracaoConsumoMensalException("Erro ao buscar registro por ID: " + e.getMessage());
        } finally {
            closeConnection();
        }

        throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para o ID especificado.");
    }





    public GeracaoConsumoMensalTO save(GeracaoConsumoMensalTO registro) {
        String sql = "INSERT INTO GERACAO_CONSUMO_MENSAL (ID_MICROGRID, ANO, MES, WATTS_GERADOS, UNIDADE_GERACAO, WATTS_CONSUMIDOS, UNIDADE_CONSUMO) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE GERACAO_CONSUMO_MENSAL SET WATTS_GERADOS = ?, UNIDADE_GERACAO = ?, WATTS_CONSUMIDOS = ?, UNIDADE_CONSUMO = ? WHERE ID_REGISTRO = ?";
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
}

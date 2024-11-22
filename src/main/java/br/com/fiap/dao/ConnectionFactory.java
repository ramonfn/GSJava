package br.com.fiap.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static ConnectionFactory instance;
    private Connection conexao;
    private String url;
    private String user;
    private String pass;
    private String driver;

    public ConnectionFactory(String url, String user, String pass, String driver) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.driver = driver;
    }

    public static synchronized ConnectionFactory getInstance() {
        if (instance != null) {
            return instance;
        }

        try (InputStream file = ConnectionFactory.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (file == null) {
                throw new RuntimeException("Arquivo application.properties não encontrado no classpath.");
            }

            Properties prop = new Properties();
            prop.load(file);

            String url = prop.getProperty("datasource.url");
            String user = prop.getProperty("datasource.username");
            String pass = prop.getProperty("datasource.password");
            String driver = prop.getProperty("datasource.driver-class-name");

            instance = new ConnectionFactory(url, user, pass, driver);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar as configurações do banco de dados: " + e.getMessage(), e);
        }

        return instance;
    }

    public Connection getConexao() {
        try {
            if (this.conexao != null && !this.conexao.isClosed()) {
                return this.conexao;
            }

            if (this.getDriver() == null || this.getDriver().isEmpty()) {
                throw new ClassNotFoundException("Nome do driver nulo ou vazio.");
            }

            Class.forName(this.getDriver());
            this.conexao = DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPass());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao estabelecer a conexão com o banco de dados: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do banco de dados não encontrado: " + e.getMessage(), e);
        }
        return conexao;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getDriver() {
        return driver;
    }
}

package eu.devload.twitch.utils;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private final String ip;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxConnections;

    private HikariDataSource dataSource;

    public Database(String ip, int port, String database, String username, String password, int maxConnections) {
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;
    }

    public Database connect() {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl("jdbc:mariadb://" + ip + ":" + port + "/" + database);
        this.dataSource.setUsername(this.username);
        this.dataSource.setPassword(this.password);
        this.dataSource.setMaximumPoolSize(this.maxConnections);
        return this;
    }

    public ResultSet query(String sql) throws SQLException {
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            return stm.executeQuery();
        }
    }

    public int execute(String sql) throws SQLException {
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            return stm.executeUpdate();
        }
    }

    public void close() {
        this.dataSource.close();
    }

}

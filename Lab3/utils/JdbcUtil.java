package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JdbcUtil — centralised JDBC helper with HikariCP connection pool.
 *
 * Credentials are loaded from WEB-INF/db.properties (NOT committed to Git).
 * Falls back to environment variables DB_URL / DB_USERNAME / DB_PASSWORD
 * if the properties file is absent (useful for CI/CD deployments).
 *
 * executeQuery()  — returns a ResultSet; caller must close it (and the
 *                   backing Connection) via closeQuietly() when done.
 * executeUpdate() — opens a connection, runs the statement, closes everything
 *                   automatically via try-with-resources.
 */
public class JdbcUtil {

    private static HikariDataSource dataSource;

    static {
        initPool();
    }

    // ── Pool initialisation ──────────────────────────────────────────────────

    private static void initPool() {
        Properties props = loadProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(     get(props, "db.url",      "DB_URL",      "jdbc:sqlserver://localhost:1433;databaseName=PolyCoffee;encrypt=false"));
        config.setUsername(    get(props, "db.username",  "DB_USERNAME", "sa"));
        config.setPassword(    get(props, "db.password",  "DB_PASSWORD", ""));
        config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        config.setMaximumPoolSize(  intProp(props, "db.pool.maximumPoolSize",  10));
        config.setMinimumIdle(      intProp(props, "db.pool.minimumIdle",       2));
        config.setConnectionTimeout(intProp(props, "db.pool.connectionTimeout", 30000));
        config.setIdleTimeout(      intProp(props, "db.pool.idleTimeout",       600_000));
        config.setMaxLifetime(      intProp(props, "db.pool.maxLifetime",       1_800_000));
        config.setPoolName("PolyCoffeePool");

        dataSource = new HikariDataSource(config);
    }

    /** Load WEB-INF/db.properties from classpath (placed there by Maven war plugin). */
    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = JdbcUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) p.load(in);
        } catch (IOException ignored) {}
        return p;
    }

    /** Read from properties first, then env-var fallback, then hardcoded default. */
    private static String get(Properties p, String key, String envKey, String defaultVal) {
        String v = p.getProperty(key);
        if (v != null && !v.isBlank()) return v.trim();
        v = System.getenv(envKey);
        if (v != null && !v.isBlank()) return v.trim();
        return defaultVal;
    }

    private static int intProp(Properties p, String key, int defaultVal) {
        try { return Integer.parseInt(p.getProperty(key, String.valueOf(defaultVal)).trim()); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /** Obtain a connection from the pool. Always close it when done. */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Execute a SELECT query.
     * Returns a ResultSetHolder that wraps the ResultSet AND holds the
     * Connection + PreparedStatement so they can be closed together.
     * Always call closeQuietly(holder) when finished.
     *
     * Returns null on failure.
     */
    public static ResultSetHolder executeQuery(String sql, Object... values) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            bind(ps, values);
            ResultSet rs = ps.executeQuery();
            return new ResultSetHolder(conn, ps, rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Close a ResultSetHolder quietly (null-safe). */
    public static void closeQuietly(ResultSetHolder holder) {
        if (holder != null) holder.close();
    }

    /**
     * Execute INSERT / UPDATE / DELETE.
     * Return values:
     *   >= 1  success (rows affected)
     *   -1    duplicate key (UNIQUE / PK constraint violation)
     *   -2    FK constraint violation
     *    0    other error or 0 rows affected
     */
    public static int executeUpdate(String sql, Object... values) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, values);
            return ps.executeUpdate();
        } catch (SQLException e) {
            int code = e.getErrorCode();
            if (code == 2627 || code == 2601) {
                System.err.println("[DB] Duplicate key: " + e.getMessage());
                return -1;
            }
            if (code == 547) {
                System.err.println("[DB] FK constraint violation: " + e.getMessage());
                return -2;
            }
            e.printStackTrace();
        }
        return 0;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static void bind(PreparedStatement ps, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            ps.setObject(i + 1, values[i]);
        }
    }

    // ── ResultSetHolder inner class ──────────────────────────────────────────

    /**
     * Wraps a ResultSet together with its Connection and PreparedStatement
     * so they can all be closed in one go, preventing connection leaks.
     *
     * Usage:
     *   ResultSetHolder h = JdbcUtil.executeQuery(sql, params);
     *   try {
     *       ResultSet rs = h.rs();
     *       while (rs != null && rs.next()) { ... }
     *   } catch (SQLException e) { e.printStackTrace(); }
     *   finally { JdbcUtil.closeQuietly(h); }
     */
    public static class ResultSetHolder implements AutoCloseable {
        private final Connection conn;
        private final PreparedStatement ps;
        private final ResultSet rs;

        ResultSetHolder(Connection conn, PreparedStatement ps, ResultSet rs) {
            this.conn = conn;
            this.ps   = ps;
            this.rs   = rs;
        }

        public ResultSet rs() { return rs; }

        @Override
        public void close() {
            try { if (rs   != null) rs.close();   } catch (SQLException ignored) {}
            try { if (ps   != null) ps.close();   } catch (SQLException ignored) {}
            try { if (conn != null) conn.close();  } catch (SQLException ignored) {}
        }
    }
}

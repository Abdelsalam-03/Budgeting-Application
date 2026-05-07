package core.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public abstract class Model {

    protected Connection connection;

    private void initializeConnection() throws SQLException {
        if (this.connection == null) {
            this.connection = DatabaseConnection.getConnection();
        }
    }

    private void terminateConnection() {
        this.connection = null;
    }

    protected abstract String getTable();

    public void insert(String[] columns, Object[] values) throws SQLException {
        initializeConnection();
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values must have same length");
        }

        StringJoiner columnPart = new StringJoiner(", ");
        StringJoiner valuePart = new StringJoiner(", ");

        for (String col : columns) {
            columnPart.add(col);
            valuePart.add("?");
        }

        String sql = "INSERT INTO " + getTable()
                + " (" + columnPart + ") VALUES (" + valuePart + ")";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }

            stmt.executeUpdate();
        }
        terminateConnection();
    }

    public void destroy(
            String[] whereColumns,
            Object[] whereValues) throws SQLException {

        initializeConnection();

        if (whereColumns.length != whereValues.length) {
            throw new IllegalArgumentException("Columns and values must have same length");
        }

        StringJoiner wherePart = new StringJoiner(" AND ");
        for (String col : whereColumns) {
            wherePart.add(col + " = ?");
        }

        String sql = "DELETE FROM " + getTable() + " WHERE " + wherePart;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < whereValues.length; i++) {
                stmt.setObject(i + 1, whereValues[i]);
            }

            stmt.executeUpdate();

        } finally {
            terminateConnection();
        }
    }

    public <T> T get(
            String[] whereColumns,
            Object[] whereValues,
            RowMapper<T> mapper
    ) throws SQLException {

        initializeConnection();

        if (whereColumns.length != whereValues.length) {
            throw new IllegalArgumentException("Columns and values must have same length");
        }

        StringJoiner wherePart = new StringJoiner(" AND ");
        for (String col : whereColumns) {
            wherePart.add(col + " = ?");
        }

        String sql = "SELECT TOP(1) * FROM " + getTable() + " WHERE " + wherePart;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < whereValues.length; i++) {
                stmt.setObject(i + 1, whereValues[i]);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper.map(rs);
            }

            return null; // no result found
        } finally {
            terminateConnection();
        }
    }

    public <T> List<T> getAll(
            String[] whereColumns,
            Object[] whereValues,
            RowMapper<T> mapper
    ) throws SQLException {

        initializeConnection();

        if (whereColumns.length != whereValues.length) {
            throw new IllegalArgumentException("Columns and values must have same length");
        }

        StringJoiner wherePart = new StringJoiner(" AND ");
        for (String col : whereColumns) {
            wherePart.add(col + " = ?");
        }

        String sql = "SELECT * FROM " + getTable() + " WHERE " + wherePart;

        List<T> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < whereValues.length; i++) {
                stmt.setObject(i + 1, whereValues[i]);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapper.map(rs));
            }
        } finally {
            terminateConnection();
        }

        return results;
    }
}

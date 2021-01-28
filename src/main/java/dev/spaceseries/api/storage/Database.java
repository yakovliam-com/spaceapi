package dev.spaceseries.api.storage;

import java.sql.SQLException;

public interface Database <T> {

    T getConnection() throws SQLException;
}

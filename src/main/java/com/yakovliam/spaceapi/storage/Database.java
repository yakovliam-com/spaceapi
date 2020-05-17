package com.yakovliam.spaceapi.storage;

import java.sql.SQLException;

public interface Database <T> {

    T getConnection() throws SQLException;
}

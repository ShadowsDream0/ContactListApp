package com.shadowsdream.util;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class JdbcUtil {

    public static DataSource createPostgresDataSource(String url, String username, String pass) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(pass);

        return dataSource;
    }
}

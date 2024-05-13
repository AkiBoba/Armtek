package com.example.reactive.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class DataConfig {

    @Bean
    public ConnectionFactory connectionFactory(
                                               @Value("${spring.r2dbc.url}") String url,
                                               @Value("${spring.r2dbc.username}") String username,
                                               @Value("${spring.r2dbc.password}") String password) {
        return ConnectionFactories.get(
                ConnectionFactoryOptions.builder()
                        .option(DRIVER, "postgresql")
                        .option(HOST, "localhost")
                        .option(USER, username)
                        .option(PASSWORD, password)
                        .option(DATABASE, "allianceParse")
                        .build());
    }
}
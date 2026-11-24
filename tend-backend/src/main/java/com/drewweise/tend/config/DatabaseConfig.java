package com.drewweise.tend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class DatabaseConfig {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties tendDataSourceProperties() {
        return new DataSourceProperties();
    }

    // Keep Hibernate on Tend when a second DataSource is added.
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource tendDataSource(
            @Qualifier("tendDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("usda.datasource")
    public DataSourceProperties usdaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public HikariDataSource usdaDataSource(
            @Qualifier("usdaDataSourceProperties") DataSourceProperties properties) {
        HikariDataSource source = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
        source.setPoolName("usda-pool");
        source.setReadOnly(true);
        source.setMaximumPoolSize(3);
        return source;
    }

    @Bean
    @Primary
    public JdbcTemplate tendJdbcTemplate(@Qualifier("tendDataSource") DataSource source) {
        return new JdbcTemplate(source);
    }

    @Bean
    public JdbcTemplate usdaJdbcTemplate(@Qualifier("usdaDataSource") DataSource source) {
        return new JdbcTemplate(source);
    }
}

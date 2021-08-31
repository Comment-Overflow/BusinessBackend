package com.privateboat.forum.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource getDataSource(){
//        return DataSourceBuilder.create().build();
//    }

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://192.168.0.100:5432/comment_overflow");
        dataSourceBuilder.username("private_boat");
        dataSourceBuilder.password("123456");
        return dataSourceBuilder.build();
    }
}

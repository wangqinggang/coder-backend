//package com.ideaworks.club.domain.coder.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//    @Bean
//    public DataSource mysqlDataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
//        dataSource.setUrl("jdbc:jtds:sybase://129.1.50.194:8888/escloud_gy;charset=cp936");
//        dataSource.setUsername("");
//        dataSource.setPassword("");
//
//        return dataSource;
//    }
//}
package com.example.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * The type Datasource config.
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:/application.properties")
public class DatasourceConfig {
    private final ApplicationContext applicationContext;

    /**
     * Instantiates a new Datasource config.
     *
     * @param ac the ac
     */
    public DatasourceConfig(ApplicationContext ac) {
        this.applicationContext = ac;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * Hikari config hikari config.
     *
     * @return the hikari config
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /**
     * Data source data source.
     *
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    /**
     * Sql session factory sql session factory.
     *
     * @param dataSource the data source
     * @return the sql session factory
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean session = new SqlSessionFactoryBean();
        session.setDataSource(dataSource);
        session.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml"));
        session.setTypeAliasesPackage("com.example.backend");
        return session.getObject();
    }

    /**
     * Sql session template sql session template.
     *
     * @param sqlSessionFactory the sql session factory
     * @return the sql session template
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

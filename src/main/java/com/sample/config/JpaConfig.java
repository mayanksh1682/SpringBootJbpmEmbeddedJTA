package com.sample.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ImportResource(value = { "classpath:spring/spring-jbpmService.xml" })
public class JpaConfig {

	@Value("${dataSource.url}")
	private String url;

	@Value("${dataSource.username}")
	private String username;

	@Value("${dataSource.password}")
	private String password;

	@Value("${dataSource.driverClassName}")
	private String driverClassName;

	@Bean(name = "mySqlDataSource")
	@Primary
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setUrl(url);

		return dataSource;

	}

	@Bean(name = "sampleEntityManagerFactoryBean")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPersistenceXmlLocation("classpath*:META-INF/jbpm-persistence-JPA2.xml");		
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "create");		
		properties.setProperty("hibernate.show_sql", "true");	
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		properties.setProperty("hibernate.default_schema", "WF");
		return properties;
	}

	@Bean(name = "sampleTransactionManager")
	@Primary
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		transactionManager.setDataSource(dataSource());
		transactionManager.setNestedTransactionAllowed(true);
		return transactionManager;
	}

	@Bean(name = "sampleEntityManager")
	@Primary
	public SharedEntityManagerBean entityManager() {
		SharedEntityManagerBean entityManager = new SharedEntityManagerBean();
		entityManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return entityManager;
	}
}
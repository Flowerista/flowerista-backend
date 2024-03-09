package ua.flowerista.shop.configs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DaoConfig {

	@Value("${spring.datasource.url}")
	private String url;
	@Value("${myUrl}")
	private String myUrl;
	private String durl = "postgres://oizwagngtwvqya:f9b933512ab9c916b47411735fcc612ec1998758f15cd0ce95426cfa8e3394a0@ec2-79-125-89-233.eu-west-1.compute.amazonaws.com:5432/db3bq11hd0q54h";

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String ddlAuto;

	@Value("${spring.jpa.show-sql}")
	private String showSql;

	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String dialect;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("ua.flowerista.shop.models");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setJpaProperties(hibernateProperties());
		return emf;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		System.out.println("spring.datasource.url : " + url);
		System.out.println("System.getenv(\"JDBC_DATABASE_URL\"): " + System.getenv("JDBC_DATABASE_URL"));
		System.out.println("System.getenv(\"DATABASE_URL\"): " + System.getenv("DATABASE_URL"));
		System.out.println("myUrl: " + myUrl);
		URI dbUri = null;
		try {
			dbUri = new URI(url);

		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", dialect);
		properties.put("hibernate.show_sql", showSql);
		properties.put("hibernate.hbm2ddl.auto", ddlAuto);
		return properties;
	}
}

package org.ku8eye;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class App {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource primaryDataSource() {
		System.out.println("-------------------- DataSource init ---------------------");
		return DataSourceBuilder.create().build();
	}

	@Bean
	MapperScannerConfigurer mpperScannnerConfigurer() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setBasePackage("org.ku8eye.mapping");
		return msc;
	}

	@Bean(name = "sqlSessionFactory")
	SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
		SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
		ssfb.setDataSource(dataSource);
		ssfb.setTypeAliasesPackage("org.ku8eye.domain");
		return ssfb;
	}

	@Bean
	PlatformTransactionManager transactionManager(DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(App.class);
		app.setWebEnvironment(true);
		app.setShowBanner(false);

		Set<Object> set = new HashSet<Object>();
		// set.add("classpath:applicationContext.xml");
		app.setSources(set);
		app.run(args);
	}
}
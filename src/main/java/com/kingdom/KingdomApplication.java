package com.kingdom;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
public class KingdomApplication {

	public static void main(String[] args) {
		SpringApplication.run(KingdomApplication.class, args);
	}

	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
		factory.setEntityManagerFactory(emf);
		return factory;
	}

	@Bean
	@Autowired
	public HibernateTemplate getHibernateTemplate(SessionFactory session) {
		HibernateTemplate hb = new HibernateTemplate();
		hb.setCheckWriteOperations(false);
		hb.setSessionFactory(session);
		return hb;
	}
}

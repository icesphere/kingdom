package com.kingdom;

import org.hibernate.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@EnableTransactionManagement
@SpringBootApplication
public class KingdomApplication {

    public static void main(String[] args) {
        SpringApplication.run(KingdomApplication.class, args);
    }

    @Bean
    public SessionFactory sessionFactory(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(SessionFactory.class);
    }

    @Bean
    public HibernateTemplate getHibernateTemplate(SessionFactory session) {
        HibernateTemplate hb = new HibernateTemplate();
        hb.setSessionFactory(session);
        return hb;
    }
}

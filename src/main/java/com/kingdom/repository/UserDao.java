package com.kingdom.repository;

import com.kingdom.model.PlayerStats;
import com.kingdom.model.User;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public class UserDao {

    HibernateTemplate hibernateTemplate;

    public UserDao(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public List<User> getUsers(String stat, Integer value) {
        if (stat.equals("playedMobileGame")) {
            return getPlayedMobileGameUsers();
        } else if (stat.equals("usingDeckFrequencies")) {
            return getUsingDeckFrequencies();
        } else if (stat.equals("usingExcludedCards")) {
            return getUsingExcludedCards();
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(Restrictions.eq("active", true));
        criteria.add(Restrictions.eq("admin", false));
        if (value != null) {
            criteria.add(Restrictions.eq(stat, value));
        } else {
            criteria.add(Restrictions.eq(stat, true));
        }
        criteria.addOrder(Order.desc("lastLogin"));
        return (List<User>) hibernateTemplate.findByCriteria(criteria);
    }

    private List<User> getPlayedMobileGameUsers() {

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        SQLQuery sqlQuery = session.createSQLQuery("select u.* from users u where u.active = 1 and u.admin = 0 and u.userid in (select gu.userid from games g, game_users gu where g.gameid = gu.gameid and g.mobile = 1 and gu.userid > 0) order by u.last_login desc");
        sqlQuery.addEntity(User.class);
        List<User> users = sqlQuery.list();

        tx.commit();
        session.close();

        return users;
    }

    private List<User> getUsingDeckFrequencies() {

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        SQLQuery sqlQuery = session.createSQLQuery("select u.* from users u where u.active = 1 and u.admin = 0 and (base_weight != 3 or intrigue_weight != 3 or seaside_weight != 3 or alchemy_weight != 3 or prosperity_weight != 3 or cornucopia_weight != 3 or hinterlands_weight != 3 or promo_weight != 3 or salvation_weight != 3 or fairy_tale_weight != 3 or proletariat_weight != 3) order by u.last_login desc");
        sqlQuery.addEntity(User.class);
        List<User> users = sqlQuery.list();

        tx.commit();
        session.close();

        return users;
    }

    private List<User> getUsingExcludedCards() {

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        SQLQuery sqlQuery = session.createSQLQuery("select u.* from users u where u.active = 1 and u.admin = 0 and u.excluded_cards != '' order by u.last_login desc");
        sqlQuery.addEntity(User.class);
        List<User> users = sqlQuery.list();

        tx.commit();
        session.close();

        return users;
    }

    public void calculateGameStats(User user) {
        PlayerStats stats = new PlayerStats();

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        //against computer players
        SQLQuery query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.winner = 1 and gu.quit = 0 and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, (user.getUserId()));
        stats.setGamesAgainstComputerWon(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.winner = 0 and gu.quit = 0 and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        stats.setGamesAgainstComputerLost(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.quit = 1 and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        stats.setGamesAgainstComputerQuit(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and g.test_game = 1");
        query.setInteger(0, user.getUserId());
        stats.setTestGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select avg(margin_of_victory) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.winner = 1 and gu.quit = 0 and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        Object avg = query.uniqueResult();
        if (avg != null) {
            stats.setAverageMarginOfVictoryAgainstComputer(((BigDecimal) avg).doubleValue());
        }

        //against human players
        query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.winner = 1 and gu.quit = 0 and g.num_players-1 > g.num_computer_players and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        stats.setGamesWon(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.winner = 0 and gu.quit = 0 and g.num_players-1 > g.num_computer_players and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        stats.setGamesLost(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.quit = 1 and g.num_players-1 > g.num_computer_players and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        stats.setGamesQuit(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select avg(margin_of_victory) from game_users gu, games g where gu.gameid = g.gameid and gu.userid = ? and gu.winner = 1 and gu.quit = 0 and g.num_players-1 > g.num_computer_players and g.test_game = 0 and g.abandoned_game = 0");
        query.setInteger(0, user.getUserId());
        avg = query.uniqueResult();
        if (avg != null) {
            stats.setAverageMarginOfVictory(((BigDecimal) avg).doubleValue());
        }

        user.setStats(stats);

        tx.commit();

        session.close();
    }

    public int getErrorCount() {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        SQLQuery query = session.createSQLQuery("select count(*) from errors");
        int numErrors = ((BigInteger) query.uniqueResult()).intValue();

        tx.commit();
        session.close();

        return numErrors;
    }
}

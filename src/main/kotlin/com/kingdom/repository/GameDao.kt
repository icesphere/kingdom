package com.kingdom.repository

import org.springframework.stereotype.Repository

@Repository
class GameDao//todo
/*    public List<GameHistory> getGameHistoryList(int userId) {
        return getGameHistoryList(userId, 50);
    }

    @SuppressWarnings({"unchecked"})
    public List<GameHistory> getGameHistoryList(int userId, int limit) {
        Session session = null;
        try {
            session = hibernateTemplate.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();

            SQLQuery query = session.createSQLQuery("select g.* from games g, game_users gu where g.gameid = gu.gameid and gu.userid = :userId order by g.gameid desc limit " + limit);
            query.addEntity(GameHistory.class);
            query.setInteger("userId", userId);

            tx.commit();

            return query.list();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<GameUserHistory> getGamePlayersHistory(int gameId) {

        DetachedCriteria criteria = DetachedCriteria.forClass(GameUserHistory.class);
        criteria.add(Restrictions.eq("gameId", gameId));
        List<GameUserHistory> gamePlayers = (List<GameUserHistory>) hibernateTemplate.findByCriteria(criteria);
        List<GameUserHistory> players = new ArrayList<>();
        for (GameUserHistory gamePlayer : gamePlayers) {
            DetachedCriteria userCriteria = DetachedCriteria.forClass(User.class);
            userCriteria.add(Restrictions.eq("userId", gamePlayer.getUserId()));
            List<User> users = (List<User>) hibernateTemplate.findByCriteria(userCriteria);
            gamePlayer.setUsername(users.get(0).getUsername());
            players.add(gamePlayer);
        }
        return players;
    }

    public OverallStats getOverallStats() {
        OverallStats stats = new OverallStats();

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        SQLQuery query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_computer_players = g.num_players-1 and g.test_game = 0");
        stats.setGamesAgainstComputersPlayed(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_players-1 > g.num_computer_players and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesAgainstHumansPlayed(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.winner = 1 and gu.userid > 0 and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesAgainstComputersWon(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and gu.quit = 1 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesQuit(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct gu.userid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.userid > 0 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setNumUsers(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.userid > 0");
        stats.setNewAccountsCreated(((BigInteger) query.uniqueResult()).intValue());

        stats.setNewUsersWithGamePlayed(stats.getNumUsers());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.test_game = 1");
        stats.setTestGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.show_victory_points = 1");
        stats.setShowVictoryPointsGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.identical_starting_hands = 1");
        stats.setIdenticalStartingHandsGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.repeated = 1");
        stats.setRepeatedGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.recent_game = 1");
        stats.setRecentGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.mobile = 1");
        stats.setMobileGames(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from games g where g.abandoned_game = 1");
        stats.setGamesAbandoned(((BigInteger) query.uniqueResult()).intValue());

        //hard computer
        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_computer_players > 0 and gu.userid > -40 and gu.userid < -30 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesAgainstHardComputerPlayed(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.winner = 1 and g.num_computer_players > 0 and gu.userid > -40 and gu.userid < -30 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesWonByHardComputer(((BigInteger) query.uniqueResult()).intValue());

        //BMU computer
        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_computer_players > 0 and gu.userid > -50 and gu.userid < -40 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesAgainstBMUComputerPlayed(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.winner = 1 and g.num_computer_players > 0 and gu.userid > -50 and gu.userid < -40 and g.test_game = 0 and g.abandoned_game = 0");
        stats.setGamesWonByBMUComputer(((BigInteger) query.uniqueResult()).intValue());

        tx.commit();

        session.close();

        return stats;
    }

    public OverallStats getOverallStats(Date startDate, Date endDate) {
        OverallStats stats = new OverallStats();

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        String query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0";
        int result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesAgainstComputersPlayed(result);

        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_players-1 > g.num_computer_players and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesAgainstHumansPlayed(result);

        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.winner = 1 and gu.userid > 0 and g.num_computer_players = g.num_players-1 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesAgainstComputersWon(result);

        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and gu.quit = 1 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesQuit(result);

        query = "select count(distinct gu.userid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.userid > 0 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setNumUsers(result);

        query = "select count(*) from users u where u.userid > 0 and u.creation_date >= :startDate";
        if (endDate != null) {
            query += " and u.creation_date < :endDate";
        }
        SQLQuery q = session.createSQLQuery(query);
        q.setTimestamp("startDate", startDate);
        if (endDate != null) {
            q.setTimestamp("endDate", endDate);
        }
        stats.setNewAccountsCreated(((BigInteger) q.uniqueResult()).intValue());

        query = "select count(distinct gu.userid) from game_users gu, games g, users u where gu.gameid = g.gameid and gu.userid = u.userid and g.game_end_reason not like '%quit%' and gu.userid > 0 and g.test_game = 0 and g.abandoned_game = 0 and u.creation_date >= :startDate";
        if (endDate != null) {
            query += " and u.creation_date < :endDate";
        }
        q = session.createSQLQuery(query);
        q.setTimestamp("startDate", startDate);
        if (endDate != null) {
            q.setTimestamp("endDate", endDate);
        }
        stats.setNewUsersWithGamePlayed(((BigInteger) q.uniqueResult()).intValue());

        query = "select count(distinct g.gameid) from games g where g.test_game = 1";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setTestGames(result);

        query = "select count(distinct g.gameid) from games g where g.show_victory_points = 1";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setShowVictoryPointsGames(result);

        query = "select count(distinct g.gameid) from games g where g.identical_starting_hands = 1";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setIdenticalStartingHandsGames(result);

        query = "select count(distinct g.gameid) from games g where g.recent_game = 1";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setRecentGames(result);

        query = "select count(distinct g.gameid) from games g where g.mobile = 1";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setMobileGames(result);

        query = "select count(distinct g.gameid) from games g where g.abandoned_game = 1";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesAbandoned(result);

        //hard computer
        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_computer_players > 0 and gu.userid > -40 and gu.userid < -30 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesAgainstHardComputerPlayed(result);

        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.winner = 1 and g.num_computer_players > 0 and gu.userid > -40 and gu.userid < -30 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesWonByHardComputer(result);

        //BMU computer
        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and g.num_computer_players > 0 and gu.userid > -50 and gu.userid < -40 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesAgainstBMUComputerPlayed(result);

        query = "select count(distinct g.gameid) from game_users gu, games g where gu.gameid = g.gameid and g.game_end_reason not like '%quit%' and gu.winner = 1 and g.num_computer_players > 0 and gu.userid > -50 and gu.userid < -40 and g.test_game = 0 and g.abandoned_game = 0";
        result = getStatsResultFromQuery(query, startDate, endDate, session);
        stats.setGamesWonByBMUComputer(result);

        tx.commit();

        session.close();

        return stats;
    }

    private int getStatsResultFromQuery(String queryString, Date startDate, Date endDate, Session session) {
        queryString += " and g.end_date >= :startDate";
        if (endDate != null) {
            queryString += " and g.end_date < :endDate";
        }
        SQLQuery query = session.createSQLQuery(queryString);
        query.setTimestamp("startDate", startDate);
        if (endDate != null) {
            query.setTimestamp("endDate", endDate);
        }
        return ((BigInteger) query.uniqueResult()).intValue();
    }

    public UserStats getUserStats() {
        UserStats stats = new UserStats();

        Session session = hibernateTemplate.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        SQLQuery query = session.createSQLQuery("select count(*) from users u where u.active = 1 and u.admin = 0");
        stats.setActiveUsers(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.base_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setBaseDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.intrigue_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setIntrigueDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.seaside_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setSeasideDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.prosperity_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setProsperityDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.cornucopia_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setCornucopiaDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.hinterlands_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setHinterlandsDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.promo_checked = 1 and u.active = 1 and u.admin = 0");
        stats.setPromoDeck(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.sound_default = 2 and u.active = 1 and u.admin = 0");
        stats.setSoundOff(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.always_play_treasure_cards = 1 and u.active = 1 and u.admin = 0");
        stats.setAlwaysPlayTreasureCards(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.show_victory_points = 1 and u.active = 1 and u.admin = 0");
        stats.setShowVictoryPoints(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.identical_starting_hands = 1 and u.active = 1 and u.admin = 0");
        stats.setIdenticalStartingHands(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(distinct u.userid) from game_users gu, games g, users u where g.gameid = gu.gameid and gu.userid = u.userid and g.mobile = 1 and gu.userid > 0 and u.active = 1 and u.admin = 0");
        stats.setPlayedMobileGame(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.active = 1 and u.admin = 0 and (base_weight != 3 or intrigue_weight != 3 or seaside_weight != 3 or prosperity_weight != 3 or cornucopia_weight != 3 or hinterlands_weight != 3 or promo_weight != 3)");
        stats.setUsingDeckFrequencies(((BigInteger) query.uniqueResult()).intValue());

        query = session.createSQLQuery("select count(*) from users u where u.active = 1 and u.admin = 0 and excluded_cards != ''");
        stats.setUsingExcludedCards(((BigInteger) query.uniqueResult()).intValue());

        tx.commit();
        session.close();

        return stats;
    }*/

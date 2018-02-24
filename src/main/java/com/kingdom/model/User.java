package com.kingdom.model;

import com.kingdom.service.LoggedInUsers;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Entity
public class User {
    public static final String MALE = "M";
    public static final String FEMALE = "F";
    public static final String UNKNOWN = "U";
    public static final String COMPUTER = "C";

    public static final int SOUND_DEFAULT_ON = 1;
    public static final int SOUND_DEFAULT_OFF = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userid")
    private int userId;

    private String username = "";

    private String password = "";

    private boolean admin;

    private boolean guest;

    private String gender = MALE;

    @Column(name = "last_login")
    private Date lastLogin;

    private String email = "";

    private boolean invisible;

    @Column(name = "creation_date")
    private Date creationDate;

    private int logins;

    @Column(name = "sound_default")
    private int soundDefault = SOUND_DEFAULT_ON;

    @Column(name = "change_password")
    private boolean changePassword;

    @Column(name = "player2_default")
    private String player2Default = "computer_bmu";

    @Column(name = "player3_default")
    private String player3Default = "computer_hard";

    @Column(name = "player4_default")
    private String player4Default = "none";

    @Column(name = "player5_default")
    private String player5Default = "none";

    @Column(name = "player6_default")
    private String player6Default = "none";

    @Column(name = "base_checked")
    private boolean baseChecked = true;

    @Column(name = "intrigue_checked")
    private boolean intrigueChecked = true;

    @Column(name = "seaside_checked")
    private boolean seasideChecked = true;

    @Column(name = "alchemy_checked")
    private boolean alchemyChecked = true;

    @Column(name = "prosperity_checked")
    private boolean prosperityChecked = true;

    @Column(name = "cornucopia_checked")
    private boolean cornucopiaChecked = true;

    @Column(name = "hinterlands_checked")
    private boolean hinterlandsChecked = true;

    @Column(name = "promo_checked")
    private boolean promoChecked = false;

    @Column(name = "salvation_checked")
    private boolean salvationChecked = false;

    @Column(name = "fairy_tale_checked")
    private boolean fairyTaleChecked = false;

    @Column(name = "leaders_checked")
    private boolean leadersChecked = false;

    @Column(name = "proletariat_checked")
    private boolean proletariatChecked = false;

    @Column(name = "other_fan_cards_checked")
    private boolean otherFanCardsChecked = false;

    @Column(name = "base_weight")
    private int baseWeight = 3;

    @Column(name = "intrigue_weight")
    private int intrigueWeight = 3;

    @Column(name = "seaside_weight")
    private int seasideWeight = 3;

    @Column(name = "alchemy_weight")
    private int alchemyWeight = 3;

    @Column(name = "prosperity_weight")
    private int prosperityWeight = 3;

    @Column(name = "cornucopia_weight")
    private int cornucopiaWeight = 3;

    @Column(name = "hinterlands_weight")
    private int hinterlandsWeight = 3;

    @Column(name = "promo_weight")
    private int promoWeight = 3;

    @Column(name = "salvation_weight")
    private int salvationWeight = 3;

    @Column(name = "fairy_tale_weight")
    private int fairyTaleWeight = 3;

    @Column(name = "proletariat_weight")
    private int proletariatWeight = 3;

    @Column(name = "fan_weight")
    private int fanWeight = 3;

    @Column(name = "always_play_treasure_cards")
    private boolean alwaysPlayTreasureCards;

    @Column(name = "show_victory_points")
    private boolean showVictoryPoints;

    @Column(name = "identical_starting_hands")
    private boolean identicalStartingHands;

    private boolean active;

    @Column(name = "excluded_cards")
    private String excludedCards = "";

    @Column(name = "user_agent")
    private String userAgent = "";

    @Column(name = "ipaddress")
    private String ipAddress = "";

    private String location = "";

    private transient int gameId;
    private transient Date lastActivity = new Date();
    private transient Date lastRefresh = new Date();
    private transient String status = "";
    private transient PlayerStats stats;
    private transient RefreshLobby refreshLobby = new RefreshLobby();
    private transient boolean mobile;

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getLastLogin() {
        if (lastLogin == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try {
                lastLogin = sdf.parse("09/09/1999");
            } catch (ParseException e) {
                //
            }
        }
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public boolean isExpired() {
        if (gameId > 0) {
            return false;
        }
        long thirtyMinutes = 60 * 1000 * 30;
        boolean expired = lastRefresh == null || lastRefresh.getTime() + thirtyMinutes < System.currentTimeMillis();
        if (expired) {
            LoggedInUsers.getInstance().userLoggedOut(this);
            LoggedInUsers.getInstance().refreshLobbyPlayers();
        }
        return expired;
    }

    public boolean isIdle() {
        if (gameId > 0) {
            return false;
        }
        long threeMinutes = 60 * 1000 * 3;
        return lastActivity.getTime() + threeMinutes < System.currentTimeMillis();
    }

    public String getIdleTime() {
        long timeDifference = System.currentTimeMillis() - lastActivity.getTime();
        int minutes = (int) (timeDifference / 1000 / 60);
        int hours = 0;
        if (minutes > 60) {
            hours = minutes / 60;
            minutes = minutes - hours * 60;
        }
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        sb.append(minutes).append("m");
        return sb.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getLogins() {
        return logins;
    }

    public void setLogins(int logins) {
        this.logins = logins;
    }

    public void incrementLogins() {
        logins++;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setStats(PlayerStats stats) {
        this.stats = stats;
    }

    public int getSoundDefault() {
        return soundDefault;
    }

    public void setSoundDefault(int soundDefault) {
        this.soundDefault = soundDefault;
    }

    public String getPlayer2Default() {
        return player2Default;
    }

    public void setPlayer2Default(String player2Default) {
        this.player2Default = player2Default;
    }

    public String getPlayer3Default() {
        return player3Default;
    }

    public void setPlayer3Default(String player3Default) {
        this.player3Default = player3Default;
    }

    public String getPlayer4Default() {
        return player4Default;
    }

    public void setPlayer4Default(String player4Default) {
        this.player4Default = player4Default;
    }

    public String getPlayer5Default() {
        return player5Default;
    }

    public void setPlayer5Default(String player5Default) {
        this.player5Default = player5Default;
    }

    public String getPlayer6Default() {
        return player6Default;
    }

    public void setPlayer6Default(String player6Default) {
        this.player6Default = player6Default;
    }

    public void setPlayerDefault(int i, String selection) {
        if (i == 2) {
            player2Default = selection;
        } else if (i == 3) {
            player3Default = selection;
        } else if (i == 4) {
            player4Default = selection;
        } else if (i == 5) {
            player5Default = selection;
        } else if (i == 6) {
            player6Default = selection;
        }
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public boolean isBaseChecked() {
        return baseChecked;
    }

    public void setBaseChecked(boolean baseChecked) {
        this.baseChecked = baseChecked;
    }

    public boolean isIntrigueChecked() {
        return intrigueChecked;
    }

    public void setIntrigueChecked(boolean intrigueChecked) {
        this.intrigueChecked = intrigueChecked;
    }

    public boolean isSeasideChecked() {
        return seasideChecked;
    }

    public void setSeasideChecked(boolean seasideChecked) {
        this.seasideChecked = seasideChecked;
    }

    public boolean isAlchemyChecked() {
        return alchemyChecked;
    }

    public void setAlchemyChecked(boolean alchemyChecked) {
        this.alchemyChecked = alchemyChecked;
    }

    public boolean isProsperityChecked() {
        return prosperityChecked;
    }

    public void setProsperityChecked(boolean prosperityChecked) {
        this.prosperityChecked = prosperityChecked;
    }

    public boolean isCornucopiaChecked() {
        return cornucopiaChecked;
    }

    public void setCornucopiaChecked(boolean cornucopiaChecked) {
        this.cornucopiaChecked = cornucopiaChecked;
    }

    public boolean isHinterlandsChecked() {
        return hinterlandsChecked;
    }

    public void setHinterlandsChecked(boolean hinterlandsChecked) {
        this.hinterlandsChecked = hinterlandsChecked;
    }

    public boolean isPromoChecked() {
        return promoChecked;
    }

    public void setPromoChecked(boolean promoChecked) {
        this.promoChecked = promoChecked;
    }

    public boolean isSalvationChecked() {
        return salvationChecked;
    }

    public void setSalvationChecked(boolean salvationChecked) {
        this.salvationChecked = salvationChecked;
    }

    public boolean isFairyTaleChecked() {
        return fairyTaleChecked;
    }

    public void setFairyTaleChecked(boolean fairyTaleChecked) {
        this.fairyTaleChecked = fairyTaleChecked;
    }

    public boolean isLeadersChecked() {
        return leadersChecked;
    }

    public void setLeadersChecked(boolean leadersChecked) {
        this.leadersChecked = leadersChecked;
    }

    public boolean isProletariatChecked() {
        return proletariatChecked;
    }

    public void setProletariatChecked(boolean proletariatChecked) {
        this.proletariatChecked = proletariatChecked;
    }

    public boolean isOtherFanCardsChecked() {
        return otherFanCardsChecked;
    }

    public void setOtherFanCardsChecked(boolean otherFanCardsChecked) {
        this.otherFanCardsChecked = otherFanCardsChecked;
    }

    public RefreshLobby getRefreshLobby() {
        return refreshLobby;
    }

    public void toggleSoundDefault() {
        if (soundDefault == SOUND_DEFAULT_ON) {
            soundDefault = SOUND_DEFAULT_OFF;
        } else {
            soundDefault = SOUND_DEFAULT_ON;
        }
    }

    public boolean isAlwaysPlayTreasureCards() {
        return alwaysPlayTreasureCards;
    }

    public void setAlwaysPlayTreasureCards(boolean alwaysPlayTreasureCards) {
        this.alwaysPlayTreasureCards = alwaysPlayTreasureCards;
    }

    public boolean isShowVictoryPoints() {
        return showVictoryPoints;
    }

    public void setShowVictoryPoints(boolean showVictoryPoints) {
        this.showVictoryPoints = showVictoryPoints;
    }

    public boolean isIdenticalStartingHands() {
        return identicalStartingHands;
    }

    public void setIdenticalStartingHands(boolean identicalStartingHands) {
        this.identicalStartingHands = identicalStartingHands;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(int baseWeight) {
        this.baseWeight = baseWeight;
    }

    public int getIntrigueWeight() {
        return intrigueWeight;
    }

    public void setIntrigueWeight(int intrigueWeight) {
        this.intrigueWeight = intrigueWeight;
    }

    public int getSeasideWeight() {
        return seasideWeight;
    }

    public void setSeasideWeight(int seasideWeight) {
        this.seasideWeight = seasideWeight;
    }

    public int getAlchemyWeight() {
        return alchemyWeight;
    }

    public void setAlchemyWeight(int alchemyWeight) {
        this.alchemyWeight = alchemyWeight;
    }

    public int getProsperityWeight() {
        return prosperityWeight;
    }

    public void setProsperityWeight(int prosperityWeight) {
        this.prosperityWeight = prosperityWeight;
    }

    public int getCornucopiaWeight() {
        return cornucopiaWeight;
    }

    public void setCornucopiaWeight(int cornucopiaWeight) {
        this.cornucopiaWeight = cornucopiaWeight;
    }

    public int getHinterlandsWeight() {
        return hinterlandsWeight;
    }

    public void setHinterlandsWeight(int hinterlandsWeight) {
        this.hinterlandsWeight = hinterlandsWeight;
    }

    public int getPromoWeight() {
        return promoWeight;
    }

    public void setPromoWeight(int promoWeight) {
        this.promoWeight = promoWeight;
    }

    public int getSalvationWeight() {
        return salvationWeight;
    }

    public void setSalvationWeight(int salvationWeight) {
        this.salvationWeight = salvationWeight;
    }

    public int getFairyTaleWeight() {
        return fairyTaleWeight;
    }

    public void setFairyTaleWeight(int fairyTaleWeight) {
        this.fairyTaleWeight = fairyTaleWeight;
    }

    public int getProletariatWeight() {
        return proletariatWeight;
    }

    public void setProletariatWeight(int proletariatWeight) {
        this.proletariatWeight = proletariatWeight;
    }

    public int getFanWeight() {
        return fanWeight;
    }

    public void setFanWeight(int fanWeight) {
        this.fanWeight = fanWeight;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public String getExcludedCards() {
        return excludedCards;
    }

    public void setExcludedCards(String excludedCards) {
        this.excludedCards = excludedCards;
    }

    public List<String> getExcludedCardNames() {
        return Arrays.asList(excludedCards.split(","));
    }
}

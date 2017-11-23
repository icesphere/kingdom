package com.kingdom.web;

import com.kingdom.model.User;
import com.kingdom.service.GameRoomManager;
import com.kingdom.service.LoggedInUsers;
import com.kingdom.service.UserManager;
import com.kingdom.util.EmailUtil;
import com.kingdom.util.KingdomUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class MainController {

    private UserManager manager = new UserManager();
    public static final int MAX_USER_LIMIT = 100;

    @RequestMapping("/login.html")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("login");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean mobile = KingdomUtil.isMobile(request);
        modelAndView.addObject("mobile", mobile);
        if(username != null && password != null){
            User user = manager.getUser(username, password);
            if(user != null) {
                user.setLastLogin(new Date());
                user.incrementLogins();
                user.setUserAgent(request.getHeader("User-Agent"));
                user.setIpAddress(request.getRemoteAddr());
                user.setLocation(KingdomUtil.getLocation(user.getIpAddress()));
                user.setMobile(mobile);
                manager.saveUser(user);
                if (!user.isAdmin() && LoggedInUsers.getInstance().getUsers().size() >= MAX_USER_LIMIT) {
                    ModelAndView modelAndView1 = new ModelAndView("userLimitReached");
                    modelAndView1.addObject("mobile", KingdomUtil.isMobile(request));
                    return modelAndView1;
                }
                else {
                    LoggedInUsers.getInstance().userLoggedIn(user);
                    LoggedInUsers.getInstance().refreshLobbyPlayers();
                    HttpSession session = request.getSession(true);
                    session.setMaxInactiveInterval(60 * 30);
                    session.setAttribute("user", user);
                    session.setAttribute("mobile", mobile);
                    if (user.isChangePassword()) {
                        return new ModelAndView("redirect:/changeTemporaryPassword.html");
                    }
                    else {
                        return new ModelAndView("redirect:/showGameRooms.html");
                    }
                }
            }
        }
        return modelAndView;
    }

    @RequestMapping("/logout.html")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = getUser(request);
        KingdomUtil.logoutUser(user, request);
        return KingdomUtil.getLoginModelAndView(request);
    }

    @RequestMapping("/admin.html")
	public ModelAndView admin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!isAdmin(request)){
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("admin");
        User user = getUser(request);
        User loggedInUser = LoggedInUsers.getInstance().getUser(user.getUserId());
        boolean showCancelGame = loggedInUser != null && loggedInUser.getGameId() > 0;
        modelAndView.addObject("showCancelGame", showCancelGame);
        modelAndView.addObject("numErrors", manager.getErrorCount());
        modelAndView.addObject("loggedInUsersCount", LoggedInUsers.getInstance().getUsers().size());
        modelAndView.addObject("updatingWebsite", GameRoomManager.getInstance().isUpdatingWebsite());
        modelAndView.addObject("updatingMessage", GameRoomManager.getInstance().getUpdatingMessage());
        modelAndView.addObject("showNews", GameRoomManager.getInstance().isShowNews());
        modelAndView.addObject("news", GameRoomManager.getInstance().getNews());
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
	}

    @RequestMapping("/requestAccount.html")
    public ModelAndView requestAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("requestAccount");
        modelAndView.addObject("error", "");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/submitAccount.html")
    public ModelAndView submitAccountRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String error = null;
        String username = request.getParameter("username");
        if (username == null || username.equals("")) {
            error = "Username required";
        }
        else if (manager.usernameExists(username) || username.equalsIgnoreCase("admin")){
            error = "Username already exists";
        }
        else {
            Pattern p = Pattern.compile("[a-zA-Z0-9_]+");
            Matcher m = p.matcher(username);
            boolean matchFound = m.matches();
            if (!matchFound) {
                error = "Invalid username (can only contain letters and numbers, with no spaces)";
            }
        }
        String email = request.getParameter("email");
        if (email == null || email.equals("")) {
            error = "Email required";
        }
        else {
            Pattern p = Pattern.compile(".+@.+\\.[a-zA-Z0-9_]+");
            Matcher m = p.matcher(email);
            boolean matchFound = m.matches();
            if (!matchFound) {
                error = "Invalid email";
            }
        }

        if (error == null) {
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(RandomStringUtils.random(6, "ABCDEFGH23456789"));
            user.setCreationDate(new Date());
            if (request.getParameter("gender") != null && request.getParameter("gender").equals(User.FEMALE)) {
                user.setGender(request.getParameter("gender"));
            }
            user.setChangePassword(true);
            manager.saveUser(user);
            EmailUtil.sendAccountRequestEmail(user);
            ModelAndView modelAndView = new ModelAndView("accountRequestSubmitted");
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        }
        else {
            ModelAndView modelAndView = new ModelAndView("requestAccount");
            modelAndView.addObject("error", error);
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        }
    }

    @RequestMapping("/myAccount.html")
    public ModelAndView myAccount(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("myAccount");
        modelAndView.addObject("user", user);
        modelAndView.addObject("invalidPassword", false);
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/saveMyAccountPassword.html")
    public ModelAndView saveMyAccountPassword(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String currentPassword = request.getParameter("currentPassword");
        if (currentPassword.equals(user.getPassword())) {
            String password = request.getParameter("password");
            user.setPassword(password);
            manager.saveUser(user);
        }
        else {
            ModelAndView modelAndView = new ModelAndView("myAccount");
            modelAndView.addObject("user", user);
            modelAndView.addObject("invalidPassword", true);
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        }
        return new ModelAndView("redirect:/showGameRooms.html");
    }

    @RequestMapping("/changeTemporaryPassword.html")
    public ModelAndView changeTemporaryPassword(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String password = request.getParameter("password");
        if (password != null && !password.trim().equals("")) {
            user.setPassword(password);
            user.setChangePassword(false);
            manager.saveUser(user);
        }
        else {
            ModelAndView modelAndView = new ModelAndView("changePassword");
            modelAndView.addObject("user", user);
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        }
        return new ModelAndView("redirect:/showGameRooms.html");
    }

    @RequestMapping("/saveMyAccount.html")
    public ModelAndView saveMyAccount(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        int soundDefault = KingdomUtil.getRequestInt(request, "soundDefault", User.SOUND_DEFAULT_ON);
        user.setSoundDefault(soundDefault);
        manager.saveUser(user);
        return new ModelAndView("redirect:/showGameRooms.html");
    }

    public void setUserManager(UserManager manager) {
        this.manager = manager;
    }

    private User getUser(HttpServletRequest request) {
        return KingdomUtil.getUser(request);
    }

    private boolean isAdmin(HttpServletRequest request){
        User user = getUser(request);
        return user != null && user.isAdmin();
    }

    @RequestMapping("/showHelp.html")
    public ModelAndView showHelp(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("help");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/showDisclaimer.html")
    public ModelAndView showDisclaimer(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("disclaimer");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/getPlayerStatsDivFromAdmin.html")
    public ModelAndView getPlayerStatsDivFromAdmin(HttpServletRequest request, HttpServletResponse response) {
        if (!isAdmin(request)) {
            return new ModelAndView("redirect:/login.html");
        }
        User user = manager.getUser(KingdomUtil.getRequestInt(request, "userId", -1));
        if (user == null) {
            return new ModelAndView("redirect:/listUsers.html");
        }
        ModelAndView modelAndView = new ModelAndView("playerStatsDiv");
        manager.calculateGameStats(user);
        modelAndView.addObject("user", user);
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/forgotLogin.html")
    public ModelAndView forgotLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("forgotLogin");
        modelAndView.addObject("error", "");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/submitForgotLogin.html")
    public ModelAndView submitForgotLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String error = null;
        String email = request.getParameter("email");
        if (email == null || email.equals("")) {
            error = "Email required";
        }
        else {
            Pattern p = Pattern.compile(".+@.+\\.[a-zA-Z0-9_]+");
            Matcher m = p.matcher(email);
            boolean matchFound = m.matches();
            if (!matchFound) {
                error = "Invalid email";
            }
        }

        if (error == null) {
            User user = manager.getUserByEmail(email);
            if (user != null) {
                EmailUtil.sendForgotLoginEmail(user);
            }
            ModelAndView modelAndView = new ModelAndView("forgotLoginSubmitted");
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        }
        else {
            ModelAndView modelAndView = new ModelAndView("forgotLogin");
            modelAndView.addObject("error", error);
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        }
    }

    @RequestMapping("/setUpdatingWebsite.html")
    public ModelAndView setUpdatingWebsite(HttpServletRequest request, HttpServletResponse response) {
        boolean updatingWebsite = KingdomUtil.getRequestBoolean(request, "updatingWebsite");
        GameRoomManager.getInstance().setUpdatingWebsite(updatingWebsite);
        GameRoomManager.getInstance().setUpdatingMessage(request.getParameter("updatingMessage"));
        return new ModelAndView("empty");
    }

    @RequestMapping("/setShowNews.html")
    public ModelAndView setShowNews(HttpServletRequest request, HttpServletResponse response) {
        boolean showNews = KingdomUtil.getRequestBoolean(request, "showNews");
        GameRoomManager.getInstance().setShowNews(showNews);
        GameRoomManager.getInstance().setNews(request.getParameter("news"));
        return new ModelAndView("empty");
    }

    @RequestMapping("/switchSite.html")
    public ModelAndView switchSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean mobile = KingdomUtil.isMobile(request);
        request.getSession().setAttribute("mobile", !mobile);
        return new ModelAndView("empty");
    }
}

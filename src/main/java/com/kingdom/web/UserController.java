package com.kingdom.web;

import com.kingdom.model.User;
import com.kingdom.service.UserManager;
import com.kingdom.util.KingdomUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    UserManager manager;

    public UserController(UserManager manager) {
        this.manager = manager;
    }

    @RequestMapping("/listUsers.html")
    public ModelAndView listUsers(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.getAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("users");
        List<User> users = manager.getUsers();
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @RequestMapping("/saveUser.html")
    public ModelAndView saveUser(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser = getUser(request);
        if (loggedInUser == null || !loggedInUser.getAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        User user;
        String id = request.getParameter("id");
        if (id.equals("0")) {
            user = new User();
            user.setCreationDate(new Date());
        } else {
            user = manager.getUser(Integer.parseInt(id));
        }
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setEmail(request.getParameter("email"));
        if (request.getParameter("gender").equals(User.FEMALE)) {
            user.setGender(User.FEMALE);
        } else {
            user.setGender(User.MALE);
        }
        manager.saveUser(user);
        return listUsers(request, response);
    }

    @RequestMapping("/deleteUser.html")
    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser = getUser(request);
        if (loggedInUser == null || !loggedInUser.getAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String id = request.getParameter("id");
        User user = manager.getUser(Integer.parseInt(id));
        if (!user.getAdmin()) {
            manager.deleteUser(user);
        }
        return listUsers(request, response);
    }

    @RequestMapping("/showUser.html")
    public ModelAndView showUser(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser = getUser(request);
        if (loggedInUser == null || !loggedInUser.getAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("user");
        String id = request.getParameter("id");
        User user;
        if (id.equals("0")) {
            user = new User();
        } else {
            user = manager.getUser(Integer.parseInt(id));
        }

        modelAndView.addObject("user", user);
        return modelAndView;
    }

    public void setUserManager(UserManager manager) {
        this.manager = manager;
    }

    private User getUser(HttpServletRequest request) {
        return KingdomUtil.getUser(request);
    }

    @RequestMapping("/showUsersForStat.html")
    public ModelAndView showUsersForStat(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.getAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("users");
        String stat = request.getParameter("stat");
        Integer value = null;
        if (request.getParameter("value") != null) {
            value = Integer.parseInt(request.getParameter("value"));
        }
        List<User> users = manager.getUsers(stat, value);
        modelAndView.addObject("users", users);
        return modelAndView;
    }
}

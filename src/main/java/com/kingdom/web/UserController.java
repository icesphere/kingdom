package com.kingdom.web;

import com.kingdom.model.User;
import com.kingdom.service.UserManager;
import com.kingdom.util.KingdomUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"UnusedDeclaration"})
public class UserController extends MultiActionController {

    private UserManager manager = new UserManager();

    public ModelAndView listUsers(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
		ModelAndView modelAndView = new ModelAndView("users");
		List<User> users = manager.getUsers();
		modelAndView.addObject("users", users);
		return modelAndView;
    }

    public ModelAndView saveUser(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser = getUser(request);
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        User user;
        String id = request.getParameter("id");
        if (id.equals("0")) {
            user = new User();
            user.setCreationDate(new Date());
        }
        else {
            user = manager.getUser(Integer.parseInt(id));
        }
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setEmail(request.getParameter("email"));
        if (request.getParameter("gender").equals(User.FEMALE)) {
            user.setGender(User.FEMALE);
        }
        else{
            user.setGender(User.MALE);
        }
        manager.saveUser(user);
        return listUsers(request, response);
    }

    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser = getUser(request);
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String id = request.getParameter("id");
        User user = manager.getUser(Integer.parseInt(id));
        if (!user.isAdmin()) {
            manager.deleteUser(user);
        }
        return listUsers(request, response);
    }

    public ModelAndView showUser(HttpServletRequest request, HttpServletResponse response) {
        User loggedInUser = getUser(request);
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
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

    public ModelAndView showUsersForStat(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
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

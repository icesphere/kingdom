package com.kingdom.web

import com.kingdom.model.User
import com.kingdom.service.UserManager
import com.kingdom.util.KingdomUtil
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Date

@Suppress("unused")
@Controller
class UserController(internal var manager: UserManager) {

    @RequestMapping("/listUsers.html")
    fun listUsers(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("users")
        val users = manager.users
        modelAndView.addObject("users", users)
        return modelAndView
    }

    @RequestMapping("/saveUser.html")
    fun saveUser(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val loggedInUser = getUser(request)
        if (loggedInUser == null || !loggedInUser.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val user: User
        val id = request.getParameter("id")
        if (id == "0") {
            user = User()
            user.creationDate = Date()
        } else {
            user = manager.getUser(Integer.parseInt(id))
        }
        user.username = request.getParameter("username")
        user.password = request.getParameter("password")
        manager.saveUser(user)
        return listUsers(request, response)
    }

    @RequestMapping("/deleteUser.html")
    fun deleteUser(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val loggedInUser = getUser(request)
        if (loggedInUser == null || !loggedInUser.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val id = request.getParameter("id")
        val user = manager.getUser(Integer.parseInt(id))
        if (!user.admin) {
            manager.deleteUser(user)
        }
        return listUsers(request, response)
    }

    @RequestMapping("/showUser.html")
    fun showUser(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val loggedInUser = getUser(request)
        if (loggedInUser == null || !loggedInUser.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("user")
        val id = request.getParameter("id")
        val user: User
        if (id == "0") {
            user = User()
        } else {
            user = manager.getUser(Integer.parseInt(id))
        }

        modelAndView.addObject("user", user)
        return modelAndView
    }

    fun setUserManager(manager: UserManager) {
        this.manager = manager
    }

    private fun getUser(request: HttpServletRequest): User? {
        return KingdomUtil.getUser(request)
    }
}

package com.kingdom.web

import com.kingdom.model.User
import com.kingdom.service.GameRoomManager
import com.kingdom.service.LoggedInUsers
import com.kingdom.service.UserManager
import com.kingdom.util.EmailUtil
import com.kingdom.util.KingdomUtil
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.util.*
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class MainController(private var manager: UserManager?,
                     private val gameRoomManager: GameRoomManager) {

    @RequestMapping("/login.html")
    @Throws(Exception::class)
    fun login(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("login")
        val username = request.getParameter("username")
        val password = request.getParameter("password")
        val mobile = KingdomUtil.isMobile(request)
        modelAndView.addObject("mobile", mobile)
        if (username != null && password != null) {
            val user = manager!!.getUser(username, password)
            if (user != null) {
                user.lastLogin = Date()
                user.incrementLogins()
                user.userAgent = request.getHeader("User-Agent")
                user.ipAddress = request.remoteAddr
                user.location = KingdomUtil.getLocation(user.ipAddress)
                user.isMobile = mobile
                manager!!.saveUser(user)
                if (!user.admin && LoggedInUsers.instance.getUsers().size >= MAX_USER_LIMIT) {
                    val modelAndView1 = ModelAndView("userLimitReached")
                    modelAndView1.addObject("mobile", KingdomUtil.isMobile(request))
                    return modelAndView1
                } else {
                    LoggedInUsers.instance.userLoggedIn(user)
                    LoggedInUsers.instance.refreshLobbyPlayers()
                    val session = request.getSession(true)
                    session.maxInactiveInterval = 60 * 30
                    session.setAttribute("user", user)
                    session.setAttribute("mobile", mobile)
                    return if (user.changePassword) {
                        ModelAndView("redirect:/changeTemporaryPassword.html")
                    } else {
                        ModelAndView("redirect:/showGameRooms.html")
                    }
                }
            }
        }
        return modelAndView
    }

    @RequestMapping("/logout.html")
    @Throws(Exception::class)
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        KingdomUtil.logoutUser(user, request)
        return KingdomUtil.getLoginModelAndView(request)
    }

    @RequestMapping("/admin.html")
    @Throws(Exception::class)
    fun admin(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        if (!isAdmin(request)) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("admin")
        val user = getUser(request)
        val loggedInUser = LoggedInUsers.instance.getUser(user!!.userId)
        val showCancelGame = loggedInUser != null && loggedInUser.gameId > 0
        modelAndView.addObject("showCancelGame", showCancelGame)
        modelAndView.addObject("numErrors", manager!!.errorCount)
        modelAndView.addObject("loggedInUsersCount", LoggedInUsers.instance.getUsers().size)
        modelAndView.addObject("updatingWebsite", gameRoomManager.isUpdatingWebsite)
        modelAndView.addObject("updatingMessage", gameRoomManager.updatingMessage!!)
        modelAndView.addObject("showNews", gameRoomManager.isShowNews)
        modelAndView.addObject("news", gameRoomManager.news)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/requestAccount.html")
    @Throws(Exception::class)
    fun requestAccount(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("requestAccount")
        modelAndView.addObject("error", "")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/submitAccount.html")
    @Throws(Exception::class)
    fun submitAccountRequest(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        var error: String? = null
        val username = request.getParameter("username")
        if (username == null || username == "") {
            error = "Username required"
        } else if (manager!!.usernameExists(username) || username.equals("admin", ignoreCase = true)) {
            error = "Username already exists"
        } else {
            val p = Pattern.compile("[a-zA-Z0-9_]+")
            val m = p.matcher(username)
            val matchFound = m.matches()
            if (!matchFound) {
                error = "Invalid username (can only contain letters and numbers, with no spaces)"
            }
        }
        val email = request.getParameter("email")
        if (email == null || email == "") {
            error = "Email required"
        } else {
            val p = Pattern.compile(".+@.+\\.[a-zA-Z0-9_]+")
            val m = p.matcher(email)
            val matchFound = m.matches()
            if (!matchFound) {
                error = "Invalid email"
            }
        }

        if (error == null) {
            val user = User()
            user.email = email
            user.username = username
            user.password = RandomStringUtils.random(6, "ABCDEFGH23456789")
            user.creationDate = Date()
            if (request.getParameter("gender") != null && request.getParameter("gender") == User.FEMALE) {
                user.gender = request.getParameter("gender")
            }
            user.changePassword = true
            manager!!.saveUser(user)
            EmailUtil.sendAccountRequestEmail(user)
            val modelAndView = ModelAndView("accountRequestSubmitted")
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } else {
            val modelAndView = ModelAndView("requestAccount")
            modelAndView.addObject("error", error)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        }
    }

    @RequestMapping("/myAccount.html")
    fun myAccount(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        val modelAndView = ModelAndView("myAccount")
        modelAndView.addObject("user", user)
        modelAndView.addObject("invalidPassword", false)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/saveMyAccountPassword.html")
    fun saveMyAccountPassword(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        val currentPassword = request.getParameter("currentPassword")
        if (currentPassword == user.password) {
            val password = request.getParameter("password")
            user.password = password
            manager!!.saveUser(user)
        } else {
            val modelAndView = ModelAndView("myAccount")
            modelAndView.addObject("user", user)
            modelAndView.addObject("invalidPassword", true)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        }
        return ModelAndView("redirect:/showGameRooms.html")
    }

    @RequestMapping("/changeTemporaryPassword.html")
    fun changeTemporaryPassword(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        val password = request.getParameter("password")
        if (password != null && password.trim { it <= ' ' } != "") {
            user.password = password
            user.changePassword = false
            manager!!.saveUser(user)
        } else {
            val modelAndView = ModelAndView("changePassword")
            modelAndView.addObject("user", user)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        }
        return ModelAndView("redirect:/showGameRooms.html")
    }

    @RequestMapping("/saveMyAccount.html")
    fun saveMyAccount(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        val soundDefault = KingdomUtil.getRequestInt(request, "soundDefault", User.SOUND_DEFAULT_ON)
        user.soundDefault = soundDefault
        manager!!.saveUser(user)
        return ModelAndView("redirect:/showGameRooms.html")
    }

    fun setUserManager(manager: UserManager) {
        this.manager = manager
    }

    private fun getUser(request: HttpServletRequest): User? {
        return KingdomUtil.getUser(request)
    }

    private fun isAdmin(request: HttpServletRequest): Boolean {
        val user = getUser(request)
        return user != null && user.admin
    }

    @RequestMapping("/showHelp.html")
    fun showHelp(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("help")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/showDisclaimer.html")
    fun showDisclaimer(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("disclaimer")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/getPlayerStatsDivFromAdmin.html")
    fun getPlayerStatsDivFromAdmin(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        if (!isAdmin(request)) {
            return ModelAndView("redirect:/login.html")
        }
        val user = manager!!.getUser(KingdomUtil.getRequestInt(request, "userId", -1))
                ?: return ModelAndView("redirect:/listUsers.html")
        val modelAndView = ModelAndView("playerStatsDiv")
        manager!!.calculateGameStats(user)
        modelAndView.addObject("user", user)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/forgotLogin.html")
    @Throws(Exception::class)
    fun forgotLogin(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("forgotLogin")
        modelAndView.addObject("error", "")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/submitForgotLogin.html")
    @Throws(Exception::class)
    fun submitForgotLogin(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        var error: String? = null
        val email = request.getParameter("email")
        if (email == null || email == "") {
            error = "Email required"
        } else {
            val p = Pattern.compile(".+@.+\\.[a-zA-Z0-9_]+")
            val m = p.matcher(email)
            val matchFound = m.matches()
            if (!matchFound) {
                error = "Invalid email"
            }
        }

        if (error == null) {
            val user = manager!!.getUserByEmail(email!!)
            if (user != null) {
                EmailUtil.sendForgotLoginEmail(user)
            }
            val modelAndView = ModelAndView("forgotLoginSubmitted")
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } else {
            val modelAndView = ModelAndView("forgotLogin")
            modelAndView.addObject("error", error)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        }
    }

    @RequestMapping("/setUpdatingWebsite.html")
    fun setUpdatingWebsite(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val updatingWebsite = KingdomUtil.getRequestBoolean(request, "updatingWebsite")
        gameRoomManager.isUpdatingWebsite = updatingWebsite
        gameRoomManager.updatingMessage = request.getParameter("updatingMessage")
        return ModelAndView("empty")
    }

    @RequestMapping("/setShowNews.html")
    fun setShowNews(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val showNews = KingdomUtil.getRequestBoolean(request, "showNews")
        gameRoomManager.isShowNews = showNews
        gameRoomManager.news = request.getParameter("news")
        return ModelAndView("empty")
    }

    @RequestMapping("/switchSite.html")
    @Throws(Exception::class)
    fun switchSite(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val mobile = KingdomUtil.isMobile(request)
        request.session.setAttribute("mobile", !mobile)
        return ModelAndView("empty")
    }

    companion object {

        private val MAX_USER_LIMIT = 100
    }
}

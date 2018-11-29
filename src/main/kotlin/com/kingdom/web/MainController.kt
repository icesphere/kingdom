package com.kingdom.web

import com.kingdom.model.User
import com.kingdom.service.GameRoomManager
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.KingdomUtil
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Suppress("unused")
@Controller
class MainController(private val gameRoomManager: GameRoomManager) {

    @RequestMapping("/login.html")
    @Throws(Exception::class)
    fun login(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("login")
        val username = request.getParameter("username")
        val mobile = KingdomUtil.isMobile(request)
        modelAndView.addObject("mobile", mobile)
        if (username != null) {
            if (LoggedInUsers.usernameBeingUsed(username)) {
                modelAndView.addObject("usernameBeingUsed", true)
            } else {
                val user = User()
                user.username = username
                LoggedInUsers.userLoggedIn(user)
                LoggedInUsers.refreshLobbyPlayers()
                val session = request.getSession(true)
                session.maxInactiveInterval = 60 * 30
                session.setAttribute("user", user)
                session.setAttribute("mobile", mobile)
                return ModelAndView("redirect:/showGameRooms.html")
            }
        }
        return modelAndView
    }

    @RequestMapping("/logout.html")
    @Throws(Exception::class)
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user != null) {
            val gameId = request.session.getAttribute("gameId")
            if (gameId != null) {
                val game = gameRoomManager.getGame(gameId as String)
                if (game != null) {
                    val player = game.playerMap[user.userId]
                    if (player != null) {
                        game.playerQuitGame(player)
                        game.playerExitedGame(player)
                    }
                }
            }
            KingdomUtil.logoutUser(user, request)
        }
        return KingdomUtil.getLoginModelAndView(request)
    }

    @RequestMapping("/admin.html")
    @Throws(Exception::class)
    fun admin(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        if (!isAdmin(request)) {
            return ModelAndView("adminLogin")
        }
        val modelAndView = ModelAndView("admin")
        val user = getUser(request)!!
        val loggedInUser = LoggedInUsers.getUser(user.userId)
        val showGameActions = loggedInUser?.gameId != null
        modelAndView.addObject("showGameActions", showGameActions)
        modelAndView.addObject("loggedInUsersCount", LoggedInUsers.getUsers().size)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/adminLogin.html")
    @Throws(Exception::class)
    fun adminLogin(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)

        val adminPassword = request.getParameter("adminPassword")

        if (adminPassword == "changethekingdom") {
            user.admin = true
            return admin(request, response)
        }

        val modelAndView = ModelAndView("adminLogin")
        modelAndView.addObject("wrongPassword", true)
        return modelAndView
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

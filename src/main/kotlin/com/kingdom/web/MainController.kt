package com.kingdom.web

import com.kingdom.model.User
import com.kingdom.service.GameRoomManager
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.KingdomUtil
import com.kingdom.util.USERNAME_COOKIE
import com.kingdom.util.removeSpaces
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Suppress("unused")
@Controller
class MainController(private val gameRoomManager: GameRoomManager) {

    @RequestMapping("/login.html")
    @Throws(Exception::class)
    fun login(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val accessAllowed = isAccessAllowed(request)
        if (!accessAllowed) {
            return KingdomUtil.getAccessModelAndView(request)
        }

        val modelAndView = ModelAndView("login")
        val username = request.getParameter("username")
        val mobile = KingdomUtil.isMobile(request)
        modelAndView.addObject("mobile", mobile)
        if (username != null) {
            val usernameCookieValue = getUsernameCookie(request)

            val usernameMatchesCookie = username.removeSpaces().toLowerCase() == usernameCookieValue?.toLowerCase()

            val existingUser = LoggedInUsers.getUserByUsername(username)

            if (existingUser?.isExpired == true) {
                LoggedInUsers.userLoggedOut(existingUser)
            }

            if (LoggedInUsers.usernameBeingUsed(username) && !usernameMatchesCookie) {
                modelAndView.addObject("usernameBeingUsed", true)
            } else {
                val user = usernameCookieValue?.let { existingUser } ?: User()
                user.username = username

                KingdomUtil.addUsernameCookieToResponse(username, response)

                if (request.cookies?.firstOrNull { it.name.trim().toLowerCase() == "kingdomadmin" }?.value?.trim()?.toLowerCase() == "changethekingdom") {
                    user.admin = true
                }

                LoggedInUsers.userLoggedIn(user)
                LoggedInUsers.refreshLobbyPlayers()

                val session = request.getSession(true)
                session.maxInactiveInterval = 60 * 120
                session.setAttribute("user", user)
                session.setAttribute("mobile", mobile)

                return if (user.gameId != null) {
                    session.setAttribute("gameId", user.gameId)
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        }
        return modelAndView
    }

    private fun isAccessAllowed(request: HttpServletRequest): Boolean =
            request.cookies?.firstOrNull { it.name.trim().toLowerCase() == "kingdomaccess" }?.value?.trim()?.toLowerCase() == "winner"

    private fun getUsernameCookie(request: HttpServletRequest): String? =
            request.cookies?.firstOrNull { it.name.trim().toLowerCase() == USERNAME_COOKIE }?.value?.trim()

    @RequestMapping("/access.html")
    @Throws(Exception::class)
    fun access(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val accessAllowed = isAccessAllowed(request)
        if (accessAllowed) {
            return KingdomUtil.getLoginModelAndView(request)
        }

        val modelAndView = ModelAndView("access")
        val password = request.getParameter("password")
        val mobile = KingdomUtil.isMobile(request)
        modelAndView.addObject("mobile", mobile)
        if (password != null) {
            if (password.trim().toLowerCase() == "winner") {
                addAccessCookieToResponse(response)

                return KingdomUtil.getLoginModelAndView(request)
            } else {
                modelAndView.addObject("accessDenied", true)
            }
        }
        return modelAndView
    }

    private fun addAccessCookieToResponse(response: HttpServletResponse) {
        val accessCookie = Cookie("kingdomaccess", "winner")
        accessCookie.maxAge = 24 * 60 * 60 * 365 //1 year
        response.addCookie(accessCookie)
    }

    private fun addAdminCookieToResponse(response: HttpServletResponse) {
        val adminCookie = Cookie("kingdomadmin", "changethekingdom")
        adminCookie.maxAge = 24 * 60 * 60 * 365 //1 year
        response.addCookie(adminCookie)
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
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)

        if (!isAdmin(request)) {
            return ModelAndView("adminLogin")
        }
        val modelAndView = ModelAndView("admin")
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
            addAdminCookieToResponse(response)
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
}

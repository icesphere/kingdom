package com.kingdom.web

import com.kingdom.model.User
import com.kingdom.service.GameRoomManager
import com.kingdom.service.LoggedInUsers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.Date

class MainControllerTests {

    @Test
    fun `admin model includes logged in users and active game id`() {
        val controller = MainController(mock(GameRoomManager::class.java))
        val admin = User().apply {
            username = "Admin"
            admin = true
            gameId = "game-1"
        }
        val otherUser = User().apply {
            username = "Player"
            status = "In Lobby"
        }
        val request = MockHttpServletRequest()
        request.getSession(true)!!.setAttribute("user", admin)

        try {
            LoggedInUsers.userLoggedIn(admin)
            LoggedInUsers.userLoggedIn(otherUser)

            val modelAndView = controller.admin(request, MockHttpServletResponse())

            assertEquals("admin", modelAndView.viewName)
            assertEquals("game-1", modelAndView.model["gameId"])
            assertEquals(admin.userId, modelAndView.model["currentUserId"])
            val loggedInUsers = modelAndView.model["loggedInUsers"] as List<*>
            assertTrue(loggedInUsers.contains(admin))
            assertTrue(loggedInUsers.contains(otherUser))
        } finally {
            LoggedInUsers.userLoggedOut(admin)
            LoggedInUsers.userLoggedOut(otherUser)
        }
    }

    @Test
    fun `admin can log out another user`() {
        val controller = MainController(mock(GameRoomManager::class.java))
        val admin = User().apply {
            username = "Admin"
            admin = true
        }
        val otherUser = User().apply {
            username = "Player"
            lastRefresh = Date()
        }
        val request = MockHttpServletRequest()
        request.getSession(true)!!.setAttribute("user", admin)
        request.setParameter("userId", otherUser.userId)

        try {
            LoggedInUsers.userLoggedIn(admin)
            LoggedInUsers.userLoggedIn(otherUser)

            val modelAndView = controller.adminLogoutUser(request, MockHttpServletResponse())

            assertEquals("redirect:/admin.html", modelAndView.viewName)
            assertNull(LoggedInUsers.getUser(otherUser.userId))
            assertTrue(otherUser.refreshLobby.isRedirectToLogin)
            assertEquals(0, otherUser.lastRefresh?.time)
        } finally {
            LoggedInUsers.userLoggedOut(admin)
            LoggedInUsers.userLoggedOut(otherUser)
        }
    }
}

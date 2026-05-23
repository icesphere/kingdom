package com.kingdom.api

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.servlet.http.Cookie

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun accessLoginCatalogAndCreateGame() {
        val accessCookie = mockMvc.perform(post("/api/access")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"password":"winner"}"""))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .getCookie("kingdomaccess") ?: Cookie("kingdomaccess", "winner")

        val loginResult = mockMvc.perform(post("/api/session")
                .cookie(accessCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"username":"api-test-${System.currentTimeMillis()}"}"""))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.userId").exists())
                .andReturn()

        val session = loginResult.request.session as MockHttpSession

        mockMvc.perform(get("/api/catalog").session(session))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.decks[0].cards").isArray)

        mockMvc.perform(post("/api/games")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"title":"API Test","deckNames":["Base"],"numPlayers":2,"numEasyBots":1}"""))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.viewer.username").exists())
                .andExpect(jsonPath("$.kingdomCards").isArray)
    }
}

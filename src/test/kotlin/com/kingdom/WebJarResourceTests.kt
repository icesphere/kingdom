package com.kingdom

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = RANDOM_PORT)
class WebJarResourceTests {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun jqueryWebJarResourceLoads() {
        val request = HttpRequest.newBuilder(URI("http://localhost:$port/webjars/jquery/4.0.0/jquery.min.js"))
            .GET()
            .build()

        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding())

        assertEquals(200, response.statusCode())
    }

    @Test
    fun stompWebSocketWebJarResourceLoads() {
        val request = HttpRequest.newBuilder(URI("http://localhost:$port/webjars/stomp-websocket/2.3.4/stomp.min.js"))
            .GET()
            .build()

        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding())

        assertEquals(200, response.statusCode())
    }
}
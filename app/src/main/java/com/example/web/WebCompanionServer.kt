package com.example.web

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

class WebCompanionServer(private val context: Context, private val port: Int = 8080) {
    private var serverSocket: ServerSocket? = null
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = ServerSocket(port)
                Log.d("WebCompanionServer", "Web portal server listening on port $port")
                while (isRunning) {
                    val client: Socket = serverSocket?.accept() ?: break
                    launch { handleClient(client) }
                }
            } catch (e: Exception) {
                Log.e("WebCompanionServer", "Server exception: ${e.message}")
            }
        }
    }

    private fun handleClient(socket: Socket) {
        try {
            socket.use { s ->
                val reader = BufferedReader(InputStreamReader(s.getInputStream()))
                val out = PrintWriter(s.getOutputStream(), true)
                val line = reader.readLine() ?: return

                if (line.startsWith("GET /api/status")) {
                    val json = """{"status":"ONLINE","platform":"AuralisOS Android Engine","version":"2.6.0"}"""
                    out.print("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${json.toByteArray().size}\r\nConnection: close\r\n\r\n$json")
                    out.flush()
                } else {
                    // Serve index.html
                    val html = context.assets.open("web/index.html").bufferedReader().use { it.readText() }
                    val bytes = html.toByteArray(Charsets.UTF_8)
                    out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n$html")
                    out.flush()
                }
            }
        } catch (e: Exception) {
            // Socket closed
        }
    }

    fun stop() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr.hostAddress?.indexOf(':') == -1) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    fun getPortalUrl(): String = "http://${getLocalIpAddress()}:$port"
}

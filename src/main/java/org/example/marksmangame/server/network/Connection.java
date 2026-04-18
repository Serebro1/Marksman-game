package org.example.marksmangame.server.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class Connection {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public Connection(Socket socket) throws IOException {
        this.socket = socket;

        this.out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true
        );
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
    }

    public synchronized <T> void send(MessageType type, T payload) {
        Message<T> msg = new Message<>(type, payload);
        out.println(gson.toJson(msg));
        if (out.checkError()) {
            throw new RuntimeException("Connection lost");
        }
    }

    public Message<?> read() throws IOException {
        String line = in.readLine();
        if (line == null) return null;

        return gson.fromJson(line, Message.class);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
    public Gson getGson() {
        return gson;
    }
    public boolean isClosed() {
        return socket.isClosed();
    }
}

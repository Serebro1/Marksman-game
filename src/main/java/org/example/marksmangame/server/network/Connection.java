package org.example.marksmangame.server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized void send(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
        out.reset();
    }

    public Object read() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}

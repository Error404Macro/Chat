package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    // клиент - сервер
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream((socket.getInputStream()));
    }

    public void Send(Message message) throws IOException  { // write message in ObjectOutputStream
        synchronized (out) {
            out.writeObject(message);
        }
    }

    public Message receive() throws IOException, ClassNotFoundException { // reading
        synchronized (in) {
            return (Message) in.readObject();
        }
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}

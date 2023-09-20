import client.Connection;
import client.ConsoleHelper;
import client.Message;
import client.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // основной класс сервера
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();


    public static void main(String[] args) throws IOException {
        ConsoleHelper.writeMessage("Введите порт сервера: ");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Чат запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Произошла ошибка. Попробуйте еще раз");
        }

    }

    static void SendBroadcastMessage(Message message) {
        for (Connection connection : connectionMap.values()) { // сообщение -> все соединения
            try {
                connection.Send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Сообщение не отправлено " + connection.getRemoteSocketAddress());
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с " + socket.getRemoteSocketAddress());
            String userName = null;

            try (Connection connection = new Connection(socket)) {
                userName = serverHandshake(connection);
                // message about new participant
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                // participant's message about other participants
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ошибка при обмене данными с " + socket.getRemoteSocketAddress());
            }
            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }
            ConsoleHelper.writeMessage("Соединение с " + socket.getRemoteSocketAddress() + "закрыто");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException { // -> connection -> client name
            while (true) {
                connection.Send(new Message(MessageType.NAME_REQUEST));

                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Получено сообщение от " + socket.getRemoteSocketAddress() + ". Тип сообщения не соответствует протоколу.");
                    continue;
                }

                String userName = message.getData();

                if (userName.isEmpty()) {
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }
                connectionMap.put(userName, connection);

                connection.Send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (name.equals(userName))
                    continue;
                connection.Send(new Message(MessageType.USER_ADDED, name));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String data = message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + data));
                } else {
                    ConsoleHelper.writeMessage("Получено сообщение от " + socket.getRemoteSocketAddress() + ". Тип сообщения не соответствует протоколу");
                }
            }
        }
    }

    public static void sendBroadcastMessage(Message message) {
        // Рассылаем сообщение по всем соединениям
        for (Connection connection : connectionMap.values()) {
            try {
                connection.Send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Не смогли отправить сообщение " + connection.getRemoteSocketAddress());
            }
        }
    }
}



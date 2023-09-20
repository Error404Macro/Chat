package client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    public class BotSockedThread extends SocketThread {
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            String hello = "Привет! Я бот. Ты можешь узнать у меня: дату, день, месяц, год, время, час, минуты и секунды!";
            BotClient.this.sendTextMessage(hello);
            super.clientMainLoop();
        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            // отделяем отправителя сообщения от текста
            String userNameDelimiter = ":";
            String[] split = message.split(userNameDelimiter);
            if (split.length != 2) return;

            String messageWithoutUserName = split[1];

            // формат
            String format = null;
            switch (messageWithoutUserName) {
                case "дата":
                    format = "d.MM.YYYY";
                    break;
                case "день":
                    format = "d";
                    break;
                case "месяц":
                    format = "MMMM";
                    break;
                case "год":
                    format = "YYYY";
                    break;
                case "время":
                    format = "H:mm:ss";
                    break;
                case "час":
                    format = "H";
                    break;
                case "минуты":
                    format = "m";
                    break;
                case "секунды":
                    format = "s";
                    break;
            }
            if(format != null){
                String answer = new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
                BotClient.this.sendTextMessage("По запросу " + split[0] + answer);
            }
        }
    }

    protected SocketThread getSocketThread() {
        return new BotSockedThread();
    }

    protected boolean shouldSendTextFromConsole() { // чтобы бот не отправлял текст введенный в консоль
        return false;
    }

    protected String getUserName() {
        return "bot_" + (int) (Math.random() * 100);
    }

    public static void main(String[] args) {
        Client client = new BotClient();
        client.run();
    }
}

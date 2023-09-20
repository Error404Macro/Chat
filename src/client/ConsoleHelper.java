package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ConsoleHelper {
    private static BufferedReader bR = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        while (true){
            try {
                String buf = bR.readLine();
                if(buf != null)
                    return buf;
            } catch (IOException e){
                writeMessage("Ой... Произошла ошибка при вводе текста. Попробуйте еще раз");
            }
        }
    }
    public static int readInt(){
        while (true){
            try {
                return Integer.parseInt(readString().trim());
            } catch (NumberFormatException e){
                writeMessage("Ой... Произошла ошибка при вводе текста. Попробуйте еще раз");
            }
        }
    }
}

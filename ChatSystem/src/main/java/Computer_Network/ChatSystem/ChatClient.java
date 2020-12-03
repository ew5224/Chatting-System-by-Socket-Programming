package Computer_Network.ChatSystem;

import com.sun.tools.jconsole.JConsoleContext;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
@Getter
public class ChatClient {

    private String name;  //TODO 안 입력했을 때 튕겨나오게
    private String SERVER_IP;
    private int PORT;
    private String password;
    private Socket socket;


    public void connect(String name, String password, String SERVER_IP, int PORT) {
        this.name = name;
        this.password = password;
        this.SERVER_IP = SERVER_IP;
        this.PORT = PORT;

        socket = new Socket();
        try {

            socket.connect(new InetSocketAddress(SERVER_IP, PORT));
            new ChatClientReceiveThread(socket);
            consoleLog("채팅방에 입장하셨습니다.");

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String request = "join :" + name + "\r\n";
            pw.println(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void consoleLog(String log){
        System.out.println(log);
    }

    public void sendMessage(String context){
        PrintWriter pw;
        try{
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),true);
            pw.println(context);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public class ChatClientReceiveThread extends Thread{
        Socket socket;
        ChatClientReceiveThread(Socket socket){
            this.socket =socket;
        }

        public void run(){
            try{
                BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                while(true){
                    String msg = br.readLine();
                    ///textArea.append(msg);
                    ///textArea.append("\n");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}


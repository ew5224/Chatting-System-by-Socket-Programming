package Computer_Network.ChatSystem;

import Computer_Network.ChatSystem.Repository.ChatRepository;
import Computer_Network.ChatSystem.Service.ChatService;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Service
@Getter
@RequiredArgsConstructor
public class ChatClient {

    private String name;  //TODO 안 입력했을 때 튕겨나오게
    private String SERVER_IP;
    private int PORT;
    private String password;
    private int clientid;
    private List<String> chatDAO = new ArrayList<String>();

    public Socket getDatasocket() {
        return datasocket;
    }

    private Socket datasocket;

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;

    public ChatClient(String name, String password, String SERVER_IP, int PORT, int clientid) {
        this.name = name;
        this.password = password;
        this.SERVER_IP = SERVER_IP;
        this.PORT = PORT;
        this.clientid = clientid;
    }


    public void connect() throws IOException {
        ///socket = new Socket();
        socket = new Socket();
        datasocket = new Socket();
        try {
            socket.connect(new InetSocketAddress(SERVER_IP, 10001));
            datasocket.connect(new InetSocketAddress(SERVER_IP, 10002));
            consoleLog("채팅방에 입장하셨습니다.");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String request = "join:" + name + "\r\n";
            pw.println(request);
            new ChatClientReceiveThread(socket).start();
            new ChatClientFileReceiveThread(datasocket).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void consoleLog(String log) {
        System.out.println(log);
    }

    public void sendMessage(String context) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            pw.println(context);
            ///chatRepository.getChatlist().add(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ChatClientReceiveThread extends Thread {
        Socket socket;

        ChatClientReceiveThread(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                while (true) {
                    String msg = br.readLine();
                    System.out.println(msg);
                    chatDAO.add(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ChatClientFileReceiveThread extends Thread {
        Socket socket;

        ChatClientFileReceiveThread(Socket socket) {
            this.socket = socket;
        }
        int chunkSize = 1024;
        byte[] bytes = new byte[chunkSize];

        public void run() {
            try {
                while(true) {
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[chunkSize];
                    int readBytes;
                    while ((readBytes = inputStream.read(buffer)) != -1) {
                        FileOutputStream fos = new FileOutputStream("/Users/roddie/Desktop/Roddie/Chatting-System-by-Socket-Programming/clientfile"+name);
                        fos.write(buffer, 0, readBytes);
                    }
                    ///fos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

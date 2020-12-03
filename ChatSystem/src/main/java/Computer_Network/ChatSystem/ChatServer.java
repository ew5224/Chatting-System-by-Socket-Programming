package Computer_Network.ChatSystem;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    public static final int PORT= 10001;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        List<PrintWriter> listwriters= new ArrayList<>();

        try{
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket = new ServerSocket(10001);
            consoleLog("연결 기다림");

            //3.요청 대기
            while(true){
                Socket socket = serverSocket.accept();
                new ChatServerProcessThread(socket, listwriters).start();
                System.out.println("new Room");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                if(serverSocket!=null && !serverSocket.isClosed()){
                    serverSocket.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    public static void consoleLog(String log){
        System.out.println("[server" + Thread.currentThread().getId() +"]"+log);
    }
}

class ChatServerProcessThread extends Thread{
    private String nickname= null;
    private Socket socket= null;
    List<PrintWriter> listWriters = null;

    public ChatServerProcessThread(Socket socket, List<PrintWriter> listWriters){
        this.socket = socket;
        this.listWriters = listWriters;
    }

    @Override
    public void run(){
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8));

            while(true){
                String request = bufferedReader.readLine();
                if(request ==null){
                    consoleLog("클라이언로부터 연결 끊김");
                    doQuit(printWriter);
                    break;
                }


                String[] tokens = request.split(":");
                System.out.println(request);
                if("join".equals(tokens[0])){
                    doJoin(tokens[1], printWriter);
                }
                else if("message".equals(tokens[0])){
                    doMessage(tokens[1]);
                }
                else if("quit".equals(tokens[0])){
                    doQuit(printWriter);
                }

            }
        }catch(IOException e){
            consoleLog(this.nickname + "님이 채팅방을 나갔습니다");
        }

    }

    private void doMessage(String data){
        broadcast(this.nickname + ":"+ data);
    }

    private void doQuit(PrintWriter writer){
        removeWriter(writer);
        String data = this.nickname + "님이 퇴장하였습니다";
        broadcast(data);
    }

    private void removeWriter(PrintWriter writer){
        synchronized (listWriters){ /// synchrnoized가 뭐야???
            listWriters.remove(writer);
        }
    }

    private void broadcast(String data) {
        synchronized (listWriters) {
            for (PrintWriter writer : listWriters) {
                writer.println(data);
                writer.flush();
            }
        }

    }
    private void doJoin(String nickname, PrintWriter writer){
        this.nickname = nickname;
        String data = nickname + "님이 입장하셨습니다";
        broadcast(data);
        addWriter(writer);
    }

    private void addWriter(PrintWriter writer){
        synchronized (listWriters){
            listWriters.add(writer);
        }
    }

    public static void consoleLog(String log){
        System.out.println(log);
    }
}

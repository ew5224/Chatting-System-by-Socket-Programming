package Computer_Network.ChatSystem;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    static List<PrintWriter> listwriters= new ArrayList<>();
    static List<String> userNames = new ArrayList<>();

    public static final int PORT= 10001;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        ServerSocket dataserverSocket = null;


        try{
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket = new ServerSocket(10001);
            dataserverSocket = new ServerSocket(10002);
            consoleLog("연결 기다림");

            ChatServerProcessThread chatServerProcessThread;

            //3.요청 대기
            while(true){
                Socket socket = serverSocket.accept();
                new ChatServerProcessThread(socket).start();
                Socket datasocket = dataserverSocket.accept();
                new ChatServerProcessThread2(datasocket).start();
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

class ChatServerProcessThread2 extends Thread{
    static List<String> nickname_list= new ArrayList<>();
    private Socket socket= null;
    static List<Socket> socketlist = new ArrayList<>();
    private String nickname;


    public ChatServerProcessThread2(Socket socket){
        this.socket = socket;
        socketlist.add(socket);
        System.out.println(socketlist.size());

    }

    @Override
    public void run(){
        try{
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            //FileOutputStream fos = new FileOutputStream("/Users/roddie/Desktop/Roddie/Chatting-System-by-Socket-Programming/ChatSystem/src/main/resources/newfile.txt");
            System.out.println("ㅎㅇ");
            byte[] buffer = new byte[1024];
            int readBytes;
            while(true){
                readBytes = inputStream.read(buffer);
                if (readBytes != -1) {
                    FileOutputStream fos = new FileOutputStream("/Users/roddie/Desktop/Roddie/Chatting-System-by-Socket-Programming/ChatSystem/src/main/resources/newfile2");
                    fos.write(buffer, 0, readBytes);
                    fos.close();
                    System.out.println(socketlist.size());
                    for(Socket socket : socketlist){
                        OutputStream outputStream2 = socket.getOutputStream();
                        outputStream2.write(buffer,0,readBytes);
                    }

                }
            }
        }catch(IOException e){
            consoleLog(nickname + "님이 채팅방을 나갔습니다2");
        }
    }
    /*
    private void doMessage(String data){
        broadcast(data);
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
        nickname_list.add(nickname);
        String data = nickname + "님이 입장하셨습니다";
        broadcast(data);
        addWriter(writer);
    }

     */


    public static void consoleLog(String log){
        System.out.println(log);
    }
}


class ChatServerProcessThread extends Thread{
    static List<String> nickname_list= new ArrayList<>();
    private Socket socket= null;
    static List<PrintWriter> listWriters = new ArrayList<>();
    private String nickname;


    public ChatServerProcessThread(Socket socket){
        this.socket = socket;
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
                ///System.out.println(request);
                if("join".equals(tokens[0])){
                    System.out.println("join run");
                    nickname =tokens[1];
                    doJoin(tokens[1], printWriter);
                }
                else if("message".equals(tokens[0])){
                    System.out.println("message run");
                    doMessage(tokens[1]);
                }
                else if("quit".equals(tokens[0])) {
                    System.out.println("quit run");
                    doQuit(printWriter);
                }
            }
        }catch(IOException e){
            consoleLog(nickname + "님이 채팅방을 나갔습니다");
        }
    }

    private void doMessage(String data){
        broadcast(data);
    }

    private void doQuit(PrintWriter writer){
        removeWriter(writer);
        String data = this.nickname + "님이 퇴장하였습니다";
        broadcast(data);
    }

    private void removeWriter(PrintWriter writer){
        synchronized (listWriters){
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
        nickname_list.add(nickname);
        String data = nickname + "님이 입장하셨습니다";
        broadcast(data);
        addWriter(writer);
    }

    private void addWriter(PrintWriter writer){
        listWriters.add(writer);
    }

    public static void consoleLog(String log){
        System.out.println(log);
    }
}

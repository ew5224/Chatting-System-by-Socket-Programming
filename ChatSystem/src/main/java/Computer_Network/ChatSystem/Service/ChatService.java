package Computer_Network.ChatSystem.Service;

import Computer_Network.ChatSystem.ChatClient;
import Computer_Network.ChatSystem.Repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository){
        this.chatRepository = chatRepository;
    }

    public void sendMessage(ChatClient chatClient, String context) throws IOException {
        Socket socket = chatClient.getSocket();
        PrintWriter pw;
        try{
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),true);
            pw.println(context);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendFile(ChatClient chatClient,MultipartFile file) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(chatClient.getSERVER_IP(), 10002));
        byte[] bytes = file.getBytes();
        System.out.println(bytes.length);
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "tmpFiles");
        if (!dir.exists())
            dir.mkdirs();

        // Create the file on server
        File serverFile = new File(dir.getAbsolutePath()
                + File.separator + "sendfile");
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bytes);
        outputStream.close();
        socket.close();
        System.out.println("데이터 전송 완료");
    }

    public ArrayList<String> getchatlist(){
        return chatRepository.getChatlist();
    }

    public ArrayList<ChatClient> getclientlist(){
        return chatRepository.getClientlist();
    }

    public void join(ChatClient chatClient){
        chatRepository.getClientlist().add(chatClient);
    }

    public ChatClient findOne(String name) {
        for (ChatClient chatClient : chatRepository.getClientlist()) {
            if (chatClient.getClientid()==(Integer.parseInt(name))) {
                return chatClient;
            }
        }
        return null;
            }
}





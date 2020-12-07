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

    public boolean duplicatecheck(String name){
         if(chatRepository.getIdlist().contains(name)){
             return true;
         }
         else{
             return false;
         }
    }
    public ChatClient login(String id, String password){
        if(id==null){
            return null;
        }
        if(chatRepository.getIdlist().indexOf(id)==-1){
            return new ChatClient();
        }
        if(chatRepository.getPwlist().get(chatRepository.getIdlist().indexOf(id)).equals(password)){
            return findbyName(id);
        }
        else{
            return new ChatClient();
        }
    }

    public void sendFile(ChatClient chatClient,MultipartFile file) throws IOException {
        Socket socket = chatClient.getDatasocket();
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
        System.out.println("데이터 전송 완료");
    }

    public ArrayList<ChatClient> getclientlist(){
        return chatRepository.getClientlist();
    }

    public void join(ChatClient chatClient){
        chatRepository.getClientlist().add(chatClient);
        chatRepository.getIdlist().add(chatClient.getName());
        chatRepository.getPwlist().add(chatClient.getPassword());
    }

    public ChatClient findOne(String name) {
        for (ChatClient chatClient : chatRepository.getClientlist()) {
            if (chatClient.getClientid() == (Integer.parseInt(name))) {
                return chatClient;
            }
        }
        return null;
    }
    public ChatClient findbyName(String name){
        for (ChatClient chatClient : chatRepository.getClientlist()) {
            if (chatClient.getName().equals(name)) {
                return chatClient;
            }
        }
        return null;
    }
    public void logout(ChatClient chatclient){
        chatRepository.getIdlist().remove(chatclient.getName());
        chatRepository.getPwlist().remove(chatclient.getPassword());
    }

}





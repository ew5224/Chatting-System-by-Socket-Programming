package Computer_Network.ChatSystem.Service;

import Computer_Network.ChatSystem.ChatClient;
import Computer_Network.ChatSystem.Repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

    public void sendMessage(ChatClient chatClient, String context){
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





package Computer_Network.ChatSystem.Repository;


import Computer_Network.ChatSystem.ChatClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
@Setter
public class ChatRepository {
    private String id;
    private String password;
    private ArrayList<String> chatlist = new ArrayList<String>();
    private ArrayList<ChatClient> clientlist = new ArrayList<ChatClient>();

}

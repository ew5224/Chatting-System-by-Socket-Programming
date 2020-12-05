package Computer_Network.ChatSystem.Controller;


import Computer_Network.ChatSystem.ChatClient;
import Computer_Network.ChatSystem.ChatServer;
import Computer_Network.ChatSystem.Repository.ChatRepository;
import Computer_Network.ChatSystem.Service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ClientController {


    private final ChatService chatService;
    private int chatclientid=0;
    ///private String name;

    @GetMapping("/home")
    public String createLoginForm(Model model){
        model.addAttribute("form", new ClientLoginForm());
        return "ClientHome";
    }

    @PostMapping("/home")
    public String postMessage(ClientLoginForm clientLoginForm, Model model) throws IOException {
        chatclientid+=100;
        ChatClient chatclient = new ChatClient(clientLoginForm.getName(), clientLoginForm.getPassword(), clientLoginForm.getServerIP(), clientLoginForm.getPort(),chatclientid);
        String name = chatclient.getName();
        chatclient.connect();
        System.out.println("연결 소켓 : " + chatclient.getSocket());
        chatService.join(chatclient);
        model.addAttribute("name",clientLoginForm.getName());
        return "redirect:/clientpage/"+chatclientid;
    }

    @RequestMapping("/clientpage/{chatclientid}")
    public String createChatPage(Model model, @PathVariable String chatclientid){
        System.out.println("마지막에 들어오는 곳은 이곳");
        model.addAttribute("form", new MessageForm());

        ChatClient chatclient = chatService.findOne(chatclientid);
        List<String> chatlist = chatclient.getChatDAO();
        model.addAttribute("chatlist", chatlist);
        return "clientchatpage";
    }

    @PostMapping("/clientpage/{chatclientid}")  ///TODO 채팅화면 + 파일전송 + 로그아웃
    public String sendMessage(MessageForm messageForm, Model model, @PathVariable String chatclientid){
        System.out.println("아니다 이곳이다");
        String context = messageForm.getContext();
        context = "message:"+context;
        ChatClient chatclient = chatService.findOne(chatclientid);
        chatService.sendMessage(chatclient, context);
        List<String> chatlist = chatclient.getChatDAO();
        model.addAttribute("chatlist", chatlist);
        return "redirect:/clientpage/"+chatclientid;
    }


    @GetMapping("/logout")
    public String Logout(Model model){
        ///ChatClient chatclient = chatService.findOne(chatclientid);
        ///chatService.sendMessage("quit");
        return "redirect:/home";
    }
}

package Computer_Network.ChatSystem.Controller;


import Computer_Network.ChatSystem.ChatClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ClientController {

    private final ChatClient chatclient;


    @GetMapping("/home")
    public String createLoginForm(Model model){
        model.addAttribute("form", new ClientLoginForm());
        return "ClientHome";
    }

    @PostMapping("/home")
    public String postMessage(ClientLoginForm clientLoginForm) {
        chatclient.connect(clientLoginForm.getName(), clientLoginForm.getPassword(), clientLoginForm.getServerIP(), clientLoginForm.getPort());
        log.info("여기까지는 들어옴1");
        return "redirect:/clientpage";
    }

    @GetMapping("/clientpage")
    public String createChatPage(Model model){
        model.addAttribute("form", new MessageForm());
        return "clientchatpage";
    }

    @PostMapping("/clientpage")  ///TODO 채팅화면 + 파일전송 + 로그아웃
    public String sendMessage(MessageForm messageForm){
        String context = messageForm.getContext();
        context = chatclient.getName() + " : " + context;
        chatclient.sendMessage(context);
        return "redirect:/clientpage";
    }


    @GetMapping("/logout")
    public String Logout(){
        chatclient.sendMessage("quit");
        return "redirect:/home";
    }
}

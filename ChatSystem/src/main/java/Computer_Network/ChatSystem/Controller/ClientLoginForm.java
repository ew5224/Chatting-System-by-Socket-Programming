package Computer_Network.ChatSystem.Controller;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.websocket.server.ServerEndpoint;

@Getter
@Setter
public class ClientLoginForm {
    private String ServerIP;
    private String password;
    private int Port;
    private String name;
}

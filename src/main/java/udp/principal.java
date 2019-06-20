package udp;


import java.io.IOException;

public class principal {

    public static void main(String[] args) throws IOException {
        udp.Server ser = new udp.Server();
        ser.setVisible(true);
    }
}
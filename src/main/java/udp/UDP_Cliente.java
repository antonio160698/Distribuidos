package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 *
 * @author Cursos
 */
public final class UDP_Cliente {
    String host;
    int port;
    boolean seguir=true;
    DatagramSocket socket = null;
    Sigar sigar = null;
    Server s = null;
    Client c = null;

    /**
     * @param host
     * @param port
     */
    public UDP_Cliente(String host, int port){
        this.host=host;
        this.port=port;
    }

    public UDP_Cliente() {
        iniciar();

    }
    public void iniciar(){
        Thread principal = new Thread(() -> {
            Cliente();

        });
        principal.start();
    }
    public void Cliente() {
        try {
            socket = new DatagramSocket();
            while(true){
                if(!socket.isClosed()){
                    EnvioS();
                    Thread.sleep(4000);
                    Leer();
                    System.out.println("hola1");
                }

            }
        } catch (SocketException ex) {
            Logger.getLogger(UDP_Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UDP_Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void Leer(){
        Thread principal;
        principal = new Thread(() -> {
            try {
                sigar = new Sigar();
                s = new Server();
                c = new Client();
                byte[] bRecibe = new byte[1025];
                InetAddress direccionServidor = InetAddress.getByName(host);
                DatagramPacket packet = new DatagramPacket(bRecibe ,bRecibe.length ,direccionServidor ,port);
                // recibo el saludo
                socket.receive(packet);
                String saludo = new String(packet.getData(), 0 ,packet.getLength());
                NetInterfaceConfig net =sigar.getNetInterfaceConfig(null);
                if(saludo.equals(net.getAddress())){
                    s.setVisible(true);
                    c.dispose();
                }else{
                    s.setVisible(true);
                    c.dispose();
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(UDP_Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | SigarException ex) {
                Logger.getLogger(UDP_Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        principal.start();
    }
    public void EnvioS(){
        Thread principal;
        int numero = (int)(Math.random()*10000)+1000;
        principal = new Thread(() -> {
            try {
                sigar = new Sigar();
                OperatingSystem os;
                ArrayList<String> enviar = new ArrayList<>();
                try{
                    Mem mem = sigar.getMem();
                    os = OperatingSystem.getInstance();
                    CpuInfo cpu[]=sigar.getCpuInfoList();
                    CpuInfo info=cpu[0];
                    NetInterfaceConfig net =sigar.getNetInterfaceConfig(null);
                    NetInfo netInfo;
                    netInfo=sigar.getNetInfo();
                    enviar.add(netInfo.getHostName());//Host
                    enviar.add(os.getDescription());//Descripcion del Equipo
                    enviar.add(os.getName());//Nombre del equipo
                    //enviar.add(os.getDataModel());//Arquitectura
                    enviar.add(os.getVersion());//Version del sistema operativo
                    enviar.add(String.valueOf(mem.getRam()));//Memoria Ram en MB
                    enviar.add(String.valueOf(mem.getTotal()));//Memoria Total MB
                    enviar.add(String.valueOf(mem.getFree()));//Memoria Libre MB
                    long ranking = ((mem.getRam()*1000)+(mem.getTotal()*1000)+(mem.getFree()*1000)+(info.getMhz()*1000)+(info.getTotalCores()*1000));
                    enviar.add(String.valueOf(ranking));
                    enviar.add(info.getVendor());//Nombre del Procesador
                    //enviar.add(info.getModel());//Nombre del Modelo
                    enviar.add(String.valueOf(info.getMhz()));//MegaHz
                    enviar.add(String.valueOf(info.getTotalCores()));//Cantidad de Nucleos


                    enviar.add(net.getAddress());//IP Primaria
                    enviar.add(String.valueOf(numero));
                }catch(SigarException e){
                }
                String arreglo = "";
                arreglo = enviar.stream().map((envi) -> envi+",").reduce(arreglo, String::concat);
                System.out.println(arreglo);
                byte[] bEnviar = arreglo.getBytes();
                // ip del server
                InetAddress direccionServidor = InetAddress.getByName(host);
                //byte[] ip = {127,0,0,1};
                //InetAddress address = InetAddress.getByAddress(ip);
                // paquete de informacion a enviar, ip + port (5432)
                DatagramPacket packet = new DatagramPacket(bEnviar ,bEnviar.length ,direccionServidor ,port);
                // envio el paquete
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(UDP_Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        principal.start();
    }

}


        

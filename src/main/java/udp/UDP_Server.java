/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import udp.principal;

/**
 *
 * @author Cursos
 */
public class UDP_Server {
    private int puerto;
    private String[][] data = new String[100][20];
    private int it = -1;
    private boolean seguir = true;
    Sigar sigar = null;
    OperatingSystem os = null;
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    String saludo = "";
    String [] address = new String [100];
    String [] rank = new String[100];
    int j = 0;

    public UDP_Server(int puerto) {
        this.puerto = puerto;
    }

    public UDP_Server() {

    }

    public void ver_Tabla(JTable table, String[][] data) {
        table.setDefaultRenderer(Object.class, new Render());
        JButton btn_table = new JButton("Salir");
        btn_table.setName("s");
        System.out.println(data);
        DefaultTableModel dtm = new DefaultTableModel(data, new Object[] { "Host", "Descripcion", "Arquitectura",
                "Version", "RAM", "Mem Total", "Mem Libre", "Rank" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(dtm);
        table.setRowHeight(30);
    }

    public void iniciar(JTable table) {
        Thread principal = new Thread(() -> {
            try {
                servidor(table);
            } catch (IOException ex) {
                Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        principal.start();
    }

    public void servidor(JTable table) throws SocketException, IOException {
        try {
            socket = new DatagramSocket(puerto);
            while (true) {
                //if(socket.isConnected()) {
                    datos(table);
                    Leer(table);

                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void EnviarC() {
        try {
            j++;
            System.out.println("la j"+j);
            //Thread principal = new Thread(() -> {
                try {
                    NetInterfaceConfig net = sigar.getNetInterfaceConfig(null);
                } catch (SigarException e) {
                    e.printStackTrace();
                }
                //while (true) {

                    if (j == 20) {
                        System.out.println("Voy a enviar: [" + saludo + "]...");

                        // buffer para enviar saludo
                        byte[] bEnvia = saludo.getBytes();
                        // envio el saludo
                        InetAddress address = packet.getAddress();
                        packet = new DatagramPacket(bEnvia, bEnvia.length, address, packet.getPort());
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Saludo enviado !!");

                        //principal princi = new principal();
                        //princi.server();
                        Client c = new Client();
                        c.iniciar(saludo);
                        c.setVisible(true);
                        Server s = null;
                        try {
                            s = new Server();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //s.setVisible(false);
                        s.dispose();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                //}

            //});

           // principal.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Leer(JTable table) {
        System.out.println("Esperando conexion...");
        it++;
        int hacer_algo;
        while (true) {
            byte[] bRecibe = new byte[1024];
            // recibo el nombre del cliente
            packet = new DatagramPacket(bRecibe, bRecibe.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Conexion recibida !");
            String mensaje = new String(packet.getData());
            // preparo el saludo para enviar
            String strArray[] = mensaje.split(",");
            for (int i = 0; i < strArray.length - 1; i++) {
                data[it][i] = strArray[i];
                System.out.println(strArray[i]);
            }
            hacer_algo = Integer.valueOf(data[it][12]);
            saludo = String.valueOf(data[it][11]);
            rank[it] = String.valueOf(data[it][7]);
            address[it] = data[it][11];
            //programaR pr = new programaR();
            //pr.fibo();

            ver_Tabla(table, data);
            entrando_datos(hacer_algo);
            EnviarC();
        }
    }

    public void datos(JTable table) throws SocketException {

        ArrayList<String> enviar = new ArrayList<>();
        Thread principal = new Thread(() -> {
            sigar = new Sigar();
            try {
                Mem mem = sigar.getMem();
                os = OperatingSystem.getInstance();
                CpuInfo cpu[] = sigar.getCpuInfoList();
                CpuInfo info = cpu[0];
                NetInterfaceConfig net = sigar.getNetInterfaceConfig(null);
                NetInfo netInfo;
                netInfo = sigar.getNetInfo();
                enviar.add(netInfo.getHostName());// Host
                enviar.add(os.getDescription());// Descripcion del Equipo
                enviar.add(os.getName());// Nombre del equipo
                // enviar.add(os.getDataModel());//Arquitectura
                enviar.add(os.getVersion());// Version del sistema operativo
                enviar.add(String.valueOf(mem.getRam()));// Memoria Ram en MB
                enviar.add(String.valueOf(mem.getTotal()));// Memoria Total MB
                enviar.add(String.valueOf(mem.getFree()));// Memoria Libre MB
                long ranking = ((mem.getRam() * 1000) + (mem.getTotal() * 1000) + (mem.getFree() * 1000)
                        + (info.getMhz() * 1000) + (info.getTotalCores() * 1000));
                enviar.add(String.valueOf(ranking));
                enviar.add(info.getVendor());// Nombre del Procesador
                // enviar.add(info.getModel());//Nombre del Modelo
                enviar.add(String.valueOf(info.getMhz()));// MegaHz
                enviar.add(String.valueOf(info.getTotalCores()));// Cantidad de Nucleos
                enviar.add(net.getAddress());// IP Primaria
                rank[it] = String.valueOf(ranking);
                address[it] = net.getAddress();
            } catch (SigarException ex) {
                Logger.getLogger(UDP_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int i = 0; i < enviar.size(); i++) {
                data[it][i] = enviar.get(i).toString();
                System.out.println(enviar.get(i).toString());
            }

            it++;
            ver_Tabla(table, data);

        });
        principal.start();
    }
    public void entrando_datos(int algo){
        Thread principal = new Thread(()->{
            programaR fi = new programaR();
            fi.fibo(algo);
        });
        principal.start();
    }
}

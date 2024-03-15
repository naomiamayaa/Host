import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;

public class Host {
    Thread clientListener, serverListener;
    private static final int CLIENT_PORT = 23;
    private static final int SERVER_PORT = 69;
    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;

    private int iterationCountClient = 0;
    private int iterationCountServer = 0;
    private Box clientBox;
    private Box serverBox;
    private DatagramParser parse = new DatagramParser();
    public Host() {
        try {
            this.clientSocket = new DatagramSocket(CLIENT_PORT);
            this.serverSocket = new DatagramSocket(SERVER_PORT);
            this.clientBox = new Box();
            this.serverBox = new Box();

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

    }

    public void clientListener(){
        //listen for incoming messages from port 23
        //if a message is received, add it to the box and send back confirmation
        clientListener = new Thread(new Runnable() {
            @Override
            public void run() {
                //receive packet and get the port that it came from
                //create a new temporary datagram socket to send it back to the client
                //send the packet back to the client
                while(true){
                    System.out.println("\n\nClient-Host Iteration: \u001b[32m\t" + iterationCountClient + "\u001b[0m");

                    DatagramPacket in, confirm, out, serverResponse;

                    in = new DatagramPacket(new byte[100], 100);

                    // if write request, put in box and send confirmation
                    // if read request, get from box and send back to client

                    try{
                        System.out.println("Host: Waiting for Packet from Client... \n");
                        // Set up the incoming request
                        clientSocket.receive(in);
                        System.out.println("Host: Packet received from Client:");
                        DatagramParser.parseRequest(in);

                        confirm = new DatagramPacket(new byte[0], 0, in.getAddress(), in.getPort());

                        // Send confirmation to client
                        clientSocket.send(confirm); // Send confirmation to client
                        System.out.println("\nHost: Confirmation sent to client");

                        // Add the packet to the box
                        clientBox.put(in);
                        System.out.println("\nHost: Packet added to box");

                        // receive the client request for server reply
                        System.out.println("Host: Waiting for client request... \n");
                        out = new DatagramPacket(new byte[100], 100);

                        clientSocket.receive(out);
                        System.out.println("Host: Packet received from Client:");
                        System.out.println("Client request in bytes: ");

                        for(int i = 0; i < 4; ++i) {
                            System.out.print(String.format("%02X ", out.getData()[i]));
                        }

                        System.out.println("\n\nHost: Taking packet from server box:");
                        // Take the server response from the server Box and send it back to the client
                        serverResponse = (DatagramPacket) serverBox.get();
                        System.out.println("\nHost: Packet taken from server box:");

                        for(int i = 0; i < 4; ++i) {
                            System.out.print(String.format("%02X ", serverResponse.getData()[i]));
                        }

                        DatagramPacket clientResponse = new DatagramPacket(serverResponse.getData(),
                                serverResponse.getLength(), in.getAddress(), in.getPort());

                        clientSocket.send(clientResponse); // Send server reply to Client
                        iterationCountClient++;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }


            }
        });
        clientListener.start();
    }

    public void serverListener(){
        //listen for requests from port 69
        //if a request is received, check if the box is full and send box contents to server.
        //Server sends back confirmation code and puts it in the box
        // host sends back confirmation to server that confirmation code was received and placed in the box
        //
        serverListener = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    while (true) {
                        System.out.println("\n\nServer-Host Iteration: \u001b[32m\t" + iterationCountServer + "\u001b[0m");

                        DatagramPacket request, confirm, out, in;

                        request = new DatagramPacket(new byte[100], 100);

                        // if write request, put in box and send confirmation
                        // if read request, get from box and send back to client

                        try {
                            System.out.println("Host: Waiting for Packet from Server...\n");
                            // Set up the incoming request
                            serverSocket.receive(request);

                            System.out.println("\nHost: Packet received from Server:");

                            System.out.print("Server response in bytes: ");

                            for(int i = 0; i < 4; ++i) {
                                System.out.print(String.format("%02X ", request.getData()[i]));
                            }

                            System.out.println();

                            out = (DatagramPacket) clientBox.get();
                            System.out.println("\nHost: Packet taken from client box:");
                            DatagramParser.parseRequest(out);

                            //copy information from out to new datagram and bind it to the port received from in
                            DatagramPacket clientRequest = new DatagramPacket(out.getData(), out.getLength(), request.getAddress(), request.getPort());

                            serverSocket.send(clientRequest); // Send confirmation to Server
                            System.out.println("\nHost: Client request sent to server");

                            //wait for processed server response and send back confirmation to server

                            in = new DatagramPacket(new byte[4], 4);

                            System.out.println("Host: Waiting for client response from Server... \n");

                            serverSocket.receive(in);
                            System.out.println("Host: Packet received from Server:");
                            System.out.println("Server response in bytes: ");

                            for(int i = 0; i < 4; ++i) {
                                System.out.print(String.format("%02X ", in.getData()[i]));
                            }

                            System.out.println("\n");

                            serverBox.put(in);
                            System.out.println("Host: Packet added to server box");

                            confirm = new DatagramPacket(new byte[0], 0, request.getAddress(), request.getPort());
                            serverSocket.send(confirm); // Send confirmation to Server

                            iterationCountServer++;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        serverListener.start();
    }

    public static void main(String[] args) {
        Host h = new Host();
        h.clientListener();
        h.serverListener();
    }
}

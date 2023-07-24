package kz.knewIt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;

        public Server(int port) {
            this.port = port;
            clients = new ArrayList<>();
            try(ServerSocket serverSocket = new ServerSocket(port))  {
                System.out.println("Сервер запущен на порту "  + port + ". Ожидаем подключение клиента...");
                while (true) {
                    System.out.println("Ждем нового клиента...");
                    Socket socket = serverSocket.accept(); //blocking
                    System.out.println("Клиент подключился");
                    new ClientHandler(this ,socket);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("END");
        }

        public void subscribe(ClientHandler clientHandler) {
            clients.add(clientHandler);
        }

        public void unsubscribe(ClientHandler clientHandler) {
            clients.remove(clientHandler);
        }

        public void broadcastMessage(String message) throws IOException{
            System.out.println(clients);
            for(ClientHandler client : clients) {

                client.sendMessage(message);
            }
        }

        public boolean isNickBusy(String username) {
            for(ClientHandler c : clients) {
                if(c.getUsername().equals(username)) {
                    return true;
                }
            }
            return false;
        }





}

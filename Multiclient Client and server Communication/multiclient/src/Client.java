import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
     private Socket socket;
     private BufferedWriter bufferedWriter;
     private BufferedReader bufferedReader;
     private String username;

     public Client(Socket socket,String user){
         try{
             this.socket = socket;
             this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
             this.username=user;
         }catch (IOException e){
             closeEverything(socket,bufferedReader,bufferedWriter);
         }
     }
     public void sendMessage(){
         try{
             bufferedWriter.write(username);
             bufferedWriter.newLine();
             bufferedWriter.flush();

             Scanner scanner=new Scanner(System.in);
             while(socket.isConnected()){
                 String messageSend=scanner.nextLine();
                 bufferedWriter.write(username+": "+messageSend);
                 bufferedWriter.newLine();
                 bufferedWriter.flush();
             }
         }catch (IOException e){
             closeEverything(socket,bufferedReader,bufferedWriter);
         }
     }
     public void listenforMessage(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 String msgFromchat;

                 while(socket.isConnected()){
                     try{
                         msgFromchat=bufferedReader.readLine();
                         System.out.println(msgFromchat);
                     }catch (IOException e){
                         closeEverything(socket,bufferedReader,bufferedWriter);
                     }
                 }
             }
         }).start();
     }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
     }

     public static void main(String[] args) throws IOException {
         Scanner sc=new Scanner(System.in);
         System.out.println("Ente the Username for the Group chat : ");
         String username=sc.nextLine();
         Socket socket1 = new Socket("localhost",1234);
         Client client = new Client(socket1,username);
         client.listenforMessage();
         client.sendMessage();
     }
}

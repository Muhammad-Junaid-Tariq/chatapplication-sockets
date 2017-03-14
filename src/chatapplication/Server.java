
package chatapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author
 */
public class Server implements Runnable{
        ArrayList clientoutput;
        ArrayList<String> user;
        Socket sock;
        PrintWriter client;
        BufferedReader br;
        public Server(Socket sc,PrintWriter pr,ArrayList clo,ArrayList users){
            try {
                this.sock = sc;
                this.client = pr;
                this.user = users;
                this.clientoutput = clo;
                br = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     public static void main(String[] args)
     {
         
         try 
            {
                ArrayList clo = new ArrayList();
                ArrayList<String> us = new ArrayList();
                ServerSocket serverSock = new ServerSocket(4000);

                while (true) 
                {
                    Socket clientSock = serverSock.accept();
		    PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
		    clo.add(writer);
                    new Thread(new Server(clientSock,writer,clo,us)).start();
		    System.out.println("Got a connection. \n");
                }
            }
            catch (Exception ex)
            {
               System.out.println("Error making a connection. \n"+ex.getMessage());
            }
     }

    @Override
    public void run() {
        String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat" ;
            String[] data;

            try 
            {
                while ((message = br.readLine()) != null) 
                {
                    System.out.println("Received: " + message + "\n");
                    data = message.split(":");
                    
                    for (String token:data) 
                    {
                        System.out.println(token + "\n");
                    }

                    if (data[2].equals(connect)) 
                    {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);
                    } 
                    else if (data[2].equals(disconnect)) 
                    {
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);
                    } 
                    else if (data[2].equals(chat)) 
                    {
                        tellEveryone(message);
                    } 
                    else 
                    {
                        System.err.println("No Conditions were met. \n");
                    }
                } 
             } 
             catch (Exception ex) 
             {
                 System.out.println("i am here1");
                clientoutput.remove(client);
                System.out.println("Lost a connection. \n");
                ex.getMessage();
             }
    }
    public void userAdd (String data)
    {
            String message, add = ": :Connect", done = "Server: :Done", name = data;
            System.out.println("Before " + name + " added. \n");
            user.add(name);
            System.out.println("After " + name + " added. \n");
            String[] tempList = new String[(user.size())];
            user.toArray(tempList);
            for (String token:tempList) 
            {
                message = (token + add);
                tellEveryone(message);
            }
            tellEveryone(done);
    }
        public void userRemove (String data) 
        {
            String message, add = ": :Connect", done = "Server: :Done", name = data;
            user.remove(name);
            String[] tempList = new String[(user.size())];
            user.toArray(tempList);
            System.out.println("i am here");
            for (String token:tempList)
            {
                message = (token + add);
                tellEveryone(message);
            }
            tellEveryone(done);
            System.out.println("I am at end of user remove");
        }
        public void tellEveryone(String message)
        {
            Iterator it = clientoutput.iterator();
            while (it.hasNext()) 
            {
                try 
                {
                    PrintWriter writer = (PrintWriter) it.next();
                    writer.println(message);
		    System.out.println("Sending: " + message + "\n");
                    writer.flush();
                } 
                catch (Exception ex) 
                {
                    System.err.println("Error telling everyone. \n"+ex.getMessage());
                }
            } 
        }
}
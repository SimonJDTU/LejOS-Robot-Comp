import java.net.*;
import java.io.*;

public class TestClient
{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port){
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg){
        out.println(msg);
    }

    public void stopConnection(){
        try {
            in.close();
            out.close();
            clientSocket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public String readMessage()
    {

        String resp = "";
        try{
            while ((resp = in.readLine()) != null)
            {
                return resp;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return resp;
    }
}

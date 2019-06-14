import java.io.*;
import java.net.*;
public class Server_Robot
{

    ServerSocket serversocket;
    Socket socket;
    BufferedReader keyRead;
    OutputStream ostream;
    PrintWriter pwrite;
    InputStream istream;
    BufferedReader receiveRead;
    Robot robot = new Robot();
    String[] InputArray = new String[3];
    public void Connect(int port) {

        try {
            serversocket = new ServerSocket(port);
            System.out.println("Server  ready for chatting");
            socket = serversocket.accept();
            // reading from keyboard (keyRead object)
            keyRead = new BufferedReader(new InputStreamReader(System.in));
            // sending to client (pwrite object)
            ostream = socket.getOutputStream();
            pwrite = new PrintWriter(ostream, true);
            istream = socket.getInputStream();
            receiveRead = new BufferedReader(new InputStreamReader(istream));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void sendMessage(String message)
    {
        pwrite.println(message);
        pwrite.flush();
    }
    public String readMessage()
    {

        String receiveMessage = "";
        try {
            while((receiveMessage = receiveRead.readLine()) != "over")
            {
                InputArray = receiveMessage.split("-");
                if (InputArray[0].equals("1"))
                {
                    System.out.println(receiveMessage);
                    robot.MoveDistanceForwardAB(Integer.parseInt(InputArray[1]));
                    sendMessage("Received: " + receiveMessage + " \n Moving forward");
                }
                else if (InputArray[0].equals("2"))
                {
                    System.out.println(receiveMessage);
                    robot.TurnClockwiseAB(Integer.parseInt(InputArray[1]));
                    sendMessage("Received: " + receiveMessage + " \n Moving forward");
                }
                else if (InputArray[0].equals("3"))
                {
                    System.out.println(receiveMessage);
                    robot.TurnCounterclockwiseAB(Integer.parseInt(InputArray[1]));
                    sendMessage("Received: " + receiveMessage + " \n Moving forward");
                }
                else if (InputArray[0].equals("4"))
                {
                    System.out.println(receiveMessage);
                    robot.ReleaseBallsD();
                    robot.ThrowBallsC(3);
                    robot.StoreBallsC(3);
                    robot.CaptureBallsD();
                    sendMessage("Received: " + receiveMessage + " \n Moving forward");
                }
                else if (InputArray[0].equals("5"))
                {
                    System.out.println(receiveMessage);
                    robot.MoveDistanceBackwardsAB(Integer.parseInt(InputArray[1]));
                    sendMessage("Received " + receiveMessage + " \n Moving Backwards");
                }
                System.out.println(receiveMessage);
                sendMessage("Received: " + receiveMessage + " \n No movement action");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMessage;
    }
}                        
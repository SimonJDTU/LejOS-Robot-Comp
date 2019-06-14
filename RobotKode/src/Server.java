// A Java program for a Server
import java.net.*;
import java.io.*;
import java.text.NumberFormat;

public class Server
{
    //initialize socket and input stream
    private Socket		 socket = null;
    private ServerSocket server = null;
    private DataInputStream in	 = null;
    Robot robot = new Robot();
    String[] InputArray = new String[3];


    // constructor with port
    public Server(int port)
    {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";

            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    InputArray = line.split("-");
                    if (InputArray[0].equals("1"))
                    {
                        System.out.println(line);
                        robot.MoveDistanceForwardAB(Integer.parseInt(InputArray[1]));
                    }
                    else if (InputArray[0].equals("2"))
                    {
                        System.out.println(line);
                        robot.TurnClockwiseAB(Integer.parseInt(InputArray[1]));
                    }
                    else if (InputArray[0].equals("3"))
                    {
                        System.out.println(line);
                        robot.TurnCounterclockwiseAB(Integer.parseInt(InputArray[1]));
                    }
                    else if (InputArray[0].equals("4"))
                    {
                        robot.ReleaseBallsD();
                        robot.ThrowBallsC(3);
                        robot.StoreBallsC(3);
                        robot.CaptureBallsD();
                    }


                }
                catch(NumberFormatException nfe)
                {
                    System.out.print(nfe);
                }
                catch(IOException i)
                {
                    System.out.println(i);
                    System.exit(0);
                }
            }
            System.out.println("Closing connection");

            // close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
            System.exit(0);
        }
    }

    public static void main(String args[])
    {
        Server server = new Server(5000);
    }
}

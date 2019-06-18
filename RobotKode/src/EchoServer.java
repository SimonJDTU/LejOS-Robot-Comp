import java.io.*;
import java.net.*;

public class EchoServer
{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    String[] SplitInput = new String[3];
    Robot robot = new Robot();
    public void start(int port)
    {
        try {
            System.out.println("Waiting for a client");
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            System.out.println("Connected");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                SplitInput = inputLine.split("-");
                if (".".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    out.println("Goodbye");
                    System.out.println("Goodbye");
                    break;
                }
                else if ("1".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten kører fremad
                    if(!SplitInput[1].equals(""))
                    {
                        robot.MoveDistanceForwardAB(Integer.parseInt(SplitInput[1]));
                        out.println("OK");
                    }
                    else
                    {
                        robot.MoveDistanceBackwardsAB(Integer.parseInt(SplitInput[2]));
                        out.println("OK");
                    }


                    //Message to the PC

                }
                else if ("2".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten drejer mod uret

                    robot.TurnClockwiseAB(Integer.parseInt(SplitInput[1]));

                    //Message to the PC
                    out.println("OK");
                }
                else if ("3".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten drejer med uret

                    robot.TurnCounterclockwiseAB(Integer.parseInt(SplitInput[1]));


                    //Message to the PC
                    out.println("OK");
                }
                else if ("4".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten indsamler bolde

                    robot.ReleaseBallsD();
                    robot.StoreBallsC(3);
                    robot.ThrowBallsC(3);
                    robot.CaptureBallsD();



                    out.println("OK");
                }
                else if ("5".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten afleverer bolde


                    robot.CaptureBallsD();


                    out.println("Deliver balls");
                    System.out.println("Deliver balls");
                }
                else if ("6".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten bakker


                    robot.MoveDistanceBackwardsAB(Integer.parseInt(SplitInput[1]));
                    out.println("OK");
                }
                else
                {
                    System.out.println("Received message: " + inputLine);
                    out.println(SplitInput[0]);
                    System.out.println(SplitInput[0]);
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        EchoServer server = new EchoServer();
        server.robot.CaptureBallsD();
        server.start(5000);


    }
}

import java.io.*;
import java.net.*;

public class EchoServer
{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    String[] SplitInput = new String[3];
    //Robot robot = new Robot();
    public void start(int port)
    {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                SplitInput = inputLine.split("-");
                if (".".equals(SplitInput[0]))
                {
                    out.println("Goodbye");
                    System.out.println("Goodbye");
                    break;
                }
                else if ("1".equals(SplitInput[0]))
                {
                    //Robotten kører fremad

                    //robot.MoveDistanceForward((int) SplitInput[1]);

                    //Message to the PC
                    out.println("OK");
                }
                else if ("2".equals(SplitInput[0]))
                {
                    //Robotten drejer mod uret

                    //robot.TurnCounterclockwise((int) SplitInput[1]);

                    //Message to the PC
                    out.println("OK");
                }
                else if ("3".equals(SplitInput[0]))
                {
                    //Robotten drejer med uret

                    //robot.TurnClockwise((int) SplitInput[1]);

                    //Message to the PC
                    out.println("OK");
                }
                else if ("4".equals(SplitInput[0]))
                {
                    //Robotten indsamler bolde

                    //robot.CaptureBallsD();


                    out.println("OK");
                }
                else if ("5".equals(SplitInput[0]))
                {
                    //Robotten afleverer bolde


                    //robot.ReleaseBallsD();
                    //robot.ThrowBallsC(3);
                    //robot.StoreBalls(3);
                    //robot.CaptureBallsD();


                    out.println("Deliver balls");
                    System.out.println("Deliver balls");
                }
                else if ("6".equals(SplitInput[0]))
                {
                    //Robotten bakker


                    //robot.MoveDistanceBackwards((int) SplitInput[1]);
                    out.println("OK");
                }
                else
                {
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
        server.start(4444);

    }
}

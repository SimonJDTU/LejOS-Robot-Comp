import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Stopwatch;

import java.io.*;
import java.net.*;

public class Server
{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String[] SplitInput = new String[3];
    private Robot robot = new Robot();
    private Stopwatch stopwatch = new Stopwatch();
    public void start(int port)
    {
        try {
            System.out.println("Waiting for a client");
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            System.out.println("Connected");
            Sound.setVolume(100);
            Sound.beepSequenceUp();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stopwatch.reset();

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                SplitInput = inputLine.split("-");
                if (".".equals(SplitInput[0]) || stopwatch.elapsed()/1000 >= 480)//480 = 8 minutter
                {
                    out.println("Goodbye");
                    System.out.println("Goodbye");
                    int totaltid = stopwatch.elapsed()/1000;
                    int minutter = totaltid / 60;
                    int sekunder = totaltid - minutter * 60;
                    System.out.println("Tid: " + minutter + "m og " + sekunder + "s");
                    robot.StopRobotD();
                    Sound.setVolume(100);
                    Sound.beepSequenceUp();
                    break;
                }
                else if ("1".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten kører fremad
                    if(!SplitInput[1].equals(""))
                    {
                        robot.MoveDistanceForwardAB(Integer.parseInt(SplitInput[1]), Integer.parseInt(SplitInput[2]));
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

                    robot.TurnClockwiseAB(Integer.parseInt(SplitInput[1]), Integer.parseInt(SplitInput[2]));

                    //Message to the PC
                    out.println("OK");
                }
                else if ("3".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten drejer med uret

                    robot.TurnCounterclockwiseAB(Integer.parseInt(SplitInput[1]), Integer.parseInt(SplitInput[2]));


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

                    robot.CaptureBallsD();
                    robot.MoveDistanceBackwardsAB(Integer.parseInt(SplitInput[1]));
                    robot.CaptureBallsD();
                    out.println("OK");
                }
                else if ("7".equals(SplitInput[0]))
                {
                    System.out.println("Received message: " + inputLine);
                    //Robotten frem med en bestemt hastighed

                    out.println("OK");

                    System.out.println("Received message: " + inputLine);
                    //Robotten kører fremad med bestemt hastighed
                    if(!SplitInput[1].equals(""))
                    {
                        robot.MoveDistanceForwardAB(Integer.parseInt(SplitInput[1], Integer.parseInt(SplitInput[2])));
                        out.println("OK");
                    }
                    else
                    {
                        robot.MoveDistanceBackwardsAB(Integer.parseInt(SplitInput[2]));
                        out.println("OK");
                    }
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

        Server server = new Server();
        server.robot.CaptureBallsD();
        server.start(5000);
        Button.waitForAnyPress();
    }
}

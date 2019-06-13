import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        ComputerVision cv = new ComputerVision();
        Client client = new Client("192.168.43.181", 5000);
        Scanner scanner = new Scanner(System.in);
        boolean gotBall = false;
        boolean goingToGoal = false;
        double[] directions;
       while (true) {
           for(int i = 0; i < 100; i++){
               cv.imageLoop();
           }
           if(gotBall){
               directions = cv.goToGoal();
               goingToGoal = true;
           }else{
               directions = cv.getDirections();
               gotBall = true;
           }

           System.out.println("1ST ROUND length= " + directions[2] + ", angle: " + directions[1]);
            String command = "";
            if(directions[0] == 1){
                //turn left
                command = "3-" + (int) directions[1];
            }else{
                command = "2-" + (int) directions[1];
            }
            client.sendMessage(command);

           for(int i = 0; i < 100; i++){
               cv.imageLoop();
           }
            command = "1-" + (int) ((directions[2]-30)/3.5);
            //command = "1-30";
            client.sendMessage(command);

           for(int i = 0; i < 100; i++){
               cv.imageLoop();
           }

           if(goingToGoal){
                directions = cv.turnToFaceGoal();
                gotBall = false;
                goingToGoal = false;
               if(directions[0] == 1){
                   //turn left
                   command = "3-" + (int) directions[1];
               }else{
                   command = "2-" + (int) directions[1];
               }
               client.sendMessage(command);

               for(int i = 0; i < 100; i++){
                   cv.imageLoop();
               }
               client.sendMessage("4");
            }




        }
    }
}

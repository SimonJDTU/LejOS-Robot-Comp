import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        ComputerVision cv = new ComputerVision();
        Client client = new Client("192.168.43.181", 5000);
        Scanner scanner = new Scanner(System.in);
       while (true) {
           for(int i = 0; i < 10; i++){
               cv.imageLoop();
           }
            cv.imageLoop();
            //double[] directions = cv.getDirections();
            double[] directions = cv.goToGoal();
           System.out.println("1ST ROUND length= " + directions[2] + ", angle: " + directions[1]);
            String command = "";
            if(directions[0] == 1){
                //turn left
                command = "3-" + (int) directions[1];
            }else{
                command = "2-" + (int) directions[1];
            }
            client.sendMessage(command);
            command = "1-" + (int) ((directions[2]-30)/3.5);
            //command = "1-30";
            client.sendMessage(command);
           try {
               Thread.sleep(10000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           cv.imageLoop();
           directions = cv.turnToFaceGoal();
           System.out.println("2ND ROUND length= " + directions[2] + ", angle: " + directions[1]);

           command = "";
           if(directions[0] == 1){
               //turn left
               command = "3-" + (int) directions[1];
           }else{
               command = "2-" + (int) directions[1];
           }
           client.sendMessage(command);
           try {
               Thread.sleep(10000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

            //

           client.sendMessage("4");
            String input = scanner.nextLine();
            if(input.equals("over")){
                client.sendMessage("Over");
            }

        }
    }
}

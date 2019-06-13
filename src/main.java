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
            double[] directions = cv.getDirections();
           System.out.println("length= " + directions[2] + ", angle: " + directions[1]);
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
            String input = scanner.nextLine();
            if(input.equals("over")){
                client.sendMessage("Over");
            }

        }
    }
}

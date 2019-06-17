import java.util.Scanner;

public class TestMain
{
    public static void main(String[] args)
    {
        TestClient client = new TestClient();
        client.startConnection("127.0.0.1", 4444);
        client.sendMessage("1-50");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String msg = client.readMessage();

            if (msg.equals("1")) {
                System.out.println(msg);
            }
            else if (msg.equals("2")) {
                System.out.println(msg);
            }
            else if(msg.equals("3"))
            {
                System.out.println(msg);
            }
            else if(msg.equals("Goodbye"))
            {
                System.out.println(msg);
                break;
            }
            else
            {
                System.out.println(msg);
            }
            System.out.println("Skriv ny besked");
            client.sendMessage(scanner.next());
        }
    }
}

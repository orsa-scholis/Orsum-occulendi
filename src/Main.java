import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args){
        boolean flag = true;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Was wollen sie starten? S = Server, C = Client: ");
            String in = br.readLine();

            while (flag) {
                switch (in) {
                    case "S":
                        flag = false;
                        new server.application.Main().main(new String[]{"-c"});
                        break;

                    case "C":
                        flag = false;
                        new client.application.Main().main(null);
                        break;

                    default:
                        System.out.println("Falsche Eingabe!");
                }
            }
        }
        catch (IOException e){

        }
        finally {
            try {
                br.close();
            } catch (IOException e) {}
        }
    }


}
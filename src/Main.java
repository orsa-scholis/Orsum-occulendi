import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args){
        boolean flag = true;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {

            while (flag) {
            	System.out.println("Was wollen sie starten? S = Server, C = Client, T = Crypto testing: ");
            	String in = br.readLine();
                switch (in) {
                    case "S":
                    case "s":
                        flag = false;
                        new server.application.Main().main(new String[]{"-c"});
                        break;

                    case "C":
                    case "c":
                        flag = false;
                        new client.application.Main().main(null);
                        break;
                        
                    case "T":
                    case "t":
                    	new crypto.cli.CryptoTestingCLI().main();
                    	break;

                    default:
                        System.out.println("Falsche Eingabe!");
                        break;
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
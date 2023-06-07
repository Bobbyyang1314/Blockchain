import com.google.gson.Gson;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * author: Bobby Yang
 * email: zehuay@andrew.cmu.edu
 * reference: project2
 */
public class RemoteVariableClientTCP {

    public static void main(String args[]) throws IOException {
        System.out.println("The client is running.");
        int serverPort = 6789;

        Socket clientSocket = null;
        InetAddress aHost = InetAddress.getByName("localhost");
        clientSocket = new Socket(aHost, serverPort);

        // get reply and send out request server
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

        operation(in, out);
    }
    public static void operation(BufferedReader in, PrintWriter out) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("0. View basic blockchain status.\n" +
                        "1. Add a transaction to the blockchain.\n" + "2. Verify the blockchain.\n"
                        + "3. View the blockchain.\n" + "4. Corrupt the chain.\n" +
                        "5. Hide the corruption by repairing the chain.\n" + "6. Exit.");
                int option = scanner.nextInt(); // Input the option number
                scanner.nextLine();
                // initilize a request message
                RequestMessage requestMessage = null;

                // same as the option in blockchain class
                if (option == 1) {
                    System.out.println("Enter difficulty > 0");
                    int difficulty = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter transaction");
                    String data = scanner.nextLine();
                    requestMessage = new RequestMessage(option, difficulty, 0, data);
                } else if (option == 4) {
                    System.out.println("corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int index = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter new data for block " + index);
                    String newData = scanner.nextLine();
                    requestMessage = new RequestMessage(option, 0, index, newData);
                } else if (option == 6) {
                    break;
                } else {
                    if (option == 3) System.out.println("View the Blockchain");
                    requestMessage = new RequestMessage(option, 0, 0, "");
                }

                Gson gson = new Gson();
                String message = gson.toJson(requestMessage);
                out.println(message);
                out.flush();
                ResponseMessage responseMessage = gson.fromJson(in.readLine(), ResponseMessage.class);

                // print in console
                int op = responseMessage.option;
                if (op == 0) {
                    System.out.println("Current size of chain: " + responseMessage.size);
                    System.out.println("Difficulty of most recent block: " + responseMessage.difficulty);
                    System.out.println("Total difficulty for all blocks: " + responseMessage.totalDifficulties);
                    System.out.println("Approximate hashes per second on this machine: " + responseMessage.hashesPerSecond);
                    System.out.println("Expected total hashes required for the whole chain: " +  responseMessage.totalHashes);
                    System.out.println("Nonce for most recent block: " + responseMessage.latestNonce);
                    System.out.println("Chain hash: " + responseMessage.hashChain);
                } else {
                    System.out.println(responseMessage.message);
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
    }
}
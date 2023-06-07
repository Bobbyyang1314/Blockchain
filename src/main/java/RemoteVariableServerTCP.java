import com.google.gson.Gson;

import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * author: Bobby Yang
 * email: zehuay@andrew.cmu.edu
 * reference: project2
 */

public class RemoteVariableServerTCP {

    public static void main(String args[]) {
        // show the status of server.
        System.out.println("Blockchain server running\n" +
                "We have a visitor");
        Socket clientSocket = null;
        try {
            // creating a BlockChain object
            BlockChain blockChain = new BlockChain();
            // adding the Genesis block to the chain. The Genesis block will be created with an empty string as the pervious hash and a difficulty of 2.
            Block headBlock = new Block(0, blockChain.getTime(), "Genesis", 2);
            headBlock.setPreviousHash("");
            blockChain.addBlock(headBlock);

            // compute the hash time
            blockChain.computeHashesPerSecond();
            int serverPort = 6789; // the server port we are using

            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.

            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */
            while (true) {
                if (!in.hasNextLine()) {
                    clientSocket = listenSocket.accept();
                    in = new Scanner(clientSocket.getInputStream());
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                } else {
                    // Reference: https://github.com/CMU-Heinz-95702/Project3
                    // Create a Gson object
                    Gson gson = new Gson();
                    RequestMessage requestMessage = gson.fromJson(in.nextLine(), RequestMessage.class);

                    // initialize a response message
                    ResponseMessage responseMessage = null;

                    if (requestMessage.option == 0) {
                        // server
                        ResponseMessage responseMessage1 = new ResponseMessage(requestMessage.option, blockChain.getChainSize(),
                                blockChain.getChainHash(), blockChain.getTotalExceptedHashes(), blockChain.getTotalDifficulty(),
                                blockChain.getLatestBlock().getNonce(), blockChain.getLatestBlock().getDifficulty(),
                                blockChain.getHashesPerSecond());
                        String message = gson.toJson(responseMessage1);

                        // client
                        responseMessage = new ResponseMessage(requestMessage.option, blockChain.getChainSize(),
                                blockChain.getChainHash(), blockChain.getTotalExceptedHashes(), blockChain.getTotalDifficulty(),
                                blockChain.getLatestBlock().getNonce(), blockChain.getLatestBlock().getDifficulty(),
                                blockChain.getHashesPerSecond(), message);
                        System.out.println("Response: " + message);

                    } else if (requestMessage.option == 1) { // Adding a Block
                        System.out.println("Adding a block");
                        long startTime = System.currentTimeMillis();
                        blockChain.addBlock(new Block(blockChain.blockList.size(), blockChain.getTime(), requestMessage.data, requestMessage.difficulty));
                        long endTime = System.currentTimeMillis();
                        String message = "Total execution time to add this block was "+ (endTime - startTime) + " milliseconds";
                        responseMessage = new ResponseMessage(requestMessage.option, message);

                        System.out.println("Setting response to "+ message);
                        System.out.println("...{\"selection\": "+ requestMessage.option +",\"response\":\"Total execution time to add this block was "+
                                (endTime - startTime)+" milliseconds\"}");
                    } else if (requestMessage.option == 2) { // Verifying validation
                        System.out.println("Verifying entire chain");
                        long startTime = System.currentTimeMillis();
                        String valid = blockChain.isChainValid();
                        long endTime = System.currentTimeMillis();
                        String message = "Chain verification: " + valid + "\n" + "Total execution time required to verify the chain was "
                                + (endTime - startTime) + " milliseconds";
                        responseMessage = new ResponseMessage(requestMessage.option, message);
                        System.out.println(message);
                        System.out.println("Setting response to " + message);
                    } else if(requestMessage.option == 3){  // view the blockchain
                        System.out.println("View the Blockchain");
                        String response = blockChain.toString();
                        responseMessage = new ResponseMessage(requestMessage.option, response);
                        System.out.println("Setting response to "+response);
                    }else if(requestMessage.option == 4){  // Corrupt the block by specific index
                        System.out.println("Corrupt the Blockchain");
                        blockChain.getBlock(requestMessage.index).setData(requestMessage.data);
                        String response = "Block "+ requestMessage.index + " now holds " + blockChain.getBlock(requestMessage.index).getData();
                        responseMessage = new ResponseMessage(requestMessage.option, response);
                        System.out.println(response);
                        System.out.println("Setting response to "+ response);
                    }else if(requestMessage.option == 5){ // Repairing the blockchain
                        System.out.println("Repairing the entire chain");
                        long startTime = System.currentTimeMillis();
                        blockChain.repairChain();
                        long endTime = System.currentTimeMillis();
                        String response = "Total execution time required to repair the chain was " + (endTime - startTime) +" milliseconds";
                        responseMessage = new ResponseMessage(requestMessage.option, response);
                        System.out.println("Setting response to " + response);
                    }

                    String message = gson.toJson(responseMessage);
                    out.println(message);
                    out.flush();
                }
            }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
}
import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * Author: Bobby Yang
 * Email: zehuay@andrew.cmu.edu
 * Reference: https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/Block.html
 */

public class Block extends Object{

    private int index;
    private Timestamp timestamp;
    private String data;
    private String previousHash;
    private BigInteger nonce;
    private int difficulty;

    /**
     * Constructor
     * @param index
     * @param timestamp
     * @param data
     * @param difficulty
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * @return a String holding Hexadecimal characters
     * @throws NoSuchAlgorithmException
     */
    public String calculateHash() throws NoSuchAlgorithmException {
//  format example: {"index" : 1,"time stamp " : "2022-02-25 17:42:46.053","Tx ": "Mike pays Marty 100 DSCoin","PrevHash" : "0026883909AA470264145129F134489316E6A38439240D0468D69AA9665D4993","nonce" : 165,"difficulty": 2},
        String message = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(message.getBytes());
        String hashMessage = javax.xml.bind.DatatypeConverter.printHexBinary(md.digest());
        return hashMessage;
    }

    /**
     * This method returns the nonce for this block.
     * @return a BigInteger representing the nonce for this block.
     */
    public BigInteger getNonce() {
        return nonce;
    }

    /**
     * The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
     * @return a String with a hash that has the appropriate number of leading hex zeroes. The difficulty value is already in the block. This is the minimum number of hex 0's a proper hash must have.
     * @throws NoSuchAlgorithmException
     */
    public String proofOfWork() throws NoSuchAlgorithmException {
        String res = "";
        nonce = new BigInteger("0");
        while (true) {
            String hashMessage = calculateHash();
            int validCount = 0;
            for (int i = 0; i < difficulty; ++i) {
                if (hashMessage.charAt(i) == '0') validCount++;
                if (validCount == difficulty) return hashMessage;
            }
            nonce = nonce.add(BigInteger.ONE);
        }
    }

    /**
     * Simple getter method
     * @return previous hash
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Simple getter method
     * @return this block's transaction
     */
    public String getData() {
        return data;
    }
    /**
     * Simple getter method
     * @return index of block
     */
    public int getIndex() {
        return index;
    }

    /**
     * Simple getter method
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Simple getter method
     * @return timestamp of this block
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Simple setter method
     * @param previousHash
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Simple setter method
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Simple setter method
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Simple setter method
     * @param difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Simple setter method
     * @param timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
//        Reference: https://github.com/CMU-Heinz-95702/Project3
        // Create a Gson object
        Gson gson = new Gson();
        // Serialize to JSON
        String messageToSend = gson.toJson(this);
        // Display the JSON string
        return messageToSend;
    }

    public static void main(String[] args) {

    }
}

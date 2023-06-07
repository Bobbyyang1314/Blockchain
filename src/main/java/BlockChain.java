import com.google.gson.Gson;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * author: Bobby Yang
 * Email: zehuay@andrew.cmu,edu
 * Reference: https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/BlockChain.html
 */

public class BlockChain extends Object {
    List<Block> blockList;
    String hashChain;
    int hashesPerSecond;

    /**
     * constructor
     */
    public BlockChain() {
        blockList = new ArrayList<>();
        hashChain = "";
        hashesPerSecond = 0;
    }

    /**
     * A new Block is being added to the BlockChain.
     * @param newBlock
     * @throws NoSuchAlgorithmException
     */
    public void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        // pointer to the previousBlock
        newBlock.setPreviousHash(hashChain);
        // hash the chain by the newBlock
        hashChain = newBlock.proofOfWork();
        // add the block to the list
        blockList.add(newBlock);
    }

    /**
     * This method computes exactly 2 million hashes and times how long that process takes.
     */
    public void computeHashesPerSecond() throws NoSuchAlgorithmException {
//        Count time reference url: https://blog.csdn.net/gideal_wang/article/details/4201368
        String hashString = "00000000";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        long startTime = System.currentTimeMillis(); // StartTime
        for (int i = 0; i < 200_0000; ++i) {
            md.update(hashString.getBytes());
            javax.xml.bind.DatatypeConverter.printHexBinary(md.digest());
        }
        long endTime = System.currentTimeMillis(); // endTime
//        reference url: https://blog.csdn.net/xsj_blog/article/details/83029587
        hashesPerSecond = (int) (200_0000 * 1000/ (endTime - startTime));
    }

    /**
     * return block at position i
     * @param i
     * @return block at position i
     */
    public Block getBlock(int i) {
        return blockList.get(i);
    }

    /**
     * Get the hash string of the chain
     * @return the chain hash.
     */
    public String getChainHash() {
        return hashChain;
    }

    /**
     * the size of the chain in blocks.
     * @return the size of the chain in blocks.
     */
    public int getChainSize() {
        return blockList.size();
    }

    /**
     * get hashes per second
     * @return the instance variable approximating the number of hashes per second.
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /**
     * a reference to the most recently added Block.
     * @return a reference to the most recently added Block.
     */
    public Block getLatestBlock() {
        if (!blockList.isEmpty()) return blockList.get(blockList.size() - 1);
        return null;
    }
    public Timestamp getTime() {
//        reference url: https://blog.csdn.net/qq_15037231/article/details/78224321
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return currentTime;
    }


    /**
     * Compute and return the total difficulty of all blocks on the chain. Each block knows its own difficulty.
     * @return totalDifficulty
     */
    public int getTotalDifficulty() {
        int totalDifficulty = 0;
        for (Block block : blockList) totalDifficulty += block.getDifficulty();
        return totalDifficulty;
    }

    /**
     * Compute and return the expected number of hashes required for the entire chain.
     * @return totalExpectedHashes
     */
    public double getTotalExceptedHashes() {
        double totalExpectedHashes = 0;
        for (Block block : blockList) {
            int difficulty = block.getDifficulty();
            double expectedHashes = Math.pow(16, difficulty);
            totalExpectedHashes += expectedHashes;
        }
        return totalExpectedHashes;
    }

    /**
     *
     * @return "TRUE" if the chain is valid, otherwise return a string with an appropriate error message
     * @throws NoSuchAlgorithmException
     */
    public String isChainValid() throws NoSuchAlgorithmException {
        for (int i = 0; i < blockList.size(); ++i) {
            // get the hash of each block
            String hash = blockList.get(i).calculateHash();
            // get the difficulty of each block
            int difficulty = blockList.get(i).getDifficulty();
            String answer = "";
            for (int k = 0; k < difficulty; ++k) answer += "0";
            // checks that the hash has the requisite number of leftmost 0's (proof of work) as specified in the difficulty field.
            if (i == blockList.size() - 1) {
                int left = 0;
                while (left < difficulty) {
                    if (hash.charAt(left) != '0') {
                        System.out.println("Improper hash on node " + i + " Does not begin with " + answer);
                        return "FALSE";
                    }
                    left++;
                }
                // It also checks that the chain hash is equal to this computed hash.
                if (!hash.equals(hashChain)) {
                    System.out.println("The computed hash of " + i + " doesn't match its chain hash");
                    return "FALSE";
                }
            } else {
                //The first check will involve a computation of a hash in Block 0 and a comparison with the hash pointer in Block 1. If they match and if the proof of work is correct, go and visit the next block in the chain.
                Block parent = blockList.get(i + 1);
                if (!hash.equals(parent.getPreviousHash())) {
                    System.out.println("The computed hash of " + i + " doesn't match its parent hash");
                    return "FALSE";
                }
//                At the end, check that the chain hash is also correct.
                int left = 0;
                while (left < difficulty) {
                    if (hash.charAt(left) != '0') {
                        System.out.println("Improper hash on node " + i + " Does not begin with " + answer);
                        return "FALSE";
                    }
                    left++;
                }
            }
        }
        return "TRUE";
    }

    /**
     * This routine repairs the chain.
     * @throws NoSuchAlgorithmException
     */
    public void repairChain() throws NoSuchAlgorithmException {
        for (int i = 0; i < blockList.size(); ++i) {
            Block block = blockList.get(i);
            // get the hash of each block
            String hash = block.calculateHash();
            // get the difficulty of each block
            int difficulty = block.getDifficulty();
            if (i == blockList.size() - 1) {
                int left = 0;
                while (left < difficulty) {
//                    It checks the hashes of each block and ensures that any illegal hashes are recomputed
                    if (hash.charAt(left) != '0') {
                        String repairHash = block.proofOfWork();
                        hashChain = repairHash;
                        break;
                    }
                    left++;
                }
            } else {
                Block parent = blockList.get(i + 1);
                int left = 0;
                while (left < difficulty) {
//                    It checks the hashes of each block and ensures that any illegal hashes are recomputed
                    if (hash.charAt(left) != '0') {
                        String repairHash = block.proofOfWork();
                        parent.setPreviousHash(repairHash);
                        break;
                    }
                    left++;
                }
            }
        }
    }

    /**
     * This method uses the toString method defined on each individual block.
     * @return a String representation of the entire chain is returned.
     */
    @Override
    public String toString() {
        // Reference: https://github.com/CMU-Heinz-95702/Project3
        // Create a Gson object
        Gson gson = new Gson();
        // Serialize to JSON
        String messageToSend = gson.toJson(this);
        // Display the JSON string
        return messageToSend;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // creating a BlockChain object
        BlockChain blockChain = new BlockChain();
        // adding the Genesis block to the chain. The Genesis block will be created with an empty string as the pervious hash and a difficulty of 2.
        blockChain.addBlock(new Block(0, blockChain.getTime(), "Genesis", 2));
        blockChain.getBlock(0).setPreviousHash("");
        // compute the hash time
        blockChain.computeHashesPerSecond();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" + "2. Verify the blockchain.\n"
                    + "3. View the blockchain.\n" + "4. Corrupt the chain.\n" +
                    "5. Hide the corruption by repairing the chain.\n" + "6. Exit.");
            int option = scanner.nextInt();
            if (option == 0) {
                System.out.println("Current size of chain: " + blockChain.getChainSize());
                System.out.println("Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty());
                System.out.println("Total difficulty for all blocks: " + blockChain.getTotalDifficulty());
                System.out.println("Approximate hashes per second on this machine: " + blockChain.getHashesPerSecond());
                System.out.println("Expected total hashes required for the whole chain: " +  blockChain.getTotalExceptedHashes());
                System.out.println("Nonce for most recent block: " + blockChain.getLatestBlock().getNonce());
                System.out.println("Chain hash: " + blockChain.getChainHash());
            } else if (option == 1) {
                // Total execution time to add a block with difficulty == 1 was 15 milliseconds.
                // Total execution time to add a block with difficulty == 2 was 19 milliseconds.
                // Total execution time to add a block with difficulty == 3 was 92 milliseconds.
                // Total execution time to add a block with difficulty == 4 was 236 milliseconds.

                // We can see that difficulty is higher, the time consumed is more
                System.out.println("Enter difficulty > 0");
                int difficulty = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter transaction");
                String data = scanner.nextLine();
                long startTime = System.currentTimeMillis();
                Block block = new Block(blockChain.getChainSize(), blockChain.getTime(),
                        data, difficulty);
                blockChain.addBlock(block);
                long endTime = System.currentTimeMillis();
                System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds.");
            } else if (option == 2) {
                long startTime = System.currentTimeMillis();
                System.out.println("Chain verification: " + blockChain.isChainValid());
                long endTime = System.currentTimeMillis();
                System.out.println("Total execution time to verify the chain was " + (endTime - startTime) + " milliseconds.");
            } else if (option == 3) {
                // display the entire Blockchain contents as a correctly formed JSON document.
                System.out.println("View the Blockchain");
                for (Block block: blockChain.blockList) System.out.println(block.toString());
                System.out.println("chainHash: " + blockChain.hashChain);
            } else if (option == 4) {
                System.out.println("corrupt the Blockchain");
                System.out.println("Enter block ID of block to corrupt");
                int index = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter new data for block " + index);
                String newData = scanner.nextLine();
                blockChain.getBlock(index).setData(newData);
                System.out.println("Block " + index + " now holds " + blockChain.getBlock(index).getData());
            } else if (option == 5) {
                long startTime = System.currentTimeMillis();
                blockChain.repairChain();
                long endTime = System.currentTimeMillis();
                System.out.println("Total execution time required to repair the chain was "
                        + (endTime - startTime) + " milliseconds.");
            } else break;
        }
    }
}

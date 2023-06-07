import java.math.BigInteger;

/**
 * author: Bobby Yang
 * email: zehuay@andrew.cmu.edu
 * ResponseMessage Class
 */
public class ResponseMessage {

    int option;
    int size;
    String hashChain;
    double totalHashes;
    int totalDifficulties;
    BigInteger latestNonce;
    int difficulty;
    int hashesPerSecond;
    String message;

    /**
     * constructor of response message
     * @param option
     * @param size
     * @param hashChain
     * @param totalHashes
     * @param totalDifficulties
     * @param latestNonce
     * @param difficulty
     * @param hashesPerSecond
     * @param message
     */
    public ResponseMessage(int option, int size, String hashChain, double totalHashes, int totalDifficulties,
                           BigInteger latestNonce, int difficulty, int hashesPerSecond, String message) {
        this.difficulty = difficulty;
        this.hashChain = hashChain;
        this.latestNonce = latestNonce;
        this.totalDifficulties = totalDifficulties;
        this.totalHashes = totalHashes;
        this.hashesPerSecond = hashesPerSecond;
        this.option = option;
        this.size = size;
        this.message = message;
    }
    /**
     * constructor of response message
     * @param option
     * @param size
     * @param hashChain
     * @param totalHashes
     * @param totalDifficulties
     * @param latestNonce
     * @param difficulty
     * @param hashesPerSecond
     */
    public ResponseMessage(int option, int size, String hashChain, double totalHashes, int totalDifficulties,
                                 BigInteger latestNonce, int difficulty, int hashesPerSecond) {
        this.difficulty = difficulty;
        this.hashChain = hashChain;
        this.latestNonce = latestNonce;
        this.totalDifficulties = totalDifficulties;
        this.totalHashes = totalHashes;
        this.hashesPerSecond = hashesPerSecond;
        this.option = option;
        this.size = size;
    }

    /**
     * constructor of response message
     * @param option
     * @param message
     */
    public ResponseMessage(int option, String message) {
        this.option = option;
        this.message = message;
    }

}

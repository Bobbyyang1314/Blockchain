/**
 * author: Bobby Yang
 * email: zehuay@andrew.cmu.edu
 * Request message class
 */
public class RequestMessage {
    int option;
    int difficulty;
    int index;
    String data;

    /**
     * constructor
     * @param option
     * @param difficulty
     * @param index
     * @param data
     */
    public RequestMessage(int option, int difficulty, int index, String data) {
        this.option = option;
        this.data = data;
        this.difficulty = difficulty;
        this.index = index;
    }
}

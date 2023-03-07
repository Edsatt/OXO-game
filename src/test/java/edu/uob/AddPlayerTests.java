package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AddPlayerTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(9, 9, 10);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    String generateCommand (int rowIndex, int colIndex){
        char[] row = {'a','b','c','d','e','f','g','h','i'};
        char[] column = {'1','2','3','4','5','6','7','8','9'};
        String rowCommand = String.valueOf(row[rowIndex]);
        String colCommand = String.valueOf(column[colIndex]);
        return rowCommand.concat(colCommand);
    }

    @Test
    void lotsOfPlayers(){
        for(int i=0; i<81; i++){
            model.addPlayer(new OXOPlayer('X'));
        }
        for(int r=0;r<9;r++){
            for(int c=0; c<9; c++){
                sendCommandToController(generateCommand(r,c));
            }
        }
        String failedTestComment1 = "Failed to register draw for 9x9";
        assertTrue(model.isGameDrawn(),failedTestComment1);
        String failedTestComment2 = "Every move should be by a new player";
        int playerNumber=0;
        for(int r=0;r<9;r++) {
            for (int c = 0; c < 9; c++) {
                assertEquals(model.getCellOwner(r,c),model.getPlayerByNumber(playerNumber),failedTestComment2);
                playerNumber++;
            }
        }

    }
}

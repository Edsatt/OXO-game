package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class WinThresholdTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void increaseWinThreshold(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c2"); //p2
        controller.increaseWinThreshold();
        controller.addColumn();
        controller.addRow();
        sendCommandToController("c3"); //p1
        String failedTestComment1 = "Failed to implement increased win threshold";
        assertNull(model.getWinner(), failedTestComment1);
        sendCommandToController("d3"); //p2
        sendCommandToController("d4"); //p1
        String failedTestComment2 = "Failed to register win with increased win threshold";
        assertEquals(player1, model.getWinner(), failedTestComment2);
    }

    @Test
    void decreaseWinThreshold(){
        controller.increaseWinThreshold();
        sendCommandToController("a1");
        controller.decreaseWinThreshold();
        String failedTestComment1 = "Can't decrease win threshold once a game has started";
        assertEquals(4,model.getWinThreshold(),failedTestComment1);
        controller.reset();
        String failedTestComment2 = "Reset shouldn't change win threshold";
        assertEquals(4,model.getWinThreshold(), failedTestComment2);
        controller.decreaseWinThreshold();
        String failedTestComment3 = "Failed to decrease win threshold after a reset";
        assertEquals(3,model.getWinThreshold(), failedTestComment3);
        controller.increaseWinThreshold();
        controller.addColumn();
        controller.addRow();
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c2"); //p2
        sendCommandToController("c3"); //p1
        sendCommandToController("d3"); //p2
        sendCommandToController("d4"); //p1
        controller.decreaseWinThreshold();
        String failedTestComment4 = "Failed to decrease win threshold after a win";
        assertEquals(3,model.getWinThreshold(),failedTestComment4);
    }

    @Test
    void bigWinThreshold(){
        for(int i=0; i<1997; i++){
            controller.increaseWinThreshold();
        }
        String failedTestComment1 = "Failed to increase win threshold to a large number";
        assertEquals(2000,model.getWinThreshold(),failedTestComment1);
        for(int i=0; i<1997; i++){
            controller.decreaseWinThreshold();
        }
        String failedTestComment2 = "Win threshold can't be decreased below 3";
        assertEquals(3,model.getWinThreshold(),failedTestComment2);
    }
}

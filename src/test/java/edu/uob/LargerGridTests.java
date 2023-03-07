package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LargerGridTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(5, 5, 3);
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
    void winInMiddle(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b2");//p1
        sendCommandToController("a1");//p1
        sendCommandToController("c3");//p1
        sendCommandToController("a2");//p1
        sendCommandToController("d4");//p1
        String failedTestComment1 = "failed to register diagonal win in centre";
        assertEquals(player1, model.getWinner(),failedTestComment1);
        controller.reset();
        sendCommandToController("b2");//p1
        sendCommandToController("a1");//p1
        sendCommandToController("b3");//p1
        sendCommandToController("a2");//p1
        sendCommandToController("b4");//p1
        String failedTestComment2 = "failed to register horizontal win in centre";
        assertEquals(player1, model.getWinner(),failedTestComment2);
        controller.reset();
        sendCommandToController("b2");//p1
        sendCommandToController("a1");//p1
        sendCommandToController("c2");//p1
        sendCommandToController("a2");//p1
        sendCommandToController("d2");//p1
        String failedTestComment3 = "failed to register vertical win in centre";
        assertEquals(player1, model.getWinner(),failedTestComment3);
    }
}

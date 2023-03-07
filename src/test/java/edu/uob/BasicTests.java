package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.uob.OXOMoveException.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class BasicTests {
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
    void upperCaseInput(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("A1");
        String failedTestComment1 = "Didn't accept a capital letter input";
        assertEquals(player1,model.getCellOwner(0,0), failedTestComment1);
        OXOPlayer player2 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        String failedTestComment2 = "Player didn't change";
        assertNotEquals(player1,player2, failedTestComment2);
    }

    @Test
    void invalidIdentifierLengthException(){
        String failedTestComment1 = "Failed to throw exception for command longer than 2 chars";
        assertThrows(InvalidIdentifierLengthException.class,()->controller.handleIncomingCommand("aa1"),failedTestComment1);
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1");
        String failedTestComment2 = "After an incorrect input, the player shouldn't change";
        assertEquals(player1,model.getCellOwner(0,0),failedTestComment2);
    }

    @Test
    void nonletterCommandException(){
        String failedTestComment1 = "Failed to throw exception for input 1a";
        assertThrows(InvalidIdentifierCharacterException.class,()->controller.handleIncomingCommand("1a"),failedTestComment1);
        String failedTestComment2 = "Failed to throw exception for input @1";
        assertThrows(InvalidIdentifierCharacterException.class,()->controller.handleIncomingCommand("@1"),failedTestComment2);
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1");
        String failedTestComment3 = "After an incorrect input, the player shouldn't change";
        assertEquals(player1,model.getCellOwner(0,0),failedTestComment3);
    }

    @Test
    void outsideCellRangeException(){
        sendCommandToController("a1");
        sendCommandToController("b2");
        sendCommandToController("a2");
        sendCommandToController("b1");
        controller.removeColumn();
        controller.removeRow();
        String failedTestComment1 = "Can't resize if it makes game drawn";
        assertEquals(3,model.getNumberOfRows(),failedTestComment1);
        String failedTestComment2 = "Failed to throw exception for out of bounds move after resizing";
        assertThrows(OutsideCellRangeException.class,()->controller.handleIncomingCommand("a3"),failedTestComment2);
    }

    @Test
    void occupiedSpaceException(){
        sendCommandToController("a1");
        OXOPlayer player2 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        String failedTestComment1 = "Failed to throw exception for occupied cell";
        assertThrows(CellAlreadyTakenException.class, ()-> controller.handleIncomingCommand("a1"),failedTestComment1);
        String failedTestComment2 = "Player shouldn't change after a failed move";
        assertEquals(player2,model.getPlayerByNumber(model.getCurrentPlayerNumber()),failedTestComment2);
    }

    @Test
    void resetClearBoard(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1");
        sendCommandToController("a3");
        sendCommandToController("b2");
        sendCommandToController("c1");
        sendCommandToController("a2");
        controller.reset();
        String failedTestComment1 = "Board didn't clear";
        for(int r=0; r<3; r++){
            for(int c=0; c<3; c++){
                assertNull(model.getCellOwner(r, c), failedTestComment1);
            }
        }
        String failedTestComment2 = "Reset didn't change current player back to player 1";
        assertEquals(player1,model.getPlayerByNumber(model.getCurrentPlayerNumber()),failedTestComment2);
    }

    @Test
    void resetPlayer(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1");
        sendCommandToController("a3");
        sendCommandToController("b2");
        sendCommandToController("c1");
        sendCommandToController("a2");
        controller.reset();
        String failedTestComment = "Reset didn't change current player back to player 1";
        assertEquals(player1,model.getPlayerByNumber(model.getCurrentPlayerNumber()),failedTestComment);
    }

    @Test
    void resetWinner(){
        sendCommandToController("a1");
        sendCommandToController("b3");
        sendCommandToController("a2");
        sendCommandToController("c1");
        sendCommandToController("a3");
        controller.reset();
        String failedTestComment = "Winner didn't reset";
        assertNull(model.getWinner(), failedTestComment);
    }

    @Test
    void addRow(){
        controller.addRow();
        String failedTestComment1 = "Failed to add a row";
        assertEquals(4, model.getNumberOfRows(), failedTestComment1);
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("d1"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("d2"); //p1
        sendCommandToController("c1"); //p2
        sendCommandToController("d3"); //p1
        String failedTestComment2 = "Failed to register win on new row";
        assertEquals(player1, model.getWinner(), failedTestComment2);

    }

    @Test
    void maxRows(){
        int i=0;
        while(i<8){
            controller.addRow();
            i++;
        }
        String failedTestComment1 = "9 is the maximum number of rows";
        assertEquals(9, model.getNumberOfRows(), failedTestComment1);
        controller.reset();
        String failedTestComment2 = "Reset shouldn't change number of rows";
        assertEquals(9, model.getNumberOfRows(), failedTestComment2);
    }

    @Test
    void removeOccupiedRow(){
        sendCommandToController("c1");
        controller.removeRow();
        String failedTestComment1 = "Can't remove rows which contain a player";
        assertEquals(3, model.getNumberOfRows(), failedTestComment1);
        controller.reset();
        controller.removeRow();
        String failedTestComment2 = "Failed to remove row";
        assertEquals(2, model.getNumberOfRows(), failedTestComment2);
    }

    @Test
    void minRows(){
        int i=0;
        while(i<4){
            controller.removeColumn();
            i++;
        }
        String failedTestComment = "1 is the minimum number of columns";
        assertEquals(1, model.getNumberOfColumns(), failedTestComment);
    }

    @Test
    void addRowAfterWin(){
        sendCommandToController("a1"); //p1
        sendCommandToController("b2"); //p2
        sendCommandToController("a2"); //p1
        sendCommandToController("b3"); //p2
        sendCommandToController("a3"); //p1
        controller.addRow();
        String failedTestComment = "Failed to add a row after a win";
        assertEquals(4,model.getNumberOfRows(), failedTestComment);
    }

    @Test
    void removeRowAfterWin(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c2"); //p2
        sendCommandToController("c3"); //p1
        String failedTestComment1 = "Failed to register winner";
        assertEquals(player1,model.getWinner(), failedTestComment1);
        controller.addRow();
        controller.removeRow();
        String failedTestComment2 = "Failed to remove a row after a win";
        assertEquals(3,model.getNumberOfRows(), failedTestComment2);
    }

    @Test
    void addColumn(){
        controller.addColumn();
        String failedTestComment1 = "Failed to add a column";
        assertEquals(4, model.getNumberOfColumns(), failedTestComment1);
        int i=0;
        while(i<8){
            controller.addColumn();
            i++;
        }
        String failedTestComment2 = "9 is the maximum number of columns";
        assertEquals(9, model.getNumberOfColumns(), failedTestComment2);
        controller.removeColumn();
        String failedTestComment3 = "Failed to remove a column";
        assertEquals(8, model.getNumberOfColumns(), failedTestComment3);
    }

    @Test
    void removeOccupiedColumn(){
        sendCommandToController("a3");
        controller.removeColumn();
        String failedTestComment1 = "Can't remove columns which contain a player";
        assertEquals(3, model.getNumberOfColumns(), failedTestComment1);
        controller.reset();
        String failedTestComment2 = "Reset shouldn't change number of columns";
        assertEquals(3, model.getNumberOfColumns(), failedTestComment2);
        controller.removeColumn();
        String failedTestComment3 = "Failed to remove column after reset";
        assertEquals(2, model.getNumberOfColumns(), failedTestComment3);
    }

    @Test
    void minColumns(){
        int i=0;
        while(i<4){
            controller.removeColumn();
            i++;
        }
        String failedTestComment = "1 is the minimum number of Columns";
        assertEquals(1, model.getNumberOfColumns(), failedTestComment);
    }

    @Test
    void addColumnAfterWin(){
        sendCommandToController("a1"); //p1
        sendCommandToController("b2"); //p2
        sendCommandToController("b1"); //p1
        sendCommandToController("b3"); //p2
        sendCommandToController("c1"); //p1
        controller.addColumn();
        String failedTestComment = "Failed to add column after win";
        assertEquals(4, model.getNumberOfColumns(), failedTestComment);
    }

    @Test
    void removeColumnAfterWin(){
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c2"); //p2
        sendCommandToController("c3"); //p1
        controller.addColumn();
        controller.removeColumn();
        String failedTestComment = "Failed to remove column after win";
        assertEquals(3, model.getNumberOfColumns(), failedTestComment);
    }

    @Test
    void horizontalWin(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //p1
        OXOPlayer player2 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b2"); //p2
        sendCommandToController("a2"); //p1
        sendCommandToController("b3"); //p2
        sendCommandToController("a3"); //p1
        String failedTestComment1 = "Failed to register horizontal win";
        assertEquals(player1, model.getWinner(), failedTestComment1);
        sendCommandToController("b1");
        String failedTestComment2 = "Can't make move after game is won";
        assertNotEquals(player2,model.getCellOwner(1,0), failedTestComment2);
    }

    @Test
    void verticalWin(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //p1
        sendCommandToController("b2"); //p2
        sendCommandToController("b1"); //p1
        sendCommandToController("b3"); //p2
        sendCommandToController("c1"); //p1
        String failedTestComment = "Failed to register vertical win";
        assertEquals(player1, model.getWinner(), failedTestComment);
    }

    @Test
    void leftDiagonalWin(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("b3"); //p2
        sendCommandToController("c3"); //p1
        String failedTestComment = "Failed to register left to right diagonal win";
        assertEquals(player1, model.getWinner(), failedTestComment);
    }

    @Test
    void rightDiagonalWin(){
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("b3"); //p2
        sendCommandToController("c1"); //p1
        String failedTestComment = "Failed to register right to left diagonal win";
        assertEquals(player1, model.getWinner(), failedTestComment);
    }

    @Test
    void drawnGame(){
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c3"); //p2
        sendCommandToController("c2"); //p1
        String failedTestComment = "Failed to register draw";
        assertTrue(model.isGameDrawn(), failedTestComment);
    }

    @Test
    void invalidMoveDrawnGame(){
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c3"); //p2
        assertThrows(CellAlreadyTakenException.class, ()-> controller.handleIncomingCommand("c3")); //p1
        String failedTestComment = "Move 9 was invalid, so this game is not yet drawn";
        assertFalse(model.isGameDrawn(), failedTestComment);
    }

    @Test
    void resetDrawnGame(){
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c3"); //p2
        sendCommandToController("c2"); //p1
        controller.reset();
        String failedTestComment = "Failed to reset drawn state";
        assertFalse(model.isGameDrawn(), failedTestComment);
    }

    @Test
    void wonGameNotDraw(){
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c2"); //p2
        sendCommandToController("c3"); //p1
        String failedTestComment = "A game that is won can't be a draw";
        assertFalse(model.isGameDrawn(), failedTestComment);
    }

    @Test
    void resizeDrawnGame(){
        sendCommandToController("a1"); //p1
        sendCommandToController("a2"); //p2
        sendCommandToController("b2"); //p1
        sendCommandToController("a3"); //p2
        sendCommandToController("b3"); //p1
        sendCommandToController("b1"); //p2
        sendCommandToController("c1"); //p1
        sendCommandToController("c3"); //p2
        sendCommandToController("c2"); //p1
        controller.addRow();
        String failedTestComment1 = "Failed to add a row to a drawn game";
        assertEquals(model.getNumberOfRows(),4, failedTestComment1);
        controller.removeRow();
        controller.addColumn();
        String failedTestComment2 = "Failed to add a column to a drawn game";
        assertEquals(model.getNumberOfColumns(),4, failedTestComment2);
        String failedTestComment3 = "Increasing board size after draw should set draw to false";
        assertFalse(model.isGameDrawn(), failedTestComment3);
    }

    @Test
    void threePlayer(){
        model.addPlayer(new OXOPlayer('C'));
        OXOPlayer player1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1");
        OXOPlayer player2 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b1");
        OXOPlayer player3 = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("c1");
        String failedTestComment1 = "Incorrect cell owner";
        assertEquals(model.getCellOwner(0,0),player1,failedTestComment1);
        assertEquals(model.getCellOwner(1,0),player2,failedTestComment1);
        assertEquals(model.getCellOwner(2,0),player3,failedTestComment1);
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("b3");
        sendCommandToController("a3");
        String failedTestComment2 = "Three player game failed to register winner";
        assertEquals(model.getWinner(),player1,failedTestComment2);
    }
}


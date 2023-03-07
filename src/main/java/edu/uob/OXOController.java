package edu.uob;

import edu.uob.OXOMoveException.*;


public class OXOController {
    OXOModel gameModel;

    private OXOPlayer currentPlayer;

    private int numberOfTurns = 0;
    private final int minSize = 1;
    private final int maxSize = 9;
    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if(gameModel.getWinner()==null){
            validateInput(command);
            int currentPlayerNumber = gameModel.getCurrentPlayerNumber();
            currentPlayer = gameModel.getPlayerByNumber(currentPlayerNumber);
            int rowNumber = toLower(command.codePointAt(0));
            int colNumber = command.codePointAt(1)-'1';
            validateCell(rowNumber, colNumber);
            gameModel.setCellOwner(rowNumber, colNumber, currentPlayer);
            detectWin(rowNumber, colNumber);
            numberOfTurns++;
            detectDraw();
            nextPlayer(currentPlayerNumber);
        }

    }

    private void validateInput(String command) throws OXOMoveException {
        if (command.length() != 2) {
            throw new InvalidIdentifierLengthException(command.length());
        }
        char rowChar = command.charAt(0);
        char colChar = command.charAt(1);
        if(rowChar<'A' || (rowChar>'Z' && rowChar<'a') || rowChar>'z'){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW,rowChar);
        }
        if(colChar<'0' || colChar>'9'){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW,colChar);
        }
    }

    private void validateCell(int rowNumber, int colNumber) throws OXOMoveException{
        int height = gameModel.getNumberOfRows()-1;
        int width = gameModel.getNumberOfColumns()-1;
        if(rowNumber<0 || rowNumber>height){
            throw new OutsideCellRangeException(RowOrColumn.ROW, rowNumber+1);
        }
        else if(colNumber<0 || colNumber>width){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, colNumber+1);
        }
        else if(gameModel.getCellOwner(rowNumber,colNumber)!=null){
            throw new CellAlreadyTakenException(rowNumber+1,colNumber+1);
        }

    }

    public int toLower(int r){
        if(r<='Z') return (r-'A');
        return (r-'a');
    }

    public void nextPlayer(int currentPlayerNumber){
        currentPlayerNumber++;
        int nextPlayerNumber = currentPlayerNumber;
        int numberOfPlayers = gameModel.getNumberOfPlayers();
        if(currentPlayerNumber == numberOfPlayers) nextPlayerNumber=0;
        gameModel.setCurrentPlayerNumber(nextPlayerNumber);
    }

    public void addRow() {
        if(gameModel.getNumberOfRows()<maxSize){
            gameModel.addRow();
            gameModel.setGameDrawn(false);
        }
    }
    public void removeRow() {
        int newGridSize = (gameModel.getNumberOfRows()-1)*gameModel.getNumberOfColumns();
        int rowIndex=gameModel.getNumberOfRows() -1;
        for(int c=0; c< gameModel.getNumberOfColumns(); c++){
            if(gameModel.getCellOwner(rowIndex, c)!=null){
                return;
            }
        }
        if(gameModel.getWinner()!=null){
            gameModel.removeRow();
            return;
        }
        if(gameModel.getNumberOfRows()>minSize && newGridSize!=numberOfTurns){
            gameModel.removeRow();
        }
    }
    public void addColumn() {
        if (gameModel.getNumberOfColumns()<maxSize){
            gameModel.addColumn();
            gameModel.setGameDrawn(false);
        }
    }
    public void removeColumn() {
        int newGridSize = (gameModel.getNumberOfColumns()-1)*gameModel.getNumberOfRows();
        int colIndex=gameModel.getNumberOfColumns() -1;
        for(int r=0; r< gameModel.getNumberOfRows(); r++){
            if(gameModel.getCellOwner(r, colIndex)!=null){
                return;
            }
        }
        if(gameModel.getWinner()!=null){
            gameModel.removeColumn();
            return;
        }
        if(gameModel.getNumberOfColumns()>minSize && newGridSize!=numberOfTurns){
            gameModel.removeColumn();
        }
    }
    public void increaseWinThreshold() {
        int newWinThreshold = gameModel.getWinThreshold()+1;
        gameModel.setWinThreshold(newWinThreshold);
    }
    public void decreaseWinThreshold() {
        int newWinThreshold = gameModel.getWinThreshold()-1;
        if(newWinThreshold<3){
            return;
        }
        if(gameModel.getWinner()!=null || numberOfTurns==0){
            gameModel.setWinThreshold(newWinThreshold);
        }
    }

    public void detectDraw(){
        int width = gameModel.getNumberOfColumns();
        int height = gameModel.getNumberOfRows();
        if((numberOfTurns == (width*height)) && gameModel.getWinner()==null){
            gameModel.setGameDrawn(true);
        }
    }

    public void detectWin(int rowNumber, int columnNumber){
        detectHorizontalWin(rowNumber);
        detectVerticalWin(columnNumber);
        detectLeftDiagonalWin(rowNumber, columnNumber);
        detectRightDiagonalWin(rowNumber, columnNumber);
    }

    public void detectHorizontalWin(int rowNumber){
        int playerCount=0;
        for(int c=0; c< gameModel.getNumberOfColumns(); c++){
            if(gameModel.getCellOwner(rowNumber, c) == currentPlayer){
                playerCount++;
            }
        }
        if(playerCount == gameModel.getWinThreshold()){
            gameWon();
        }
    }

    public void detectVerticalWin(int columnNumber){
        int playerCount=0;
        for(int r=0; r< gameModel.getNumberOfRows(); r++){
            if(gameModel.getCellOwner(r, columnNumber) == currentPlayer){
                playerCount++;
            }
        }
        if(playerCount == gameModel.getWinThreshold()){
            gameWon();
        }
    }

    public void detectLeftDiagonalWin(int rowNumber, int colNumber){
        int count=0;
        int width = gameModel.getNumberOfColumns()-1;
        int height = gameModel.getNumberOfRows()-1;

        while(rowNumber>0 && colNumber>0){
            rowNumber--;
            colNumber--;
        }
        while(rowNumber<=height && colNumber<=width){
            if(gameModel.getCellOwner(rowNumber, colNumber)==currentPlayer){
                count++;
            }
            if(count == gameModel.getWinThreshold()){
                gameWon();
                return;
            }
            rowNumber++;
            colNumber++;
        }
    }

    public void detectRightDiagonalWin(int rowNumber, int colNumber){
        int count=0;
        int width = gameModel.getNumberOfColumns()-1;
        int height = gameModel.getNumberOfRows()-1;

        while(rowNumber>0 && colNumber<width){
            rowNumber--;
            colNumber++;
        }
        while(rowNumber<=height && colNumber>=0){
            if(gameModel.getCellOwner(rowNumber, colNumber)==currentPlayer){
                count++;
            }
            if(count == gameModel.getWinThreshold()){
                gameWon();
                return;
            }
            rowNumber++;
            colNumber--;
        }
    }

    void gameWon(){
        gameModel.setWinner(currentPlayer);
    }

    public void reset() {
        for(int c=0; c<gameModel.getNumberOfColumns(); c++){
            for(int r=0; r<gameModel.getNumberOfRows(); r++){
                gameModel.setCellOwner(r,c,null);
            }
        }
        gameModel.setCurrentPlayerNumber(0);
        gameModel.setWinner(null);
        gameModel.setGameDrawn(false);
        numberOfTurns=0;
    }
}

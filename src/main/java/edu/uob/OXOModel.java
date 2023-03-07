package edu.uob;
import java.util.ArrayList;

public class OXOModel {

    private final ArrayList<ArrayList<OXOPlayer>> cells;
    private final ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        players = new ArrayList<>(2);

        cells = new ArrayList<>(numberOfColumns);

        for(int r=0; r<numberOfRows; r++){
            ArrayList<OXOPlayer> row = new ArrayList<>(numberOfRows);
            for(int c=0; c<numberOfColumns; c++){
                row.add(null);
            }
            cells.add(row);
        }
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        players.add(player);
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public void addRow() {
        ArrayList<OXOPlayer>row = new ArrayList<>(getNumberOfColumns());
        for(int c=0; c< getNumberOfColumns(); c++){
            row.add(null);
        }
        cells.add(row);
    }

    public void removeRow() {
        int rowIndex = getNumberOfRows() -1;
        cells.remove(rowIndex);
    }
    public void addColumn() {
        for(int r=0; r<getNumberOfRows(); r++){
            cells.get(r).add(null);
        }
    }
    public void removeColumn() {
        int columnIndex = getNumberOfColumns() -1;
        for(int r=0; r<getNumberOfRows(); r++){
            cells.get(r).remove(columnIndex);
        }
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber,player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean drawn) {
        gameDrawn = drawn;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}

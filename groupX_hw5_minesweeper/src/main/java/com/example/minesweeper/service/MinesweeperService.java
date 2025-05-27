package com.example.minesweeper.service;

import com.example.minesweeper.model.Cell;
import com.example.minesweeper.model.GameBoard;
import org.springframework.stereotype.Service;

@Service
public class MinesweeperService {
    private GameBoard gameBoard;
    private boolean[][] revealedCells;
    private boolean inPlay = false;
    private GameStage currentState = GameStage.INITIALIZED;
    private boolean lostByMine = false;
    
	//game stages
    public enum GameStage {
    	INITIALIZED, IN_PROGRESS, END_STATE
    }
    
	//startGame
    public void startGame(int rows, int cols, int mines) {
    	startGame();
        if (rows <= 0 || cols <= 0 || mines <= 0 || mines >= (rows * cols)) {
            throw new IllegalArgumentException("Invalid board or mine count.");
        }

        this.gameBoard = new GameBoard(rows, cols, mines);
        this.revealedCells = new boolean[cols][rows];
        this.inPlay = true;
        this.lostByMine = false;
    }

	//get board
    public Cell[][] getBoard() {
        return gameBoard.getBoard();
    }
    
    //handle cell clicked behaviors
    public void clickedCell(int col, int row) {
        if (!inPlay) return;

        Cell clickedCell = gameBoard.getCell(col, row);
        System.out.println("Cell revealed at c: "+ col + " and r: " + row + " isReavealed? " + clickedCell.isRevealed());
        
        //if Cell already flagged or revealed do nothing
        if (clickedCell.isFlagged() || clickedCell.isRevealed()) return;
        
        gameBoard.getCell(col, row).revealing();
        System.out.println("Cell revealed at c: "+ col + " and r: " + row + " isReavealed? " + clickedCell.isRevealed());
        System.out.println("isBomb? " + clickedCell.isMine());
        System.out.println("Identifier? " + clickedCell.getIdentifier());
        revealedCells[col][row] = true;
        
        determineWinFromCell(clickedCell, col, row);
    }

    private void determineWinFromCell(Cell clickedCell, int col, int row) {
        if (clickedCell.getIdentifier() == 9) {
            //hit a mine
            lostByMine = true;
            revealAllCells();
            endGame();
        } else {
            if (clickedCell.getIdentifier() == 0) {
                gameBoard.revealAdjacentCells(col, row);
            }
            if (checkAllNonMine()) {
                System.out.println("Game won");
                lostByMine = false;
                endGame();
            }
        }
    }


	//reveal all cells
    private void revealAllCells() {
        for (int i = 0; i < gameBoard.getCols(); i++) {
            for (int j = 0; j < gameBoard.getRow(); j++) {
                gameBoard.getCell(i, j).revealing();
            }
        }
    }

	//check non-mine cells
    private boolean checkAllNonMine() {
        for (int i = 0; i < gameBoard.getCols(); i++) {
            for (int j = 0; j < gameBoard.getRow(); j++) {
                if (gameBoard.getCell(i, j).getIdentifier() != 9 && !gameBoard.getCell(i, j).isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

	//toggle flag
    public void toggleFlag(int col, int row) {
        if (!inPlay) return;
        
        Cell cell = gameBoard.getCell(col, row);
        if (!cell.isRevealed()) {
            cell.toggleFlag();
        }
        
        //check win condition after flagging
        //if (checkWinByFlags()) {
            //lostByMine = false;
            //endGame();
        //}
    }

    //additional win condition --- all mines are correctly flagged
    private boolean checkWinByFlags() {
        int flaggedMines = 0;
        int totalFlags = 0;
        
        for (int i = 0; i < gameBoard.getCols(); i++) {
            for (int j = 0; j < gameBoard.getRow(); j++) {
                Cell cell = gameBoard.getCell(i, j);
                if (cell.isFlagged()) {
                    totalFlags++;
                    if (cell.getIdentifier() == 9) {
                        flaggedMines++;
                    }
                }
            }
        }
        
        //all mines flagged correctly and nothing else flagged
        return flaggedMines == gameBoard.getTotalMines() && totalFlags == gameBoard.getTotalMines();
    }

	//still in play?
    public boolean isInPlay() {
        return this.inPlay;
    }

	//Cell getter
    public Cell getCell(int row, int col) {
        return gameBoard.getBoard()[col][row];
    }
    
	//row getter
    public int getRows() {
        return gameBoard.getRow();
    }
    
	//column getter
    public int getCols() {
        return gameBoard.getCols();
    }
    
	//current state getter
    public GameStage getCurrentStage() {
    	return currentState;
    }
    
	//isStarted?
    public boolean isStarted() {
    	return currentState == GameStage.INITIALIZED;
    }
    
	//inProgress?
    public boolean isInProgress() {
    	return currentState == GameStage.IN_PROGRESS;
    }
    
	//state is in progress
    public void startGame() {
        currentState = GameStage.IN_PROGRESS;
    }

	//endgame
    public void endGame() {
    	System.out.println("Game Ends");
        currentState = GameStage.END_STATE;
        inPlay = false;
    }
    
	//init game
    public void initGame() {
        currentState = GameStage.INITIALIZED;
        inPlay = false;
        lostByMine = false;
    }

	//is game over?
    public boolean isGameOver() {
        return currentState == GameStage.END_STATE;
    }
    
	//lost by mine?
    public boolean isLostByMine() {
        return lostByMine && isGameOver();
    }
}
//unit testing for MineSweeperService.java class functionality
package com.example.minesweeper.service;

import com.example.minesweeper.model.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class MinesweeperServiceTest {

    private MinesweeperService minesweeperService;

    @BeforeEach
    public void setup() {
        minesweeperService = new MinesweeperService();
    }

    @Test
    @DisplayName("service should initialize with correct state")
    public void testInitialState() {
        assertEquals(MinesweeperService.GameStage.INITIALIZED, minesweeperService.getCurrentStage());
        assertFalse(minesweeperService.isInPlay());
        assertTrue(minesweeperService.isStarted());
        assertFalse(minesweeperService.isInProgress());
        assertFalse(minesweeperService.isGameOver());
    }

    @Test
    @DisplayName("testing gamestate after initialzation / is being played")
    public void testStartGame() {
        minesweeperService.startGame(8, 8, 10); //just w/ "easy" board
        
        assertEquals(MinesweeperService.GameStage.IN_PROGRESS, minesweeperService.getCurrentStage());
        assertTrue(minesweeperService.isInPlay());
        assertFalse(minesweeperService.isStarted());
        assertTrue(minesweeperService.isInProgress());
        assertFalse(minesweeperService.isGameOver());
        
        //making sure rows and cols would be valid
        assertEquals(8, minesweeperService.getRows());
        assertEquals(8, minesweeperService.getCols());
    }
    
    //parameterized tests
    @ParameterizedTest
    @CsvSource({
        "0, 8, 10",     //invalid rows
        "8, 0, 10",     //invalid columns
        "8, 8, 0",      //no bombs
        "8, 8, 65"      //too many bombs
    })

    @DisplayName("service should validate game parameters, see if one is off")
    public void testInvalidGameParameters(int rows, int cols, int mines) {
        assertThrows(IllegalArgumentException.class, () -> {
            minesweeperService.startGame(rows, cols, mines);
        });
    }
    
    
    @Test
    @DisplayName("service should handle cell clicking correctly")
    public void testCellClicking() {
        minesweeperService.startGame(8, 8, 10);
        
        //find a non-mine cell
        Cell[][] board = minesweeperService.getBoard();
        int safeCol = -1, safeRow = -1;
        
        outerloop:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!board[i][j].isMine()) {
                    safeCol = i;
                    safeRow = j;
                    break outerloop;
                }
            }
        }
        
        //to be safe, double check it didn't have a mine
        assertTrue(safeCol >= 0 && safeRow >= 0);
        
        //click it
        assertFalse(board[safeCol][safeRow].isRevealed());
        minesweeperService.clickedCell(safeCol, safeRow);
        assertTrue(board[safeCol][safeRow].isRevealed());
        
        //see if gamestate is still in progress
        assertEquals(MinesweeperService.GameStage.IN_PROGRESS, minesweeperService.getCurrentStage());
    }
    
    @Test 
    @DisplayName("service should handle flagging mechanism/toggling correctly")
    public void testFlagging() {
        minesweeperService.startGame(8, 8, 10);
        
        //flag a cell
        assertFalse(minesweeperService.getCell(0, 0).isFlagged());
        minesweeperService.toggleFlag(0, 0);
        assertTrue(minesweeperService.getCell(0, 0).isFlagged());
        
        //unflag a cell
        minesweeperService.toggleFlag(0, 0);
        assertFalse(minesweeperService.getCell(0, 0).isFlagged());
        
        //cannot flag cells that are already revealed
        minesweeperService.clickedCell(2, 2); // Reveal a cell
        assertTrue(minesweeperService.getCell(2, 2).isRevealed());
        minesweeperService.toggleFlag(2, 2);
        assertFalse(minesweeperService.getCell(2, 2).isFlagged());
    }
    
    @Test
    @DisplayName("service should end game when mine is clicked")
    public void testGameOverByMine() {
        minesweeperService.startGame(8, 8, 10);
        
        //find a mine
        Cell[][] board = minesweeperService.getBoard();
        int mineCol = -1, mineRow = -1;
        
        outerloop:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].isMine()) {
                    mineCol = i;
                    mineRow = j;
                    break outerloop;
                }
            }
        }
        
        //double check that a mine was found at that cell
        assertTrue(mineCol >= 0 && mineRow >= 0);
        
        //click the mine
        minesweeperService.clickedCell(mineCol, mineRow);
        
        //make sure the game ends
        assertEquals(MinesweeperService.GameStage.END_STATE, minesweeperService.getCurrentStage());
        assertTrue(minesweeperService.isGameOver());
        assertTrue(minesweeperService.isLostByMine());
        assertFalse(minesweeperService.isInPlay());
    }
    
    @Test
    @DisplayName("service should stop accepting input after game end")
    public void testNoInputAfterGameEnd() {
        minesweeperService.startGame(8, 8, 10);
        
        //end the game
        minesweeperService.endGame();
        
        //try to clicking cell after it ended
        Cell cell = minesweeperService.getCell(0, 0);
        boolean initialRevealState = cell.isRevealed();
        
        minesweeperService.clickedCell(0, 0);
        
        //make sure clicking it did nothing
        assertEquals(initialRevealState, cell.isRevealed());
        
        //also try to flag a cell
        boolean initialFlagState = cell.isFlagged();
        
        minesweeperService.toggleFlag(0, 0);
        
        //make sure the flag state went unchanged
        assertEquals(initialFlagState, cell.isFlagged());
    }
    
    //end condition checks below

    @Test
    @DisplayName("service should identify win condition")
    public void testWinCondition() {
        // Create a 3x3 board w/ 1 mine
        minesweeperService.startGame(3, 3, 1);
        
        //find the mine
        Cell[][] board = minesweeperService.getBoard();
        int mineCol = -1, mineRow = -1;
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isMine()) {
                    mineCol = i;
                    mineRow = j;
                }
            }
        }
        
        //simulate revealing all non-mine cells
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != mineCol || j != mineRow) {
                    minesweeperService.clickedCell(i, j);
                }
            }
        }
        
        //make sure this caused the game to end and didn't get lost
        assertEquals(MinesweeperService.GameStage.END_STATE, minesweeperService.getCurrentStage());
        assertTrue(minesweeperService.isGameOver());
        assertFalse(minesweeperService.isLostByMine());
    }
    
    @Test
    @DisplayName("service should handle winning by flagging all mines")
    public void testWinByFlags() {
        //create a 3x3 board with 1 mine
        minesweeperService.startGame(3, 3, 1);
        
        //find the mine
        Cell[][] board = minesweeperService.getBoard();
        int mineCol = -1, mineRow = -1;
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isMine()) {
                    mineCol = i;
                    mineRow = j;
                }
            }
        }
        
        //flag it
        minesweeperService.toggleFlag(mineCol, mineRow);
        
        //reveal the non-mine cells
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != mineCol || j != mineRow) {
                    minesweeperService.clickedCell(i, j);
                }
            }
        }
        
        //make sure game was won correctly
        assertEquals(MinesweeperService.GameStage.END_STATE, minesweeperService.getCurrentStage());
        assertTrue(minesweeperService.isGameOver());
        assertFalse(minesweeperService.isLostByMine());
    }
}
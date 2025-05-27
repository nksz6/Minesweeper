//unit testing for GameBoard.java class functionality
package com.example.minesweeper.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    @Test
    @DisplayName("GameBoard should initialize with correct dimensions / initialization test")
    public void testGameBoardInitialization() { 
        //just an "easy" board for refrence - 8x8 w/ 10 bombs
        int rows = 8;
        int cols = 8;
        int mines = 10;
        GameBoard gameBoard = new GameBoard(rows, cols, mines);
        
        assertEquals(rows, gameBoard.getRow());
        assertEquals(cols, gameBoard.getCols());
        assertEquals(mines, gameBoard.getTotalMines());
        
        //check the board loaded the right dimensions
        Cell[][] board = gameBoard.getBoard();
        assertEquals(cols, board.length);
        assertEquals(rows, board[0].length);
    }
    
    @RepeatedTest(5)
    @DisplayName("GameBoard should place the correct number of mines / number of mines placed test")
    public void testMineGeneration() {
        int rows = 10;
        int cols = 10;
        int mines = 15;
        GameBoard gameBoard = new GameBoard(rows, cols, mines);
        
        Cell[][] board = gameBoard.getBoard();
        
        //go through board and count mines
        int countedMines = 0;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (board[i][j].isMine()) {
                    countedMines++;
                }
            }
        }
        assertEquals(mines, countedMines, "board should contain exactly the specified number of mines");
    }
    
    @Test
    @DisplayName("GameBoard should calculate the correct neighbor counts")
    public void testNeighborMineCounts() {
        //creating a 3x3 board, no mines initially bc of the randomization
        GameBoard gameBoard = new GameBoard(3, 3, 0);
        Cell[][] board = gameBoard.getBoard();
        
        //manually place mines at (0,0) and (1,2)
        board[0][0].placeIdentifier(9); //mine at top-left
        board[1][2].placeIdentifier(9); //mine at middle-bottom
        
        //force recalculation of neighbor/adjacent counts
        gameBoard.completeGameBoard();
        
        //check expected neighbor/adjacent counts
        assertEquals(9, board[0][0].getIdentifier()); //mine top left
        assertEquals(2, board[0][1].getIdentifier()); //2 adjacent mines, 1 west and 1 south-east 
        assertEquals(1, board[0][2].getIdentifier()); //1 adjacent mine, south
        assertEquals(1, board[1][0].getIdentifier()); //1 adjacent mine, north
        assertEquals(2, board[1][1].getIdentifier()); //2 adjacent mines, 1 northwest and 1 east 
        assertEquals(9, board[1][2].getIdentifier()); //mine middle right
        assertEquals(0, board[2][0].getIdentifier()); //no adjacent mines from bottom left
        assertEquals(1, board[2][1].getIdentifier()); //1 adjacent mine,  northeast
        assertEquals(1, board[2][2].getIdentifier()); //1 adjacent mine, north
    }
    
    @Test
    @DisplayName("GameBoard should correctly reveal the adjacent cells")
    public void testRevealAdjacentCells() {
        // Create a board with a specific pattern
        GameBoard gameBoard = new GameBoard(4, 4, 0); //startout with no mines
        Cell[][] board = gameBoard.getBoard();
        
        //manually place a mine at (1,1)
        board[1][1].placeIdentifier(9);
        
        //force recalculation
        gameBoard.completeGameBoard();
        
        //make sure all cells are initially hidden/not revealed
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertFalse(board[i][j].isRevealed());
            }
        }
        
        //reveal the cell at (3,3), should have 0 neighboring mines
        gameBoard.revealAdjacentCells(3, 3);
        
        //make sure adjacent cells were revealed accordingly based on 3,3 getting revealed, there should be no other mines by it.
        assertTrue(board[2][2].isRevealed());
        assertTrue(board[2][3].isRevealed());
        assertTrue(board[3][2].isRevealed());
        assertTrue(board[3][3].isRevealed());
        
        //yet, the cells immediately next to that mine shouldnt have been revealed. 
        assertFalse(board[1][1].isRevealed()); // The mine itself
        assertFalse(board[0][0].isRevealed()); // Near the mine
    }
    
    @Test //testing some corner cells that should be valid, along with some that shouldn't exist
    @DisplayName("GameBoard should handle edge cases correctly")
    public void testBoundaryCells() {
        GameBoard gameBoard = new GameBoard(8, 8, 9);
        
        //testing getting cells at the northwestern and southeastern most cells (i.e., 2 of the corners) 
        assertNotNull(gameBoard.getCell(0, 0));
        assertNotNull(gameBoard.getCell(7, 7));
        
        //try getting non-existent cells
        assertNull(gameBoard.getCell(-1, 0));
        assertNull(gameBoard.getCell(0, -1));
        assertNull(gameBoard.getCell(8, 0));
        assertNull(gameBoard.getCell(0, 8));
    }
}
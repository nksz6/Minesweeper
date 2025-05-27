package com.example.minesweeper.model;

import java.util.Random;

public class GameBoard {
	private int rows;
	private int cols;
	private int totalMines;
	private Cell[][] board;
	
	public GameBoard(int rows, int cols, int totalMines) {
		this.rows = rows;
		this.cols = cols;
		this.totalMines = totalMines;
		this.board = new Cell[cols][rows];
		
		// initialize the board with 0 because it is not null
		for (int i = 0; i < cols; i++) {
		    for (int j = 0; j < rows; j++) {
		        board[i][j] = new Cell(0);
		    }
		}
		generateGameBoard();
	}
	
	//generate GameBoard through the Dimension
	private void generateGameBoard() {
		//generate mine field and place those mine
		generateMineField();		
		//place Identifier for non-mine cells.		
		completeGameBoard();		
	}
	
	//place identifiers for the rest of non-mine cells
	public void completeGameBoard() { //just needed to change to public so I can test it, otherwise no touch
		for(int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (board[i][j].getIdentifier() != 9) {
					int count = countNeighborMines(i,j); 
					board[i][j].placeIdentifier(count);
				}
			}
		}		
	}

	//find its neighbor mines
	private int countNeighborMines(int i, int j) {
		int mineNearby = 0;
		for (int k = i-1; k <= i+1; k++) {
			for (int l = j-1; l <= j+1; l++) {
				if (k==i && l == j) { //ignore the Cell we are identifying
					continue;
				}
				else {
					if( inBound(k,l) && board[k][l].getIdentifier()==9) {
						mineNearby++;
					}
				}
			}
		}
		return mineNearby;
	}
	
	//method to check adjacent cells
	public void revealAdjacentCells(int col, int row) {
		for (int i = col-1; i <= col+1; i++) {
			for (int j = row-1; j <= row+1; j++) {
				if (i == col && j == row) {
					continue;
				}
				
				//recursive call for inbound and cell has not been revealed.
				if ((inBound(i, j)) && (!board[i][j].isRevealed() )) {
					board[i][j].revealing();
					// continue until no more
					if (board[i][j].getIdentifier() ==0) {
						revealAdjacentCells(i, j);
					}
					
				}
			}
		}
	}

	//the cell is valid in gameboard
	private boolean inBound(int k, int l) {
		return k>=0 && k < cols && l >= 0 && l <rows;
	}

	//generate minefield and place mine.
	private void generateMineField() {
		Random rand = new Random();
		int minePlaced = 0;
		while (minePlaced < totalMines) {
			int r = rand.nextInt(rows);
			int c = rand.nextInt(cols);
			// Only place a mine if the cell doesn't already have one
			if (board[c][r].getIdentifier() != 9) {
				board[c][r].placeIdentifier(9);
				minePlaced++;
			}
		}
	}

	//cell getter
	public Cell getCell(int col, int row) {
		if (inBound(col, row)) {
	        return board[col][row];
	    }
	    return null;
	}
	
	//return and or display board back to user screen.
	public Cell[][] getBoard() {
		return board;
	}
	
	//row getter
	public int getRow() {
		return rows;
	}
	
	//column getter
	public int getCols() {
		return cols;
	}
	
	//total mines getter
	public int getTotalMines() {
		return totalMines;
	}
}
package com.example.minesweeper.model;

public class Cell {
	int identifier; // 0-8 for safe cells, Use 9 for bomb. 
	boolean flag;
	boolean revealed;
	
	// GameBoard initialize Cell with and id. 
	public Cell(int id) {
		this.identifier = id;
		flag = false;
		revealed = false;
	}
	
	public boolean isFlagged() {
		return this.flag;
	}
	
	public void toggleFlag() {
		this.flag = !this.flag;
	}
	
	public void placeIdentifier(int id) {
		this.identifier = id;
	}
	
	public int getIdentifier() {
		return this.identifier;
	}
	
	public boolean isRevealed() {
		return this.revealed;
	}
	
	public void revealing() {
		this.revealed = true;
	}
	
	public boolean isMine() {
		if (getIdentifier() == 9) {
			return true;
		} //else no bomb
		return false;
	}
}
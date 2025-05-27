//unit testing for Cell.java functionality
package com.example.minesweeper.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellTest {


    //initializing cell tests
    @Test
    public void testCellInitialization() {
        Cell cell = new Cell(0);
        assertEquals(0, cell.getIdentifier());
        assertFalse(cell.isFlagged());
        assertFalse(cell.isRevealed());
        assertFalse(cell.isMine());
    }
    
    //toggling flag tests
    @Test
    public void testToggleFlag() {
        Cell cell = new Cell(0);
        assertFalse(cell.isFlagged());
        
        cell.toggleFlag();
        assertTrue(cell.isFlagged());
        
        cell.toggleFlag();
        assertFalse(cell.isFlagged());
    }
    
    //revealing cell tests
    @Test
    public void testRevealing() {
        Cell cell = new Cell(0);
        assertFalse(cell.isRevealed());
        
        cell.revealing();
        assertTrue(cell.isRevealed());
    }
    
    //identifier placement tests
    @Test
    public void testPlaceIdentifier() {
        Cell cell = new Cell(0);
        assertEquals(0, cell.getIdentifier());
        
        cell.placeIdentifier(5);
        assertEquals(5, cell.getIdentifier());
        
        cell.placeIdentifier(9);
        assertEquals(9, cell.getIdentifier());
        assertTrue(cell.isMine());
    }
    
    //mine identification tests
    @Test
    public void testIsMine() {
        Cell cell1 = new Cell(0);
        assertFalse(cell1.isMine());
        
        Cell cell2 = new Cell(9);
        assertTrue(cell2.isMine());
        
        cell1.placeIdentifier(9);
        assertTrue(cell1.isMine());
    }
}
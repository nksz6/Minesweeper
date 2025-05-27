package com.example.minesweeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.minesweeper.service.MinesweeperService;

@Controller
public class MinesweeperController {
    private final MinesweeperService mineSweeperService;
	//constructor
	public MinesweeperController(MinesweeperService minesweeperService) {
    	this.mineSweeperService = minesweeperService;
    }
	
	@GetMapping("/")
	public String showHomePage() {
		//make sure game state initialized on return home
		mineSweeperService.initGame();
		return "index";
	}
	
	@PostMapping("/game")
	public String handleGameRequest(
	    @RequestParam(required = false) Integer rows,
	    @RequestParam(required = false) Integer cols,
	    @RequestParam(required = false) Integer mines,
	    @RequestParam(required = false) Integer row,
	    @RequestParam(required = false) Integer col,
	    @RequestParam(required = false) String flag,
	    Model model) {
    	
    	//starting a new game with specified dimensions for that difficulty
    	if (mineSweeperService.isStarted()) {
	    	if (rows != null && cols != null && mines != null) {
	    		mineSweeperService.startGame(rows, cols, mines);
	    		System.out.println("Game Started");
	    	}	        
	    }
    	
    	//processing cell click or flag toggle
    	if (mineSweeperService.isInProgress() && row != null && col != null) {
	    	System.out.println("Processing cell at col: " + col + ", row: " + row);
	    	
	    	// Check for flagging
	    	if (flag != null && flag.equals("true")) {
	    		System.out.println("Flagging cell...");
	    		mineSweeperService.toggleFlag(col, row);
	    	} else { //else reveal
	    		System.out.println("Revealing cell...");
	    		mineSweeperService.clickedCell(col, row);
	    	}
	    }
	    
	    if (mineSweeperService.isGameOver()) {
	    	System.out.println("End game");
	    }

	    //add updated Gameboard and State to the Model
	    model.addAttribute("gameboard", mineSweeperService.getBoard());
	    model.addAttribute("rows", mineSweeperService.getRows());
	    model.addAttribute("cols", mineSweeperService.getCols());
	    model.addAttribute("gameState", mineSweeperService.getCurrentStage());
	    model.addAttribute("gameOver", mineSweeperService.isLostByMine());

	    return "game";
	}
}
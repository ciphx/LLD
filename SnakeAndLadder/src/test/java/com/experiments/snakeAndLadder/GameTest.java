package com.experiments.snakeAndLadder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.experiments.snakeAndLadder.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.model.Position;
import com.experiments.snakeAndLadder.model.board.Board;
import com.experiments.snakeAndLadder.model.dice.Dice;

class GameTest {
    private Game game;
    private Board board;
    private Dice dice;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        board = mock(Board.class);
        dice = mock(Dice.class);
        players = Arrays.asList(new Player(0), new Player(1));
        // Initialize players' positions to start position
        players.forEach(player -> player.setPosition(new Position(0)));
        game = new Game("test-game", board, players, dice);
    }

    @Test
    void makeMove_ValidTurn_Succeeds() {
        Position startPosition = new Position(0);
        Position newPosition = new Position(4);
        when(dice.roll()).thenReturn(4);
        when(board.getNextPosition(eq(startPosition), eq(4))).thenReturn(newPosition);
        
        assertTrue(game.makeMove(0));
        assertEquals(4, players.getFirst().getPosition().value());
    }

    @Test
    void makeMove_WinningPosition_EndsGame() {
        Position startPosition = new Position(0);
        Position winPosition = new Position(100);
        when(dice.roll()).thenReturn(6);
        when(board.getNextPosition(eq(startPosition), eq(6))).thenReturn(winPosition);
        when(board.isWinningPosition(winPosition)).thenReturn(true);
        
        assertTrue(game.makeMove(0));
        assertTrue(game.getState().isEnded());
    }

    @Test
    void makeMove_WrongTurn_Fails() {
        assertFalse(game.makeMove(1)); // Player 1 tries to move on Player 0's turn
    }

    @Test
    void makeMove_GameAlreadyEnded_Fails() {
        // First make a winning move to end the game
        Position winPosition = new Position(100);
        when(dice.roll()).thenReturn(6);
        when(board.getNextPosition(any(), eq(6))).thenReturn(winPosition);
        when(board.isWinningPosition(winPosition)).thenReturn(true);
        game.makeMove(0);
        
        // Now try to make another move
        assertFalse(game.makeMove(0));
    }

    @Test
    void makeMove_SnakeEncounter_MovesDown() {
        Position startPosition = new Position(0);
        Position snakePosition = new Position(2);
        when(dice.roll()).thenReturn(4);
        when(board.getNextPosition(eq(startPosition), eq(4))).thenReturn(snakePosition);
        
        assertTrue(game.makeMove(0));
        assertEquals(2, players.getFirst().getPosition().value());
    }

    @Test
    void makeMove_LadderEncounter_MovesUp() {
        Position startPosition = new Position(0);
        Position ladderPosition = new Position(8);
        when(dice.roll()).thenReturn(2);
        when(board.getNextPosition(eq(startPosition), eq(2))).thenReturn(ladderPosition);
        
        assertTrue(game.makeMove(0));
        assertEquals(8, players.getFirst().getPosition().value());
    }

    @Test
    void makeMove_ExceedsBoardSize_StaysInPlace() {
        Position currentPos = new Position(96);
        players.getFirst().setPosition(currentPos);
        
        when(dice.roll()).thenReturn(6);
        when(board.getNextPosition(eq(currentPos), eq(6))).thenReturn(currentPos);
        
        assertTrue(game.makeMove(0));
        assertEquals(96, players.getFirst().getPosition().value());
    }
} 
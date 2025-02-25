package com.experiments.snakeAndLadder.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.experiments.snakeAndLadder.model.Game;
import com.experiments.snakeAndLadder.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.experiments.snakeAndLadder.factory.GameFactory;
import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.repository.PlayerHistoryRepository;

class GameServiceTest {
    private GameService gameService;
    private GameFactory gameFactory;
    private PlayerHistoryRepository playerHistory;
    private Game mockGame;
    private GameState mockGameState;

    @BeforeEach
    void setUp() {
        gameFactory = mock(GameFactory.class);
        playerHistory = mock(PlayerHistoryRepository.class);
        mockGame = mock(Game.class);
        mockGameState = mock(GameState.class);
        when(mockGame.getState()).thenReturn(mockGameState);
        gameService = new GameService(gameFactory, playerHistory);
    }

    @Test
    void createGame_Success() {
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
        when(gameFactory.createGame(100, snakes, ladders, playerIds))
            .thenReturn(mockGame);
        when(mockGame.getId()).thenReturn("game-1");

        String gameId = gameService.createGame(100, snakes, ladders, playerIds);
        assertEquals("game-1", gameId);
        verify(playerHistory).recordGame(playerIds, gameId);
    }

    @Test
    void createGame_PlayerInActiveGame_ThrowsException() {
        when(mockGameState.isEnded()).thenReturn(false);
        when(mockGame.getId()).thenReturn("game-1");
        when(mockGame.getPlayers()).thenReturn(Arrays.asList(new Player(1)));
        
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
        when(gameFactory.createGame(100, snakes, ladders, playerIds))
            .thenReturn(mockGame);
            
        gameService.createGame(100, snakes, ladders, playerIds);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            gameService.createGame(100, snakes, ladders, playerIds));
        assertEquals("A player is already in an ongoing game", exception.getMessage());
    }

    @Test
    void createGame_ConcurrentAccess_ThreadSafe() throws InterruptedException {
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        when(mockGame.getId()).thenReturn("game-1", "game-2");
        when(mockGameState.isEnded()).thenReturn(false);
        when(mockGame.getPlayers()).thenReturn(Arrays.asList(new Player(1)));
        when(gameFactory.createGame(anyInt(), any(), any(), any())).thenReturn(mockGame);

        Thread t1 = new Thread(() -> {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                gameService.createGame(100, snakes, ladders, Arrays.asList(1, 2)));
            assertEquals("A player is already in an ongoing game", exception.getMessage());
        });
        Thread t2 = new Thread(() -> {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                gameService.createGame(100, snakes, ladders, Arrays.asList(1, 3)));
            assertEquals("A player is already in an ongoing game", exception.getMessage());
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        verify(gameFactory, times(1)).createGame(anyInt(), any(), any(), any());
    }

    @Test
    void holdDice_ValidTurn_ReturnsTrue() {
        when(mockGame.getId()).thenReturn("game-1");
        when(mockGameState.isPlayerTurn(1)).thenReturn(true);
        when(mockGameState.isEnded()).thenReturn(false);
        
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
        when(gameFactory.createGame(100, snakes, ladders, playerIds))
            .thenReturn(mockGame);
            
        String gameId = gameService.createGame(100, snakes, ladders, playerIds);
        assertTrue(gameService.holdDice(gameId, 1));
    }

    @Test
    void getPlayerGameHistory_ReturnsHistory() {
        int playerId = 1;
        List<String> expectedHistory = Arrays.asList("game-1", "game-2");
        when(playerHistory.getPlayerHistory(playerId)).thenReturn(expectedHistory);
        
        List<String> actualHistory = gameService.getPlayerGameHistory(playerId);
        assertEquals(expectedHistory, actualHistory);
    }

    @Test
    void getActiveGame_GameNotFound_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            gameService.rollDiceAndMove("nonexistent-game", 1)
        );
        assertEquals("Game not found: nonexistent-game", exception.getMessage());
    }

    @Test
    void rollDiceAndMove_GameEnded_ReturnsFalse() {
        when(mockGame.getId()).thenReturn("game-1");
        when(mockGameState.isEnded()).thenReturn(true);
        when(mockGame.makeMove(1)).thenReturn(false);
        
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
        when(gameFactory.createGame(100, snakes, ladders, playerIds))
            .thenReturn(mockGame);
            
        String gameId = gameService.createGame(100, snakes, ladders, playerIds);
        assertFalse(gameService.rollDiceAndMove(gameId, 1));
    }
} 
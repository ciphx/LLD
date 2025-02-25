package com.experiments.snakeAndLadder.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import static org.mockito.ArgumentMatchers.eq;

import com.experiments.snakeAndLadder.factory.GameFactory;
import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.repository.PlayerHistoryRepository;
import com.experiments.snakeAndLadder.factory.DefaultGameFactory;

class GameServiceTest {
    private GameService gameService;
    private GameFactory gameFactory;
    private PlayerHistoryRepository playerHistory;
    private Game mockGame;
    private GameState mockGameState;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        gameFactory = mock(GameFactory.class);
        playerHistory = mock(PlayerHistoryRepository.class);
        mockGame = mock(Game.class);
        mockGameState = mock(GameState.class);
        
        // Set up common mock behavior
        when(mockGame.getState()).thenReturn(mockGameState);
        when(gameFactory.createGame(eq(100), any(), any(), any())).thenReturn(mockGame);
        
        gameService = new GameService(gameFactory, playerHistory);
    }

    @Test
    void createGame_WithDefaultFactory_Success() {
        // For this test, we want to use the real factory
        gameFactory = new DefaultGameFactory();
        gameService = new GameService(gameFactory, playerHistory);
        
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
        String gameId = gameService.createGame(100, snakes, ladders, playerIds);
        
        assertNotNull(gameId);
        verify(playerHistory).recordGame(playerIds, gameId);
        
        Game createdGame = gameService.getActiveGames().get(gameId);
        assertNotNull(createdGame);
        assertEquals(100, createdGame.getBoard().getSize());
        assertEquals(2, createdGame.getPlayers().size());
    }

    @Test
    void createGame_PlayerInActiveGame_ThrowsException() {
        when(mockGameState.isEnded()).thenReturn(false);
        when(mockGame.getId()).thenReturn("game-1");
        when(mockGame.getPlayers()).thenReturn(Arrays.asList(new Player(1)));
        
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
        // Create first game
        String gameId = gameService.createGame(100, snakes, ladders, playerIds);
        
        // Attempt to create second game
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            gameService.createGame(100, snakes, ladders, playerIds));
        assertEquals("A player is already in an ongoing game", exception.getMessage());
    }

    @Test
    void createGame_ConcurrentAccess_ThreadSafe() throws InterruptedException {
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        
        // Set up initial game state
        when(mockGame.getId()).thenReturn("game-1");
        when(mockGameState.isEnded()).thenReturn(false);
        when(mockGame.getPlayers()).thenReturn(Arrays.asList(new Player(1)));
        
        // Create initial game with player 1
        gameService.createGame(100, snakes, ladders, Arrays.asList(1));

        // Counter for number of exceptions caught
        AtomicInteger exceptionCount = new AtomicInteger(0);

        // Create and run concurrent threads
        Runnable createGameTask = () -> {
            try {
                gameService.createGame(100, snakes, ladders, Arrays.asList(1, 2));
            } catch (IllegalStateException e) {
                if (e.getMessage().equals("A player is already in an ongoing game")) {
                    exceptionCount.incrementAndGet();
                }
            }
        };

        Thread t1 = new Thread(createGameTask);
        Thread t2 = new Thread(createGameTask);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Verify that both threads received the exception
        assertEquals(2, exceptionCount.get());
        // Verify that only the initial game was created
        verify(gameFactory, times(1)).createGame(eq(100), any(), any(), any());
    }

    @Test
    void holdDice_ValidTurn_ReturnsTrue() {
        when(mockGame.getId()).thenReturn("game-1");
        when(mockGameState.isPlayerTurn(1)).thenReturn(true);
        when(mockGameState.isEnded()).thenReturn(false);
        
        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        List<Integer> playerIds = Arrays.asList(1, 2);
        
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
        
        String gameId = gameService.createGame(100, snakes, ladders, playerIds);
        assertFalse(gameService.rollDiceAndMove(gameId, 1));
    }
} 
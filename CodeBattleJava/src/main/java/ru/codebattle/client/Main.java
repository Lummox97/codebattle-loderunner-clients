package ru.codebattle.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.*;
import ru.codebattle.client.api.BoardPoint;
import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.LoderunnerAction;

public class Main {

    private static final String SERVER_ADDRESS = "codingdojo2019.westeurope.cloudapp.azure.com";
    private static final String PLAYER_NAME = "lh7i9thl7uv4evdnd1k5";
    private static final String AUTH_CODE = "6296115112114097073";

    public static void main(String[] args) throws URISyntaxException, IOException {
        LodeRunnerClient client = new LodeRunnerClient(SERVER_ADDRESS, PLAYER_NAME, AUTH_CODE);
        
        client.setShouldExit(false);
        client.run(gameBoard -> {
            var gold = gameBoard.getGoldPositions();
            var me = gameBoard.getMyPosition();
            BoardElement[] pits = {BoardElement.PIT_FILL_1,
                    BoardElement.PIT_FILL_2, BoardElement.PIT_FILL_3,
                    BoardElement.PIT_FILL_4, BoardElement.NONE};
            
            return gameBoard.getGoldPositions().stream()
                .filter(coin -> (coin.getY() == me.getY()) && ((coin.getX() - me.getX() < 3) || (coin.getX() - me.getX() > -3)))
                .min(Comparator.comparingInt(a -> a.getX() - me.getX()))
                .map(coin -> {
                    if (coin.getX() - me.getX() > 0)
                        return LoderunnerAction.GO_RIGHT;
                    else
                        return LoderunnerAction.GO_LEFT;
                }).orElse((gameBoard.hasElementAt(
                        me.shiftBottom().shiftLeft(), pits)) ?
                        LoderunnerAction.GO_LEFT :
                    (gameBoard.hasElementAt(
                        me.shiftBottom().shiftRight(), pits)) ?
                        LoderunnerAction.GO_RIGHT :
                    LoderunnerAction.DRILL_RIGHT );
        });
        System.in.read();

        client.initiateExit();
    }
}

package ru.codebattle.client.api;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

@Slf4j
public abstract class LoderunnerBase extends WebSocketClient {
    private final String responsePrefix = "board=";

    public LoderunnerBase(String serverAddress, String user, String code) throws URISyntaxException {
        super(new URI(format("ws://%s/codenjoy-contest/ws?user=%s&code=%s", serverAddress, user, code)));
    }

    @Setter
    @Getter
    private boolean shouldExit;

    protected abstract String doMove(GameBoard gameBoard);

    protected static String loderunnerActionToString(LoderunnerAction action) {
        switch (action) {
            case GO_LEFT:
                return "left";
            case GO_RIGHT:
                return "right";
            case GO_UP:
                return "up";
            case GO_DOWN:
                return "down";
            case DRILL_LEFT:
                return "act,left";
            case DRILL_RIGHT:
                return "act,right";
            case SUICIDE:
                return "act(0)";
            case DO_NOTHING:
            default:
                return "stop";
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Connection established");
    }

    @Override
    public void onMessage(String message) {
        log.info("222");
        //if (!shouldExit) {
            log.info("111");
            if (!message.startsWith(responsePrefix)) {
                log.error(String.format("Something strange is happening on the server... Response:%n%s", message));
                shouldExit = true;
            } else {
                String boardString = message.substring(responsePrefix.length());
                String action = doMove(new GameBoard(boardString));
                log.info(action);
                send(action);
            }
        //}

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("### disconnected ###");
    }

    @Override
    public void onError(Exception ex) {
        log.error("### error ###", ex);
    }
}

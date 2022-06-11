package com.github.bitfexl.stockfishconnector;

import com.github.bitfexl.stockfishconnector.engine.EngineConnector;
import com.github.bitfexl.stockfishconnector.httpserver.HttpServer;
import com.github.bitfexl.stockfishconnector.httpserver.Method;
import com.github.bitfexl.stockfishconnector.httpserver.Request;
import com.github.bitfexl.stockfishconnector.httpserver.RequestHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class StockfishServer implements RequestHandler {
    private static final int PORT = 37734;
    private static final String enginePath = "../engine/stockfish_14.1_win_x64_avx2.exe";

    public static void main(String[] args) {
        StockfishServer app = new StockfishServer();

        try {
            app.run();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                app.shutdown();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.out.println("-- END OF MAIN --");
    }

    private EngineConnector engine;

    private HttpServer httpServer;

    public void shutdown() {
        engine.close();
        httpServer.stop();
    }

    public void run() throws Exception {
        Scanner stdin = new Scanner(System.in);

        engine = new EngineConnector(enginePath);
        engine.open();

        httpServer = new HttpServer().start(PORT);
        httpServer.setHandler("/stockfish/", this);

        System.out.println("RUNNING...");
        System.out.println("PRESS ENTER TO SHUTDOWN");
        stdin.nextLine();
        System.out.println("EXITING...");
    }


    @Override
    public void handleRequest(Request request) throws IOException {
        if(request.getMethod() == Method.POST) {
            Scanner body = new Scanner(request.getRequestBody());

            String firstLine = "";
            if(body.hasNextLine()) {
                firstLine = body.nextLine();
            }

            if (firstLine.equals("solve")) {
                if(body.hasNextLine()) {
                    engine.searchPosition(body.nextLine());
                }
                request.beginBody(200);
            } else if (firstLine.equals("stop")) {
                PrintStream returnBody = new PrintStream(request.beginBody(200));

                String bestMove = engine.stopSearch();
                if(bestMove != null) {
                    returnBody.println(bestMove);
                } else {
                    returnBody.println("err");
                }
            }
        } else {
            PrintStream returnBody = new PrintStream(request.beginBody(200));
            returnBody.println("Hello World!");
        }
    }
}

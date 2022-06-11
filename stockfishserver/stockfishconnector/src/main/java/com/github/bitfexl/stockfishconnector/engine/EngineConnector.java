package com.github.bitfexl.stockfishconnector.engine;

import java.io.*;

public class EngineConnector {
    private static final int ANSWER_TIMEOUT_MS = 100;

    /**
     * Path to the chess-engine.
     */
    private String enginePath;

    /**
     * The engine subprocess.
     */
    private Process engine;

    /**
     * The process input stream.
     */
    private BufferedReader engineOutStream;

    /**
     * The process output stream.
     */
    private OutputStreamWriter engineInStream;

    /**
     * Construct new EngineConnector.
     * @param enginePath The path to the engine (exe).
     */
    public EngineConnector(String enginePath) {
        this.enginePath = enginePath;
    }

    /**
     * Start the engine.
     * @throws IOException Unable to init connection/start engine.
     */
    public void open() throws IOException {
        try {
            ProcessBuilder engineBuilder = new ProcessBuilder(enginePath);
            engine = engineBuilder.start();
            engineOutStream = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            engineInStream = new OutputStreamWriter(engine.getOutputStream());
        } catch (IOException ex) {
            throw new IOException("Error starting engine: " + new File(enginePath).getAbsolutePath(), ex);
        }
    }

    /**
     * Start to search a given position.
     * @param fen The position to search.
     * @throws IOException Error communicating with engine.
     */
    public void searchPosition(String fen) throws IOException {
        writeLine("position fen " + fen);
        writeLine("go");
    }

    /**
     * Stop search and wait up to 100ms for a bestmove answer.
     * @return The best move or null.
     */
    public String stopSearch() throws IOException {
        writeLine("stop");

        long startTime = System.currentTimeMillis();

        String currentLine;
        do {
            if(engineOutStream.ready()) {
                currentLine = engineOutStream.readLine();
                if (currentLine != null && currentLine.startsWith("bestmove")) {
                    return currentLine.split(" ")[1];
                }
            }
        } while (System.currentTimeMillis() - startTime < ANSWER_TIMEOUT_MS);

        return null;
    }

    public void close() {
        engine.destroy();
        try {
            engineInStream.close();
        } catch (IOException ignored) { }
        try {
            engineOutStream.close();
        } catch (IOException ignored) { }
    }

    /**
     * Write line to engine.
     * @param msg Message to send.
     */
    private void writeLine(String msg) throws IOException {
        try {
            engineInStream.write(msg);
            engineInStream.write("\n");
            engineInStream.flush();
        } catch (IOException ex) {
            throw new IOException("Error communicating with engine.", ex);
        }
    }
}


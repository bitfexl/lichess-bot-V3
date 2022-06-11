(async () => {
    let fenstr = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    await fetch("http://localhost:37734/stockfish", {
        method: "POST",
        body: "stop",
    });

    await fetch("http://localhost:37734/stockfish", {
        method: "POST",
        body: "solve\n" + fenstr,
    });

    await delay(2000);

    let response = await fetch("http://localhost:37734/stockfish", {
        method: "POST",
        body: "stop",
    });

    let bestmove = await response.text();

    console.log(bestmove);

    async function delay(ms) {
        return new Promise((resolve, reject) => {
            setTimeout(resolve, ms);
        });
    }
})();

// ==UserScript==
// @name         Lichessbot
// @namespace    https://github.com/bitfexl/
// @version      1.0
// @description  Makes you better at chess...
// @author       bitfexl
// @match        https://lichess.org/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=lichess.org
// @grant        GM_xmlhttpRequest
// @connect      localhost
// ==/UserScript==

(async function () {
    const updateIntervalMs = 400;
    const thinkTimeMs = 3000;
    const port = 37734;
    const url = "http://localhost:" + port + "/stockfish";

    let lastFen = "";
    let bestMove = "";

    let updateInterval = setInterval(async () => {
        let fenstr = await readBoardFen();
        if (fenstr != lastFen && fenstr !== null) {
            lastFen = fenstr;

            if (fenstr.split(" ")[1] == readOwnColor()) {
                bestMove = await getBestMove(fenstr, thinkTimeMs);

                console.log(fenstr);
                console.log(bestMove);

                try {
                    drawMove(bestMove);
                } catch {}
            }
        }
    }, updateIntervalMs);

    async function getBestMove(fen, timeout) {
        await tmFetch({
            url,
            method: "POST",
            data: "stop",
        });

        await tmFetch({
            url,
            method: "POST",
            data: "solve\n" + fen,
        });

        await delay(timeout);

        let newBestMove = await tmFetch({
            url,
            method: "POST",
            data: "stop",
        });

        return newBestMove;
    }

    async function tmFetch(options) {
        return new Promise((resolve, reject) => {
            options["onload"] = function (response) {
                resolve(response.responseText);
            };
            GM_xmlhttpRequest(options);
        });
    }

    async function readBoardFen() {
        return (await (await fetch(document.location.href)).text()).split('"fen":"')[1].split('"')[0];
    }

    function readOwnColor() {
        return document.querySelector("meta[property='og:url']").content.endsWith("black") ? "b" : "w";
    }

    function drawMove(move) {
        const abc = "abcdefgh";

        let x1 = abc.indexOf(move[0]);
        let y1 = 8 - Number(move[1]);
        let x2 = abc.indexOf(move[2]);
        let y2 = 8 - Number(move[3]);

        if (document.querySelector(".orientation-black") !== null) {
            x1 = 7 - x1;
            y1 = 7 - y1;
            x2 = 7 - x2;
            y2 = 7 - y2;
        }

        drawArrow(x1, y1, x2, y2);

        document.title = "Your turn - " + move;
    }

    function drawArrow(x, y, x2, y2) {
        let board = document.getElementsByTagName("cg-board")[0];

        let boundaries = board.getBoundingClientRect();
        let xOffset = (board.clientWidth / 8) * x + board.clientWidth / 16;
        let yOffset = (board.clientHeight / 8) * y + board.clientHeight / 16;
        let x2Offset = (board.clientWidth / 8) * x2 + board.clientWidth / 16;
        let y2Offset = (board.clientHeight / 8) * y2 + board.clientHeight / 16;

        board.dispatchEvent(
            new MouseEvent("mousedown", {
                button: 2,
                clientX: boundaries.x + xOffset,
                clientY: boundaries.y + yOffset,
            })
        );

        function cancelEvent(e) {
            if (e.detail != 89216345) {
                e.stopPropagation();
            }
        }
        document.addEventListener("mousemove", cancelEvent, true);

        setTimeout(() => {
            document.dispatchEvent(
                new MouseEvent("mousemove", {
                    button: 2,
                    clientX: boundaries.x + x2Offset,
                    clientY: boundaries.y + y2Offset,
                    detail: 89216345,
                })
            );
        }, 50);

        setTimeout(() => {
            document.dispatchEvent(
                new MouseEvent("mouseup", {
                    button: 2,
                    clientX: 0,
                    clientY: 0,
                })
            );

            document.removeEventListener("mousemove", cancelEvent, true);
        }, 100);
    }

    async function delay(ms) {
        return new Promise((resolve, reject) => {
            setTimeout(resolve, ms);
        });
    }
})();

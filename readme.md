> [!WARNING]  
> Deprecated! Use this https://gist.github.com/bitfexl/727b40e85d898308923384bc48df9185 instead.

# Lichessbot

A [lichess.org](https://lichess.org/) bot.

## Installation

The bot reads the board using a [tampermonkey](https://www.tampermonkey.net/) userscript and passes the data on to an application running on the pc.

-   Install the tampermonkey userscript [install](https://gist.github.com/bitfexl/be60ead10379a4c69c2aebcd126df620/raw/3f821b1e14f1123553f08f4289342fb5518394b5/lichessscript.user.js)

```
https://raw.githubusercontent.com/bitfexl/lichess-bot-V3/master/js/lichessscript.js
```

-   Execute the java app (run.bat in dist folder) [download](https://downgit.github.io/#/home?url=https://github.com/bitfexl/lichess-bot-V3/tree/master/dist)

## Usage

-   Java app needs to be running

The bot will automatically display the best move (takes some time, default: 3000ms, can be configured in tampermonkey script) to disable the bot simply disable the userscript and refresh the page.

To enable the bot mid round the page needs to be refreshed.

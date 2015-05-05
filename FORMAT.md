# 2048fx On-Disk File Format
`2048fx` uses two file types to store information about games: A high-score tracking file
and a more general save game file.

## Statistics
The high score file keeps track of the best score ever reached and the location of the most
recently saved game

* **signed `int`**: High Score
* **utf8 `String`**: Path to most recently saved game

## Saved Games
> Current Version: 2

Saved games contain the remainder of the stats and all cells created during the life of a game.

First, all cells on the board currently are saved, and then the history is traversed, saving
the state of cells and their composing cells along the way in a recursive fashion.

The file format is as follows:
* **signed `int`**: The version of the disk format
* **signed `int`**: Total Number of Moves thus far
* **signed `int`**: Score Stack Size
* `For Each`
    * **signed `int`**: Total Score during the turn
* **signed `int`**: Merged Cells Stack Size
* `For Each`
    * **signed `int`**: Total Number of Merged Cells this turn
* **signed `int`**: Undo Counter
* **signed `int`**: Number of Cells
* `For Each Cell`:
    * **signed `int`**: Cell Age
    * **signed `int`**: Cell Value
    * **signed `int`**: Position Stack Size
        * `For Each Position Vector`:
            * **signed `int`**: X Position
            * **signed `int`**: Y Position
        * **`boolean`**: Origin Cell
        * `If Not Origin Cell`:
            * `Cell` data for **Father `Cell`**
            * `Cell` data for **Mother `Cell`**
    
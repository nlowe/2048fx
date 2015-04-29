# 2048fx On-Disk File Format

## Statistics

* **signed `int`**: High Score
* **utf8 `String`**: Path to most recently saved game

## Saved Games
Starts off with some basic statistics and then stores cells recursively

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
    
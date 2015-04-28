# 2048fx On-Disk File Format

## Statistics

* **signed `int`**: High Score
* **utf8 `String`**: Path to most recently saved game

## Saved Games
Starts off with some basic statistics and then stores cells recursively up to the undo limit

* **signed `int`**: Total Number of Moves thus far
* **signed `int`**: Total Score thus far
* **signed `int`**: Total Number of Merged Cells thus far
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
    
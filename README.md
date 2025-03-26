# 20Wordy8

OVERVIEW
20Wordy8 is an interactive word puzzle game that combines the mechanics of sliding tiles, similar to 2048, with word
formation. The primary objective is to move tiles around the grid to form words. The game offers a range of features
including the ability to lock tiles, change letters, and remove tiles, with strategic options like using a blank tile
for letter changes or pressing a solve button for automated help.

The game consists of the following key classes:
- PaneOrganizer: Responsible for organizing and managing the layout of the game's user interface components.
- Game: The core game logic that handles the user key and mouse inputs, word formation, and game state.
- Board: Responsible for holding and updating the tiles, processing tile movements, and handling game logic related to
  the board itself.
- Tile: Represents a tile containing a letter and functionality for moving, merging, and locking/unlocking.
- BlankTile: A subclass of Tile, representing the special blank tile that can change its letter.
- SolveGame: Contains the logic for automatically solving the puzzle through a BFS search and animating the steps to
  the solution.
- Constants: Stores the game's constants, including board dimensions, tile sizes, colors, and other settings.
- Directions/Locks: Enums that contain the possible directions and lock states of a tile.

The program is structured around the Game class, which communicates with Board class to create Tile and BlankTile
objects. Tiles move across the board and merge based on letter matches, and the game state is continuously updated.
The SolveGame class provides a helper function to visually display the quickest path to solving the puzzle when the
player is stuck. It calls the Board Class to carry out this path

DESIGN CHOICES
- The game uses inheritance, where the BlankTile class extends the Tile class, because both types of tiles share
  common functionality (e.g., movement, locking), but the blank tile has distinct behavior (changing its letter). This
  allows for reuse of shared code while maintaining flexibility in functionality.
- Array Representation: The board is represented by a 2D array (Tile[][] board), which is a suitable structure for
  managing the tiles in a grid-based layout. It provides easy access to each tile's position using row and column
  indices, simplifying the logic for moving tiles and checking for merged tiles.

KNOWN BUGS
- Tile Movement: Occasionally, tiles may overlap or fail to merge correctly when moving rapidly in the same direction.
  This happens due to timing issues with the animation frames and has been noted but not yet fully resolved.
- Solver Path: If the target word contains a letter beyond "D", the BFS (Breadth-First Search) solver WILL fail to load
  due to the excessive number of combinations, which results in long computation times.



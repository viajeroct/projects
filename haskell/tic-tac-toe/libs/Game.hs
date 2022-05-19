module Game where
import Data.Array

n = 3 :: Int
type Board = Array (Int, Int) Cell
data Player = PlayerX | PlayerO deriving (Eq, Show)
data Cell = Empty | Full Player deriving (Eq, Show)
data State = Running | GameOver (Maybe Player) deriving (Eq, Show)
data Game = Game { gameBoard :: Board, gamePlayer :: Player, gameState :: State} deriving (Eq, Show)

screenWidth = 640 :: Int
screenHeight = 480 :: Int

cellWidth = (fromIntegral screenWidth / fromIntegral n) :: Float
cellHeight = (fromIntegral screenHeight / fromIntegral n) :: Float

initialGame = Game { gameBoard = array indexRange $ zip (range indexRange) (cycle [Empty]), gamePlayer = PlayerX, gameState = Running }
  where indexRange = ((0, 0), (n - 1, n - 1))

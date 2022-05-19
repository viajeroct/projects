module Main where

import Game
import Logic
import Rendering

import Graphics.Gloss
import Graphics.Gloss.Data.Color

window = InWindow "Viajero" (640, 480) (100, 100)
backgroundColor = makeColorI 0 179 255 255

main :: IO ()
main = play window backgroundColor 30 initialGame gameAsPicture transformGame (const id)

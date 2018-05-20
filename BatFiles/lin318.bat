@echo off
set loopcount=50
:loop
java -jar ../CoppaAlgoritmi.jar ../ALGO_cup_2018_problems/lin318.tsp YES
set /a loopcount=loopcount-1
if %loopcount%==0 goto exitloop
goto loop
:exitloop
pause

rem i commenti vengono fatti con REM
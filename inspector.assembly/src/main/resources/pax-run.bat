@echo off
SETLOCAL
set _SCRIPTS_=%~dp0
cd pax

CALL java %JAVA_OPTS% -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run %1 %2 %3 %4 %5 %6 %7 %8 %9 
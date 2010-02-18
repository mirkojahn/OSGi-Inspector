@echo off
SETLOCAL
set _SCRIPTS_=%~dp0
cd pax
echo Welcome to OSGi Inspector

if "%1%"=="tut1" goto tut1
if "%1%"=="tut2" goto tut2
:: the default runner is requested
goto default

:tut1
echo starting Tutorial 1
CALL java %JAVA_OPTS% -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run --args="file:tutorial1.args" %2 %3 %4 %5 %6 %7 %8 %9 
goto:eof

:tut2
echo starting Tutorial 2
CALL java %JAVA_OPTS% -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run --args="file:tutorial1.args" %2 %3 %4 %5 %6 %7 %8 %9 
goto:eof

:default
echo starting Inspector
CALL java %JAVA_OPTS% -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run %1 %2 %3 %4 %5 %6 %7 %8 %9 
goto:eof

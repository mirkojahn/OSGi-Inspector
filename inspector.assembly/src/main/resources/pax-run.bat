@echo off
SETLOCAL
set _SCRIPTS_=%~dp0
cd pax
echo Welcome to OSGi Inspector

if "%1%"=="tut1" goto tut1
if "%1%"=="tut2" goto tut2
if "%1%"=="mem" goto mem
:: the default runner is requested
goto default

:tut1
echo starting Tutorial 1
CALL java %JAVA_OPTS% -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run --args="file:tutorial1.args" %2 %3 %4 %5 %6 %7 %8 %9
goto:eof

:tut2
echo Starting Memory Profiling Inspector Runtime
set LOCALPATH=%~dp0
set LOCALPATH=%LOCALPATH%/libs
set DYLD_LIBRARY_PATH=%LOCALPATH%
CALL java %JAVA_OPTS% -agentlib:mem.agent-${mem.agent.version} -Dmem.agent.version=${mem.agent.version} -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run --args="file:tutorial1.args" %2 %3 %4 %5 %6 %7 %8 %9
goto:eof

:mem
echo Starting Memory Profiling Inspector Runtime
set LOCALPATH=%~dp0
set LIB_PATH=%LOCALPATH%libs
rem CALL java -verbose %JAVA_OPTS% -Djava.library.path="%LIB_PATH%" -agentlib:"mem.agent-0.1.1.SNAPSHOT" -Dmem.agent.version=0.1.1.SNAPSHOT -cp ".;%_SCRIPTS_%;%_SCRIPTS_%libs;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run --args="file:tutorial1.args" %2 %3 %4 %5 %6 %7 %8 %9
CALL java -verbose %JAVA_OPTS% -agentpath:%LIB_PATH%\mem.agent-${mem.agent.version}.dll -Dmem.agent.version=${mem.agent.version} -cp ".;%_SCRIPTS_%;%_SCRIPTS_%libs;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run --args="file:runner-grizzly.args" %2 %3 %4 %5 %6 %7 %8 %9
goto:eof

:default
echo starting Inspector
CALL java %JAVA_OPTS% -cp ".;%_SCRIPTS_%;%_SCRIPTS_%\pax\pax-runner.jar" org.ops4j.pax.runner.Run %1 %2 %3 %4 %5 %6 %7 %8 %9
goto:eof

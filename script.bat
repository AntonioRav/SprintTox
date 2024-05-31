@echo off
echo compilation loading ...

if exist javasource (
    rmdir /s /q javasource
)
mkdir javasource

for /r ./src/ %%f in (*.java) do copy "%%f" javasource

javac -d . javasource/*.java

echo creation jar file ...
jar -cf "tox.jar"  mg

exit /b 0
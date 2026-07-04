@REM Maven Wrapper startup script for Windows
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir

@echo off
setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"

set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

if exist "%WRAPPER_JAR%" (
    java %MAVEN_OPTS% -jar "%WRAPPER_JAR%" %*
    goto end
)

set "DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"

if not exist "%MAVEN_HOME%" (
    echo Downloading Maven...
    mkdir "%MAVEN_HOME%" 2>nul
    powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%MAVEN_HOME%\maven.zip'"
    powershell -Command "Expand-Archive -Path '%MAVEN_HOME%\maven.zip' -DestinationPath '%MAVEN_HOME%' -Force"
    del "%MAVEN_HOME%\maven.zip"
    for /d %%i in ("%MAVEN_HOME%\apache-maven-*") do set "MAVEN_HOME=%%i"
)

"%MAVEN_HOME%\bin\mvn" %*

:end
endlocal

@echo off
@setlocal
set "MAVEN_PROJECTBASEDIR=%~dp0"

rem Check if mvn is available
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    mvn %*
    goto end
)

set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper...
    set "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
    if %ERRORLEVEL% neq 0 (
        echo Failed to download Maven Wrapper.
        exit /b 1
    )
)

set "MVN_DIR=%MAVEN_PROJECTBASEDIR:~0,-1%"
java -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MVN_DIR%" "%WRAPPER_LAUNCHER%" %*

:end
endlocal

@echo off

echo Welcome to our AI world! Please choose an option:
echo 1. Continue
echo 2. Terminate

set choice=
set /p choice=Enter your choice (1 or 2):

if "%choice%"=="1" (
    echo Continuing with script...
    python script/auto_s1_stmt_generation.py
    python script/auto_s2.0_feature_generation.py
    python script/auto_s3_step_generation.py
    python script/auto_s4_Java_page_generation.py
) else if "%choice%"=="2" (
    echo Script terminated by user.
) else (
    echo Invalid choice. Please enter 1 to continue or 2 to terminate.
)

pause

#!/bin/bash

# Print welcome message
echo "Welcome to our AI world! Please choose an option:"
echo "1. Continue"
echo "2. Terminate"

# Read user input without echoing characters
read -r -p "Enter your choice (1 or 2): " choice

# Check for valid choices and execute scripts
if [[ $choice -eq 1 ]]; then
  echo "Continuing with script..."
  python3 script/auto_s1_stmt_generation.py
  python3 script/auto_s2.0_feature_generation.py
  echo "Feature file generation is done. Press any key to continue to the Step Definition..."
  read -r -p "(Press Enter to continue)"  # Wait for user input without echoing
  python3 script/auto_s3_step_generation.py
  echo "Step Definition file is done. Press any key to continue to the Object and Pages creation..."
  read -r -p "(Press Enter to continue)"  # Wait for user input without echoing
  python3 script/auto_s4_Java_page_generation.py
  echo "Script completed."
elif [[ $choice -eq 2 ]]; then
  echo "Script terminated by user."
else
  echo "Invalid choice. Please enter 1 to continue or 2 to terminate."
fi

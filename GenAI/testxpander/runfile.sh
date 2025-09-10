#!/bin/bash

api_url=$(python3 extract_data_from_config.py "CreateQIdUrl")
gpt_response=$(python3 extract_data_from_config.py "GetResponseFromQId")
stepdef=$(python3 extract_data_from_config.py "StepDefFilePath")
featureFileRequestPath=$(python3 extract_data_from_config.py "FeatureFileRequestPath")
responseFilePath=$(python3 extract_data_from_config.py "FeatureFileResponsePath")
stepDefReqPath=$(python3 extract_data_from_config.py "StepDefRequestPath")
stepDefResPath=$(python3 extract_data_from_config.py "StepDefResponseJavaPath")
access_token=$(python3 extract_data_from_token.py "access_token")

api_key="Bearer $access_token"
REQUEST_BODY_FILE="request.json"
sessionID=""

# while true; do
read -p "Hello, Please Type 1 to autocorrect feature file steps Or Type 2 to create step definition code? " answer
if [[ $answer == "1" ]]; then

    if test -f "$responseFilePath"; then
        rm "$responseFilePath"
    fi
    #  read -p "Dear User!! how can I help you ?" query
    prompt1="prompt1.txt"
    #Read the contents of the promtp1 into variable
    prompt1Data=$(cat "$prompt1" | tr -d '\n')
    #Read the contents of the step def file into variable
    cat $stepdef/* > merged_steps.txt
    stepDefText=$(cat merged_steps.txt | tr -d '\n' | head -c 18000)
    #Read the contents of the feature file into variable
    featurefileData=$(cat "$featureFileRequestPath" | tr -d '\n')
    #Read the contents of the JSON request body into a variable
    REQUEST_BODY=$(cat "$REQUEST_BODY_FILE")

    # Setting context and Getting session ID
    # Modify the request body
    modified_REQUEST_BODY=${REQUEST_BODY/\"message\": \"queryfromUser\"/\"message\": \"$prompt1Data\"}
    modified_REQUEST_BODY=$(echo "$modified_REQUEST_BODY" | sed "s/\"sessID\"/\"$sessionID\"/g")
    # echo "hello"$modified_REQUEST_BODY
    response=$(./setContext.sh "$modified_REQUEST_BODY")
    #echo $response
    sessionID=$(python3 extract_sessionID.py "$response")
    #echo "SessionID**"$sessionID

    # Setting Step Definition file to read
    stepDefData=${stepDefText//\"/\\\"}
    # Modify the request body
    modified_REQUEST_BODY=${REQUEST_BODY/\"message\": \"queryfromUser\"/\"message\": \"$stepDefData\"}
    modified_REQUEST_BODY=$(echo "$modified_REQUEST_BODY" | sed "s/\"sessID\"/\"$sessionID\"/g")
    response=$(./setContext.sh "$modified_REQUEST_BODY")
    #echo "____RESPONSE____: "$response

    # Providing raw feature file to autocorrect
    # Modify the request body
    featurefileDataModify=${featurefileData//\"/\\\"}
    modified_REQUEST_BODY=${REQUEST_BODY/\"message\": \"queryfromUser\"/\"message\": \"$featurefileDataModify\"}
    modified_REQUEST_BODY=$(echo "$modified_REQUEST_BODY" | sed "s/\"sessID\"/\"$sessionID\"/g")
    response1=$(./setContext.sh "$modified_REQUEST_BODY")
    #echo "*********RESPONSE********: "$response1

    last_content=$(python3 extract_content.py "$response1")
    last_content=$(echo $last_content | tr -d '\\')
    # Remove double quotes from the beginning and end of the string
    # last_content="${last_content#\"}"
    # last_content="${last_content%\"}"
    #echo "_______refined api response: $last_content"
    extracted_string=$(python3 extract_finalResponse.py "$last_content")

    # if echo "$last_content" | grep -q 'Thank you' || echo "$last_content" | grep -q 'Sure'; then

    #     echo "&&&&inside IF&&&&"
    #     if echo "$last_content" | grep -q 'step definitions provided:'; then
    #         echo "&&&&inside 1&&&&"
    #         extracted_string=$(echo "$last_content" | awk -F 'step definitions provided:' '{print $2}' | sed 's/gherkin//')
    #     fi
    #     if echo "$last_content" | grep -q '```'; then
    #         echo "&&&&inside 2&&&&"
    #         extracted_string=$(echo "$last_content" | awk -F '```' '{print $2}' | sed 's/gherkin//')
    #     fi
    #     if echo "$last_content" | grep -q 'step definition file:'; then
    #         echo "&&&&inside 3&&&&"
    #         extracted_string=$(echo "$last_content" | awk -F 'step definition file:' '{print $2}' | sed 's/gherkin//')
    #     fi
    #     if echo "$last_content" | grep -q 'steps you provided:'; then
    #         echo "&&&&inside 4&&&&"
    #         extracted_string=$(echo "$last_content" | awk -F 'steps you provided:' '{print $2}' | sed 's/gherkin//')
    #     fi
    # else
    #     extracted_string="$last_content"
    # fi

    echo "_____Final Text_______"$extracted_string

    #Create the feature file
    touch "$responseFilePath"
    # Set the substrings to split the string
    substrings=("Feature:" "Scenario:" "Scenario Outline:" "Given" "When" "Then" "And" "Examples:")
    extracted_string=$extracted_string
    for substring in "${substrings[@]}"; do
        extracted_string=$(echo "$extracted_string" | sed "s/$substring/\n$substring/g")
    done
    # Print the updated string
    # echo "$extracted_string"
    echo "$extracted_string" | while IFS= read -r line; do
        echo "$line" >>"$responseFilePath"
    done

else
    prompt3=""
    sessionID=""
    if test -f "$stepDefResPath.java"; then
        rm "$stepDefResPath.java"
    fi
    if test -f "$stepDefResPath.js"; then
        rm "$stepDefResPath.js"
    fi
    read -p "In which language you want to create step definition? (java/js) " stepdefLang
    if [[ $stepdefLang == "java" ]]; then
        prompt3="Can you create code with non duplicating cucumber step definition for following steps in Java language without import statement"
    elif [[ $stepdefLang == "js" ]]; then
        prompt3="Can you create code with non duplicating cucumber step definition for following steps in JavaScript language without import statement and async function"
    else
        echo "Invalid selection. Please type either 'java' or 'js'."
    fi
    
    #echo $prompt3
    #Read the contents of the JSON file into a variable
    query=$(cat "$stepDefReqPath" | tr -d '\n')
    finalQuery=$prompt3" "$query
    # Replace double quotes with escaped double quotes
    query_updated=${finalQuery//\"/\\\"}
    #echo "$query_updated"

    #Read the contents of the JSON file into a variable
    REQUEST_BODY=$(cat "$REQUEST_BODY_FILE")
    modified_REQUEST_BODY=${REQUEST_BODY/\"message\": \"queryfromUser\"/\"message\": \"$query_updated\"}
    modified_REQUEST_BODY=$(echo "$modified_REQUEST_BODY" | sed "s/\"sessID\"/\"$sessionID\"/g")
    #echo "$modified_REQUEST_BODY"
    response=$(./setContext.sh "$modified_REQUEST_BODY")
    #echo $response
    last_content=$(python3 extract_content_StepDef.py "$response")
    last_content=$(echo $last_content | tr -d '\n')
    # Remove double quotes from the beginning and end of the string
    last_content="${last_content#\"}"
    last_content="${last_content%\"}"
    if echo "$last_content" | grep -q '```' || echo "$last_content" | grep -q 'Sure'; then
        extracted_string=$(echo "$last_content" | awk -F '```' '{print $2}' | sed 's/javascript//' | sed 's/java//')
        #echo "Extracted string: $extracted_string"
    else
        extracted_string="$last_content"
        #echo $extracted_string
    fi
    substrings=""
    if [[ $stepdefLang == "java" ]]; then
        #Create the stepdef file
        fileName="$stepDefResPath.java"
        touch "$fileName"
        substrings=("@Given" "@When" "@Then" "@And" "public" "import")

    else
        [[ $stepdefLang == "js" ]]
        fileName="$stepDefResPath.js"
        #Create the stepdef file
        touch "$fileName"
        substrings=("Given(" "When(" "Then(")
    fi
    extracted_string=$extracted_string
    for substring in "${substrings[@]}"; do
        extracted_string=$(echo "$extracted_string" | sed "s/$substring/\n$substring/g")
    done
    echo "$extracted_string" | while IFS= read -r line; do
        echo "$line" >>"$fileName"
    done
fi

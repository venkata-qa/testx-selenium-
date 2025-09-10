#!/bin/bash

api_url=$(python3 extract_data_from_config.py "CreateQIdUrl")
gpt_response=$(python3 extract_data_from_config.py "GetResponseFromQId")
access_token=$(python3 extract_data_from_token.py "access_token")


# Get the input parameters
modified_REQUEST_BODY=$1
api_key="Bearer $access_token"

# Send the HTTP POST request and get the query ID
getId=$(curl -s -H "Authorization: $api_key" -H "Content-Type: application/json" -X POST -d "$modified_REQUEST_BODY" "$api_url")
qid=$(echo "$getId" | grep -o '"qid":"[^"]*"' | sed 's/\\"/"/g' | sed 's/^.*://')
queryId=$(echo $qid | awk '{gsub(/^"|"$/,"")}1')
# echo "*******"$queryId
# Update the request URL with the query ID
updated_gpt_request="$gpt_response$queryId"

# Wait for the response status to change from "pending"
status="pending"
while [ "$status" == "pending" ]; do
    response=$(curl -s -H "Authorization: $api_key" -H "Content-Type: application/json" -X GET "$updated_gpt_request")
    status_response=$(echo "$response" | grep -o '"status":"[^"]*"' | sed 's/\\"/"/g' | sed 's/^.*://')
    status=$(echo $status_response | awk '{gsub(/^"|"$/,"")}1')
    if [ "$status" == "pending" ]; then
        sleep 5
    fi
done

# Return the API response
echo "$response"

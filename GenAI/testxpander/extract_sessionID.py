import json
import sys

# Get the JSON response from the command-line argument
response = sys.argv[1]

# Parse the JSON response
data = json.loads(response)

# Get the last message content
sessionID = data['results']['data']['id']

# Print the last message content
print(sessionID)

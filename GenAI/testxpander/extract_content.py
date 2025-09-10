import json
import sys

# Get the JSON response from the command-line argument
response = sys.argv[1]

# Parse the JSON response
data = json.loads(response)

# Get the last message content
last_message = data['results']['data']['messages'][-1]['content']

# Remove any backslashes from the last message content
last_content = last_message.replace('\\', '')

# Print the last message content
print(last_content)

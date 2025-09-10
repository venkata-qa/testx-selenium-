import json
import sys

# Get the JSON response from the command-line argument
key = sys.argv[1]

# Read the JSON file
with open('config.json') as f:
    json_str = f.read()
    json_obj = json.loads(json_str)

# Retrieve the value based on the given key
value = json_obj.get(key)

# Print the value
print(value)

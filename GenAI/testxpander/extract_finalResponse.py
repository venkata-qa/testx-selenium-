import sys

def get_substring(string):
    start = string.find("Feature:")
    end1 = string.find("Please note")
    end2 = string.find("'''")

    if end2 != -1:
        end = end2
    elif end1 != -1:
        end = end1
    else:
        end = len(string)

    if start != -1 and end != -1:
        result = string[start:end].strip()
        return result
    else:
        return "Substring not found."

if __name__ == "__main__":
    if len(sys.argv) > 1:
        input_string = sys.argv[1]
        substring = get_substring(input_string)
        print(substring)
    else:
        print("Please provide a string as a parameter.")
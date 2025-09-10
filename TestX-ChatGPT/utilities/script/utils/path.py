import os


def find_project_root():
    current_path = os.getcwd()
    while True:
        # Check if requirements.txt exists in the current path
        requirements_path = os.path.join(current_path, 'requirements.txt')
        if os.path.isfile(requirements_path):
            return current_path
        # Move up one directory level
        parent_path = os.path.dirname(current_path)
        # Check if we have reached the filesystem root
        if parent_path == current_path:
            break
        current_path = parent_path
    # If no project root is found, return None
    return None

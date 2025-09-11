import os
import pandas as pd

from utils.path import find_project_root


root = find_project_root()
# Safe path/open helpers to prevent path traversal and arbitrary file access
def _ensure_safe_path(path: str) -> str:
    base = os.path.abspath(root)
    abs_path = os.path.abspath(path)
    # Ensure the target path stays within the project root
    if os.path.commonpath([base, abs_path]) != base:
        raise ValueError(f"Unsafe file path detected: {path}")
    return abs_path

def _safe_open(path: str, mode: str):
    abs_path = _ensure_safe_path(path)
    # Create parent directories when writing/appending
    if any(m in mode for m in ("w", "a", "+")):
        os.makedirs(os.path.dirname(abs_path), exist_ok=True)
    return open(abs_path, mode, encoding="utf-8")
# Assuming you have read your data into a DataFrame called df
df = pd.read_excel(f'{root}/utilities/script/input/test_guidelines.xlsx')

# Replace 'column_name' with the actual names of the columns you want to extract
elements_to_pages_mapping = dict(zip(df['Element1'], df['Page']))


def is_field_present(file_path, field_name):
    with _safe_open(file_path, 'r') as file:
        content = file.read()
        return field_name in content


def create_or_update_file_with_fields(element_name, page_name):
    file_path = f"{root}/utilities/script/result/pages/{page_name}.py"

    if os.path.exists(_ensure_safe_path(file_path)):
        # File exists, open in append mode
        with _safe_open(file_path, 'a') as file:
            # Convert field name to uppercase and replace spaces with underscores
            formatted_field_name = element_name.upper().replace(" ", "_")
            if not is_field_present(file_path, formatted_field_name):
                file.write(f"\t{formatted_field_name} = (By.ID, '{formatted_field_name.lower()}')\n")

    else:
        with _safe_open(file_path, 'w') as file:
            file.write("# This is a new Python file\n\n")
            file.write("from selenium.webdriver.common.by import By")
            file.write(f"\n\n\nclass {page_name}:\n")
            # Convert field name to uppercase and replace spaces with underscores
            formatted_field_name = element_name.upper().replace(" ", "_")
            file.write(f"\t{formatted_field_name} = (By.ID, '{formatted_field_name.lower()}')\n")

for element_name, page_name in elements_to_pages_mapping.items():
    if pd.notna(element_name) and pd.notna(page_name):
        create_or_update_file_with_fields(element_name, page_name)
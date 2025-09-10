import os
import pandas as pd

from utils.path import find_project_root

root = find_project_root()
# Assuming you have read your data into a DataFrame called df
df = pd.read_excel(f'{root}/utilities/script/input/test_guidelines.xlsx')

# Replace 'column_name' with the actual names of the columns you want to extract
elements_to_pages_mapping = dict(zip(df['Element1'], df['Page']))


def is_field_present(file_path, field_name):
    with open(file_path, 'r') as file:
        content = file.read()
        return field_name in content


def create_or_update_file_with_fields(element_name, page_name, file_path):
    if os.path.exists(file_path):
        # File exists, open in append mode
        with open(file_path, 'a') as file:
            # Convert field name to uppercase and replace spaces with underscores
            formatted_field_name = element_name.upper().replace(" ", "_")
            if not is_field_present(file_path, formatted_field_name):
                file.write(f"@FindBy(id = \"{formatted_field_name.lower()}\")\n")
                file.write("public static WebElement " + formatted_field_name + ";\n\n")

    else:
        with open(file_path, 'w') as file:
            file.write("// This is a Java Class\n\n")

            file.write("import org.openqa.selenium.WebElement;\n")
            file.write("import org.openqa.selenium.support.FindBy;")

            file.write(f"\n\npublic class {page_name} {{ \n")
            # Convert field name to uppercase and replace spaces with underscores
            formatted_field_name = element_name.upper().replace(" ", "_")

            file.write(f"@FindBy(id = \"{formatted_field_name.lower()}\")\n")

            file.write("public static WebElement " + formatted_field_name + ";\n\n")


for element_name, page_name in elements_to_pages_mapping.items():
    file_path = f"{root}/utilities/script/result/pages/{page_name}.java"
    if pd.notna(element_name) and pd.notna(page_name):
        create_or_update_file_with_fields(element_name, page_name, file_path)

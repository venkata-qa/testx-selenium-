import pandas as pd
import warnings
from utils.path import find_project_root


root = find_project_root()
warnings.filterwarnings("ignore")
# Read the data from the Excel file
df = pd.read_excel(root + '/utilities/script/input/test_guidelines.xlsx', sheet_name="Sheet1")

result_string = ""
for index, row in df.iterrows():
    if row['Action'] == 'Hover':
        result_string += f"{row['Action']} on <{row['Element1']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Click':
        result_string += f"{row['Action']} on <{row['Element1']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Enter':
        result_string += f"{row['Action']} <{row['Value']}> in <{row['Element1']}> field on <{row['Page']}>\n"
    elif row['Action'] == 'Double Click':
        result_string += f"{row['Action']} on <{row['Element1']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Right Click':
        result_string += f"{row['Action']} on <{row['Element1']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Drag and Drop':
        result_string += f"{row['Action']} from <{row['Element1']}> to <{row['Element2']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Assert':
        if row['Sub Action'] == 'Element Text':
            result_string += f"{row['Action']} element <{row['Element1']}> text with <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Alert Text':
            result_string += f"{row['Action']} alert text with <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Page Title Text':
            result_string += f"{row['Action']} page title text with <{row['Expected']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Zoom':
        if row['Sub Action'] == 'In':
            result_string += f"{row['Action']} in(Keys.CONTROL, Keys.ADD) on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Out':
            result_string += f"{row['Action']} out(Keys.CONTROL, Keys.SUBTRACT) on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Reset':
            result_string += f"Reset {row['Action']}(Keys.CONTROL, '0') on <{row['Page']}>\n"
    elif row['Action'] == 'Scroll':
        if row['Sub Action'] == 'To Element':
            result_string += f"{row['Action']} till element <{row['Element1']}> visible on <{row['Page']}>\n"
        else:
            result_string += f"{row['Action']} {row['Sub Action']} on <{row['Page']}>\n"
    elif row['Action'] == 'Select':
        if row['Sub Action'] == 'By Value':
            result_string += f"{row['Action']} by value <{row['Value']}> option in <{row['Element1']}> dropdown on <{row['Page']}>\n"
        elif row['Sub Action'] == 'By Index':
            result_string += f"{row['Action']} by index <{row['Value']}> option in <{row['Element1']}> dropdown on <{row['Page']}>\n"
        elif row['Sub Action'] == 'By Visible Text':
            result_string += f"{row['Action']} by visible text <{row['Value']}> option in <{row['Element1']}> " \
                             f"dropdown on <{row['Page']}>\n"
    elif row['Action'] == 'Verify':
        if row['Sub Action'] == 'Checked':
            result_string += f"{row['Action']} element <{row['Element1']}> checked <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Selected':
            result_string += f"{row['Action']} element <{row['Element1']}> selected <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Enabled':
            result_string += f"{row['Action']} element <{row['Element1']}> enabled <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Editable':
            result_string += f"{row['Action']} element <{row['Element1']}> editable <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Presence':
            result_string += f"{row['Action']} element <{row['Element1']}> presence <{row['Expected']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Zoom Level':
            result_string += f"{row['Action']} zoom level as <{row['Expected']}> on <{row['Page']}>\n"
    elif row['Action'] == 'Navigate':
        if row['Sub Action'] == 'To':
            result_string += f"{row['Action']} to <{row['Value']}>\n"
        elif row['Sub Action'] == 'Back':
            result_string += f"{row['Action']} back\n"
        elif row['Sub Action'] == 'Forward':
            result_string += f"{row['Action']} forward\n"
        elif row['Sub Action'] == 'Refresh':
            result_string += f"{row['Action']} to refresh\n"
    elif row['Action'] == 'Switch':
        if row['Sub Action'] == 'Frame':
            result_string += f"{row['Action']} to frame using element <{row['Element1']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Default Content':
            result_string += f"{row['Action']} to default content on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Alert':
            result_string += f"{row['Action']} to alert on <{row['Page']}>\n"
        elif row['Sub Action'] == 'New Window':
            result_string += f"{row['Action']} to new opened window on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Window By Name':
            result_string += f"{row['Action']} to window by name <{row['Value']}> on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Previous Window':
            result_string += f"{row['Action']} to previous opened window on <{row['Page']}>\n"
        elif row['Sub Action'] == 'Close Window':
            result_string += f"{row['Sub Action']}\n"
        elif row['Sub Action'] == 'Maximize Window':
            result_string += f"{row['Sub Action']}\n"
    elif row['Scenario No.'] >= 2:
        result_string += f"\nScenario:{row['Scenario No.']} - {row['Scenario']}\n"
    elif row['Scenario No.'] == 1:
        result_string += f"Scenario:{row['Scenario No.']} - {row['Scenario']}\n"

with open(root + '/utilities/script/input/input.txt', 'w') as file:
    file.write(result_string)

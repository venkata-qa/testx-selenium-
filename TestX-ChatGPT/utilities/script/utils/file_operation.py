import os
import json


def get_api_token(json_file_path):
    # Read the JSON file
    with open(json_file_path, 'r') as file:
        data = json.load(file)

    # Specify the key you want to retrieve
    key_to_read = "gen_ai_token"
    return data[key_to_read]


def get_latest_folder(path):
    # Get a list of all directories in the path
    directories = [d for d in os.listdir(path) if os.path.isdir(os.path.join(path, d))]

    # If there are no directories, return None
    if not directories:
        return None

    # Find the latest folder based on the modification time
    latest_folder = max(directories, key=lambda d: os.path.getmtime(os.path.join(path, d)))

    # Return the full path to the latest folder
    return os.path.join(path, latest_folder)


def get_latest_folder_and_file(path):
    entries = [entry for entry in os.listdir(path) if os.path.exists(os.path.join(path, entry))]

    if not entries:
        return None, None

    folders = [entry for entry in entries if os.path.isdir(os.path.join(path, entry))]

    if not folders:
        return None, None

    latest_folder = max(folders, key=lambda folder: os.path.getmtime(os.path.join(path, folder)))
    folder_path = os.path.join(path, latest_folder)

    folder_entries = [entry for entry in os.listdir(folder_path) if
                      os.path.exists(os.path.join(folder_path, entry)) and not os.path.isdir(
                          os.path.join(folder_path, entry))]

    if not folder_entries:
        return folder_path, None

    latest_file = max(folder_entries, key=lambda file: os.path.getmtime(os.path.join(folder_path, file)))

    return folder_path, latest_file


def read_file(file_path):
    with open(file_path, 'r') as file:
        data = file.read()
    return data


def feature_helper_data():
    helper_data = ''' 'Write BDD test scenario
    'Append 'on <random page name> page' for each step Always use double quote for all 2 or 3 argument in 
    step inside a feature file like web element, text value to be entered and page name. Like below.\n'''
    return helper_data


def temp_feature_helper_data():
    return '''
    Please modify the following Gherkin scenario with data table feature of BDD while updating ensuring that the sequence and meaning remain unchanged.
     Combine all possible steps from top to bottom that involve similar subsequent actions on the same page, 
     but keep distinct steps separate. Do not alter the order of the steps or the meaning of the scenario. 
Here is my input that you need to modify
-------------------------------------------------------
    '''


def be_helper_data():
    return '''Generate complete code using Java for below\n'''


def feature_query_data(file_path):
    return read_file(file_path)


def create_query(file_path):
    # helper_data = '''Write BDD test scenario exactly same as per provided input
    # 'Append 'on <random page name> page' for each step Always use double quote for all 2 or 3 argument in
    # step inside a feature file like web element, text value to be entered and page name. Like below.
    # This is my input below.\n'''
    # data = read_file(file_path)
    # return helper_data + data

    helper_data = ''' 'Write BDD test scenario
    'Append 'on <random page name> page' for each step Always use double quote for all 2 or 3 argument in 
    step inside a feature file like web element, text value to be entered and page name. Like below.\n'''
    data = read_file(file_path)
    return helper_data + data


def create_query_optimized_ff(file_path):
    helper_data = ''' Convert my input as per below format\n.
    Given the following format for testing various features in my application for all pages and all actions:
Given User is on the "Page_Name"
When User hovers on the following elements on "Page_Name"
  | fieldName |
  | ...        |
And User clicks on the following elements on "Page_Name"
  | fieldName |
  | ...        |
And User selects the following values from dropdowns on "Page_Name"
  | fieldName      | selectedValue |
  | ...             | ...           |
And User enters the following data for the fields on "Page_Name"
  | fieldName | data |
  | ...       | ...  |
And User asserts that element "Element_Name" text is "Expected_Text" on "Page_Name"
  | fieldName | 
  | ...       |

Please generate responses based on this structure for various features and scenarios.
    \n Here is my input \n'''
    data = read_file(file_path)
    return helper_data + data


def create_query_step(addon):
    individual_test_cases = 'Generate selenium generic step_definition for new ' \
                            'lines/steps in detail using java language, generic means do not use any testcases ' \
                            'reference like login button, continue button, search field, etc. and ' \
                            'avoid ambiguity while generating step definition.' \
                            'step definition method name should be generic like clickElement(),enterValue(), etc. ' \
                            'and strictly use webelement declaration with ele1 or ele2 name only' \
                            'and take step definition argument name relevant to step line ' \
                            'like element, pageName, valueToEnter, expectedValue, sropdownOption, etc.' \
                            'and make sure strictly no duplicate stepdefinition ' \
                            'and each steps needs to generate only once.' \
                            'use this methods <load_and_find_page_element> to find the web element. and I ' \
                            'want all code inside step definition only related to selenium commands.' \
                            'Do not add commented line in step definition related to selenium commands.' \
                            'For step def argument use {string}' \
                            ' Please generate complete end to end code ' \
                            'and will not change ' \
                            'any thing in code generated by chatgpt\n' + addon
    return individual_test_cases


def temp_create_query_step(addon, all_step):
    existing = read_file(all_step)
    var = '''
Generate selenium step_definition for required and new steps only in detail using java language.
Do not generate step definitions those are already available.
Here are the list of available step definitions.
------------------------------------------------
Read these rules before generating the response
''' + existing + '''
Please use this methods <load_and_find_page_element> to find the web element. and I want all code inside step definition only related to selenium commands.Do not add commented line in step definition related to selenium commands.For step def argument use {string} Please generate complete end to end code and will not change any thing in code generated by chatgpt

Input is here:
--------------
Here is the scenario for that you need to generate step definitions(Only new, not the existing step definitions)
    ''' + addon
    return var


def temp_create_query_step1(addon, all_step):
    existing = read_file(all_step)
    individual_test_cases = 'Note:- Below step definition already we have with us' \
                            '\n------------------------------------------------\n' \
                            'assertElementText' \
                            '\n strictly generate the step definition apart from all above.' \
                            'Generate selenium generic step_definition for required ' \
                            'lines/steps in detail using java language, generic means do not use any testcases ' \
                            'reference like login button, continue button, search field, etc. and ' \
                            'avoid ambiguity while generating step definition.' \
                            'step definition method name should be generic like clickElement(),enterValue(), etc. ' \
                            'and strictly use webelement declaration with ele1 or ele2 name only' \
                            'and take step definition argument name relevant to step line ' \
                            'like element, pageName, valueToEnter, expectedValue, dropdownOption, etc.' \
                            'and make sure strictly no duplicate stepdefinition ' \
                            'and each steps needs to generate only once.' \
                            'use this methods <load_and_find_page_element> to find the web element. and I ' \
                            'want all code inside step definition only related to selenium commands.' \
                            'Do not add commented line in step definition related to selenium commands.' \
                            'For step def argument use {string}' \
                            ' Please generate complete end to end code ' \
                            'and will not change ' \
                            'any thing in code generated by chatgpt\n' + addon
    return individual_test_cases


def temp_create_query_step3(addon, all_step):
    existing = read_file(all_step)
    individual_test_cases = f'''
    Note: Below is the existing step definition.\n
    {existing}\n
Instructions:\n
Generate Selenium generic step definitions for the specified lines/steps in detail using the Java language. 
Ensure the generated step definitions are distinct from those listed above. 
In "generic" step definitions, refrain from referencing specific test cases 
such as login buttons, continue buttons, search fields, etc. to avoid ambiguity. 
The method names in the step definitions should be generic, like clickElement(), enterValue(), etc. 
Strictly use WebElement declarations with names like ele1 or ele2 only. 
Additionally, use relevant names for step definition arguments based on the step lines, 
such as element, pageName, valueToEnter, expectedValue, dropdownOption, etc. 
Ensure there are no duplicate step definitions, and each step generates only once. 
Utilize the <load_and_find_page_element> method to find the web element. 
All code inside the step definition should be related to Selenium commands. 
Avoid adding commented lines in the step definition related to Selenium commands. 
For step definition arguments, use {{string}}. 
Please generate complete end-to-end code, and do not alter the code generated by ChatGPT.\n
    ''' + addon
    return individual_test_cases


def temp_create_query_step2():
    return '''
    Generate Java step definitions (except - I assert element {string} text with {string} on {string} page) for the following Gherkin scenario:
    
    Feature: Compare two shirt products test
    
    Scenario: Compare two shirt products on <random page name> page
    
    Given I enter "<shirt>" in "<Search_Field_Element>" field on "<Home_Page>" page
    When I click on "<Search_Icon_Element>" on "<Home_Page>" page
    Then I assert the page title text with "<Search Item>" on "<Result_Page>" page
    And I click on "<Add_To_Compare_Icon_Item1_Element>" on "<Result_Page>" page
    And I click on "<Add_To_Compare_Icon_Item2_Element>" on "<Result_Page>" page
    And I click on "<Add_To_Compare_Icon_Item3_Element>" on "<Result_Page>" page
    And I click on "<Add_To_Compare_Icon_Item4_Element>" on "<Result_Page>" page
    And I click on "<Add_To_Compare_Icon_Item5_Element>" on "<Result_Page>" page
    And I click on "<Add_To_Compare_Icon_Item6_Element>" on "<Result_Page>" page
    And I assert element "<Item1_Label_Element>" text with "<Circe Hooded Ice Fleece>" on "<Result_Page>" page
    And I assert element "<Item2_Label_Element>" text with "<Balboa Persistence Tee>" on "<Result_Page>" page
    And I assert element "<Item3_Label_Element>" text with "<Circe Hooded Ice Fleece>" on "<Result_Page>" page
    And I assert element "<Item4_Label_Element>" text with "<Balboa Persistence Tee>" on "<Result_Page>" page
    And I assert element "<Item5_Label_Element>" text with "<Circe Hooded Ice Fleece>" on "<Result_Page>" page
    And I assert element "<Item6_Label_Element>" text with "<Balboa Persistence Tee>" on "<Result_Page>" page
    And I click on "<Compare_Button_Element>" on "<Compare_Page>" page
    Then I assert element "<Compared_View_Element>" text with "<This is the Compared View.>" on "<Compare_Page>" page
    
    
    Note: Ensure the step definition methods are generic, using webelement declarations with names like ele1, ele2, etc., and avoiding duplicate step definitions. Also, use the provided <load_and_find_page_element> method for finding web elements. Do not include commented lines related to Selenium commands, and make sure each missing step generates only once.
    
    '''


def read_existing_steps(path):
    data = read_file(path)
    return ''' I have these step definitions already so please generate those I don't have''' + data


def ac_create_query_step(file_path):
    helper_data = '''
    1. Use Java as programming language.
    1. Generate all possible test cases for provided acceptance criteria with actual complete implementation.
    2. Generate detailed Selenium step definitions in Java for all actions.
    3. Utilize the <load_and_find_page_element> method to locate web elements. 
    4. Ensure that all code within step definitions is solely related to Selenium commands. 
    5. Avoid adding commented lines pertaining to Selenium commands in the step definitions.
    6. Kindly produce a comprehensive end-to-end code, and refrain from making any alterations to the code 
    generated by ChatGPT.
    7. Here is my input.
    '''
    data = read_file(file_path)
    return helper_data + data
    #
    #
    # individual_test_cases = 'Generate selenium step_definition for all in detail using java and ' \
    #                         'use these methods <load_and_find_page_element> to find the web element. and I ' \
    #                         'want all code inside step definition only related to selenium commands.' \
    #                         'Do not add commented line in step definition related to selenium commands.' \
    #                         ' Please generate complete end to end code ' \
    #                         'and will not change ' \
    #                         'anything in code generated by chatgpt\n'+addon
    # return individual_test_cases

    #
    # Modify the given Gherkin scenario without changing the sequence or meaning.
    # Combine steps that involve the same subsequent actions, but keep distinct steps separate.
    # Do not alter the order of the steps or the scenario's intended outcome.
    # Ensure that the scenario retains its original structure and logic.

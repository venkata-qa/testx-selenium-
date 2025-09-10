import re

# Read the feature file
with open('response.feature', 'r') as file:
    feature_file = file.read()

# Read the step definition file
with open('common.steps.ts', 'r') as file:
    step_definition_file = file.read()

# Find all the steps in the feature file
steps = re.findall(r'(Given|When|Then|And|But)\s+(.*)', feature_file)

# Find all the step definitions in the step definition file
step_definitions = re.findall(r"(Given|When|Then|And|But)(?:/)?\((['\"])(.*?)\2\s*,", step_definition_file)
#print(step_definitions)
step_definitions = [(step, re.sub(r"{.*?}", '""', definition)) for step, _, definition in step_definitions]
print(step_definitions)
# Create a list of implemented steps
implemented_steps = [re.sub(r'[^\w\s"]', '', step[1]) for step in step_definitions]
#implemented_steps = [re.sub(r'<.*?>', '', step[1]) for step in step_definitions]
print(implemented_steps)
# Find the steps that are not implemented
not_implemented_steps = []
for step in steps:
    step_without_params = re.sub(r'".*?"', '""', step[1])
    print("without params",step_without_params)
    implemented = False
    for impl_step in implemented_steps:
        if step_without_params in impl_step:
            implemented = True
            break
    if not implemented:
        not_implemented_steps.append(step)

# Print the steps that are not implemented
if not_implemented_steps:
    print('The following steps are not implemented:')
    for step in not_implemented_steps:
        print(f'{step[0]} {step[1]}')
else:
    print('All steps are implemented.')

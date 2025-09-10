from datetime import datetime
import warnings
from langchain_community.chat_models import ChatOpenAI
from langchain_community.document_loaders import DirectoryLoader
from langchain.indexes import VectorstoreIndexCreator

from utils.file_operation import *
from utils.path import find_project_root


warnings.filterwarnings("ignore")
project_root = find_project_root()

# Set the OpenAI API key
os.environ["OPENAI_API_KEY"] = get_api_token(f'{project_root}/utilities/script/input/token.json')

# Specify the folder containing feature files
folder_path = get_latest_folder(f'{project_root}/utilities/script/result/feature')

# Initialize a dictionary to store feature contents with file names as keys
feature_contents_dict = {}

for root, dirs, files in os.walk(folder_path):
    for file_name in files:
        file_path = os.path.join(root, file_name)

        with open(file_path, "r") as file:
            feature_contents = file.read()

        feature_contents_dict[file_name] = feature_contents

    current_date = datetime.now().strftime("%Y_%m_%d_%H_%M_%S")+'_Temp'
    folder_path = os.path.join(f'{project_root}/utilities/script/result/feature', current_date)
    os.makedirs(folder_path, exist_ok=True)

for file_name, feature_contents in feature_contents_dict.items():
    file_path = os.path.join(folder_path, f"{file_name}")

    print(f"StepDefinition will get at '{file_path.split('ChatGPT', 1)[1]}' location")

    individual_test_cases = temp_feature_helper_data() + feature_contents

    loader = DirectoryLoader(f"{project_root}/utilities/script/master_data/temp_feature_data", glob="*.txt")
    # Create a VectorstoreIndex using the TextLoader
    index = VectorstoreIndexCreator().from_loaders([loader])

    result = index.query(individual_test_cases.strip(), llm=ChatOpenAI())

    with open(file_path, "w") as file:
        file.write(str(result))


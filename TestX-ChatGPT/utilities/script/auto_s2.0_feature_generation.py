from datetime import datetime
import warnings
from langchain_community.chat_models import ChatOpenAI
from langchain_community.document_loaders import DirectoryLoader
from langchain.indexes import VectorstoreIndexCreator

from utils.file_operation import *
from utils.path import find_project_root

warnings.filterwarnings("ignore")
root = find_project_root()
# Set the OpenAI API key
os.environ["OPENAI_API_KEY"] = get_api_token(f'{root}/utilities/script/input/token.json')
test_cases_file_path = f'{root}/utilities/script/input/input.txt'

test_cases = feature_query_data(test_cases_file_path)
# Split the multi-line string into individual test cases
individual_test_cases = test_cases.split('\n\n')

# Get the current date in the format YYYY-MM-DD
current_date = datetime.now().strftime("%Y_%m_%d_%H_%M_%S")

# Create a folder for the current date
folder_path = os.path.join(f'{root}/utilities/script/result/feature', current_date)
os.makedirs(folder_path, exist_ok=True)

# Iterate over the individual test cases
for i, query in enumerate(individual_test_cases, start=1):
    file_path = os.path.join(folder_path, f"feature_result_{current_date}_{i}.feature")
    print(f"Feature file will get at '{file_path.split('ChatGPT', 1)[1]}' location")
    # Load text from 'data.txt' using TextLoader
    loader = DirectoryLoader(f"{root}/utilities/script/master_data/feature_data", glob="*.txt")
    # Create a VectorstoreIndex using the TextLoader
    index = VectorstoreIndexCreator().from_loaders([loader])

    # Print the result of querying the index with the provided query
    final_query = feature_helper_data()+query
    result = index.query(final_query.strip(), llm=ChatOpenAI())

    # Open the file in write mode and save the result
    with open(file_path, "w") as file:
        file.write(str(result))
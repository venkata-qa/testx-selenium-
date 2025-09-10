# TestXpander

<!-- TABLE OF CONTENTS -->
<details open="open">
    <summary>Table of Contents</summary>
    <ol>
        <li><a href="#about-GenAI-Testing-Tool">About GenAI Testing Tool</a></li>
        <li><a href="#supported-frameworks">Supported Frameworks</a></li>     
        <li>
            <a href="#getting-started">Getting started</a>
            <ul>
               <li><a href="#prerequisites">Prerequisites</a></li>
            </ul>
        </li>
        <li><a href="#Steps-to-Setup">Steps to Setup</a></li>
        <li><a href="#usage">Usage</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#contact">Contact</a></li>
    </ol>
</details>
<!--
# Table of Contents
1.  About accessibility testing using axe-core
2.  Supported browsers
3.  Getting started
    *  Prerequisites
    *  Installation
4.  Usage
5.  Contact
-->

## About TestXpander

TestXpander is a genAI based solution that plugs-in with any cucumber-based testing framework and enables auto-generation, auto-correction and auto-expanding of scenarios in feature files. 
The plug-in learns the existing cucumber steps and automatically maps them to the newly written feature files. The solution is language-agnostic and works with any framework with an accuracy of 70%. It uses genAI prompt engineering framework with gherkin language model. The plug-in is configurable to connect to any GenAI engines like PS Chat, Microsoft Bing Chat, OpenAI etc. 

We plan to embed this plug-in in all TestX cucumber-based accelerators by end of February to enable GenAI capabilities. The next version (WIP) will enable framework model learning and auto-code generation using framework-specific libraries, webelements, etc.

## Supported Frameworks

The GenAI Testing Tool supports all cucumber-based automation frameworks, including those written in Java and JavaScript languages.

## Getting started

### Prerequisites
To use the GenAI Testing Tool, you need to have the following installed on your system:

1. Java Development Kit (JDK) 8 or higher
2. Python
3. Shell

## Steps to Setup

For Autocorrecting Feature File:
1. Step Definition Files: Place all necessary step definition files inside the stepDefinition folder. These files will be used to correct feature files.
2. Clear Steps: Write clear and concise steps in the feature file. Each step should describe a specific action or verification point. Avoid ambiguity.
3. Step Parameters: If your automation framework's step definitions use parameters, make sure the number of parameters in the feature file steps matches those in the step definitions.

For Creating Blank Step Definition Code:
1. Place the feature file steps in the StepDefRequest.feature file.

## Usage

To use the GenAI Testing Tool, follow these steps:

1. Setup: Clone the repository to your local machine and install the necessary dependencies.
2. ChatGpt Token: Update the token.json file with your ChatGpt token.
3. Running the Tool: Use the command sh runfile.sh in your bash terminal to start the tool.
4. Creating Feature File Steps: 
    1. Input your raw feature file scenario in request.feature file.
    2. When prompted, type 1 to autocorrect feature file steps.
    3. The updated feature file will be saved in response.feature.
5. Creating Step Definition Code:
    1. Input your feature file scenario steps in StepDefRequest.feature.
    2. When prompted, type 2 and then specify the language (java or js) for the step definition code.
    3. The step definition code will be saved in StepDefinitions.java or StepDefinitions.js based on your input.

## Contributing
Link to contribution guidelines: [https://psinnersource.lioncloud.net/contribute](https://psinnersource.lioncloud.net/contribute)

If you would like to contribute to the GenAI Testing Tool, please follow these steps:

1. Fork the repository.
2. Create a new branch for your changes.
3. Make your changes and commit them.
4. Push your changes to your fork.
5. Create a pull request.


## Contact
* [Rahul Verma](mailto:rahverma3@publicisgroupe.net)
* [Sukesh Jha](mailto:sukesh.jha@publicissapient.com)
* [Mohit Mair](mailto:mohmair@publicisgroupe.net)

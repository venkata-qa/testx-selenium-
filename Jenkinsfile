pipeline {
    agent any

stage("Build UI Test"){
    steps{
    echo "Starting UI Test Cases"
    bat "mvn clean install -DTest=RunUITest"
    
}}
stage("UI Results"){
    steps{
        
publishHTML([allowMissing: false,
         alwaysLinkToLastBuild: true,
         keepAll: true,
         reportDir: 
        '/target/cucumber-html-reports',
         reportFiles: 'overview-features.html',
         reportName: 'overview-features'
         ])
    
}}

stage("Build API Test"){
    steps{
    echo "Starting API testing"
    bat "mvn clean install -DTest=RunAPITest"
}}
 stage(" API Results"){
    steps{
        
publishHTML([allowMissing: false,
         alwaysLinkToLastBuild: true,
         keepAll: true,
         reportDir: 
        '/target/cucumber-html-reports',
         reportFiles: 'overview-features.html',
         reportName: 'overview-features'
         ])
    
}}}
stage("Build API-UI Test"){
    steps{
    echo "Starting API-UI testing"
    bat "mvn clean install -DTest=RunUITest"
}}
 stage(" API-UI Results"){
    steps{

publishHTML([allowMissing: false,
         alwaysLinkToLastBuild: true,
         keepAll: true,
         reportDir:
        '/target/cucumber-html-reports',
         reportFiles: 'overview-features.html',
         reportName: 'overview-features'
         ])

}}}
stage("Build BE Test"){
    steps{
    echo "Starting BE testing"
    bat "mvn clean install -DTest=RunBETest"
}}
 stage(" BE Results"){
    steps{

publishHTML([allowMissing: false,
         alwaysLinkToLastBuild: true,
         keepAll: true,
         reportDir:
        '/target/cucumber-html-reports',
         reportFiles: 'overview-features.html',
         reportName: 'overview-features'
         ])

}}}
    }

# reports
Stateless services to generate PDF reports

## to read:
1. https://docs.microsoft.com/en-us/azure/azure-functions/functions-create-first-java-maven

## Tech stack:
1. Apache PdfBox to generate Pdf file
2. Serverless on Azure 

## To run locally abnd test
* run
  mvn clean package 
  mvn azure-functions:run
* test
  http://localhost:7071/api/HttpTrigger-Java

## To push application to Azure:
mvn azure-functions:deploy

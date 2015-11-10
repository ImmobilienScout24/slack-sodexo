#!/bin/bash
CFN_PARAMS="--region eu-west-1 --stack-name utm-dev-25 --template-body file://templates/utm.json --parameters ParameterKey=stage,ParameterValue=dev25 ParameterKey=locName,ParameterValue=dev"

source with-afp-credentials-and-aws-tools.sh

functionName="Sodexo"

echo "Build binary"
sh sbt assembly
echo "Delete funtion $funtionName"
aws lambda delete-function --function-name "$functionName"
echo "Create funtion $funtionName"
aws lambda create-function --timeout 15 --function-name "$functionName" --runtime "java8" --role "arn:aws:iam::285072396330:role/lambda_basic_execution" --handler "de.is24.Sodexo::handleCall" --zip-file "fileb://target/scala-2.11/slack-sodexo-assembly-0.1.1-SNAPSHOT.jar"

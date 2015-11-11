#!/bin/bash
CFN_PARAMS="--region eu-west-1 --stack-name utm-dev-25 --template-body file://templates/utm.json --parameters ParameterKey=stage,ParameterValue=dev25 ParameterKey=locName,ParameterValue=dev"

source with-afp-credentials-and-aws-tools.sh

functionName="Sodexo"

echo "Build binary"
sh sbt assembly
echo "create bucket"
aws --region eu-west-1 s3 mb "s3://sodexo-slack" || echo "Bucket already exists"
echo "update function $functionName"
aws --region eu-west-1 lambda update-function-code --function-name "$functionName" --zip-file "fileb://target/scala-2.11/slack-sodexo-assembly-0.1.1-SNAPSHOT.jar" || {
    echo "update failed, creating the function"
    aws --region eu-west-1 lambda create-function --timeout 60 --function-name "$functionName" --runtime "java8" --role "arn:aws:iam::285072396330:role/lambda_basic_execution" --handler "de.is24.Sodexo::handleCall" --zip-file "fileb://target/scala-2.11/slack-sodexo-assembly-0.1.1-SNAPSHOT.jar"
}
aws --region eu-west-1 lambda update-function-configuration --function-name "$functionName" --memory-size 256 --timeout 60

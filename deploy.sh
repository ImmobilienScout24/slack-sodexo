#!/bin/bash
CFN_PARAMS="--region eu-west-1 --stack-name utm-dev-25 --template-body file://templates/utm.json --parameters ParameterKey=stage,ParameterValue=dev25 ParameterKey=locName,ParameterValue=dev"

source with-afp-credentials-and-aws-tools.sh

functionName="Sodexo"

echo "Build binary"
sh sbt assembly
echo "create bucket"
aws --region eu-west-1 s3 mb "s3://sodexo-slack" || echo "Bucket already exists"
echo "upload update function $functionName"
aws s3 cp "target/scala-2.11/slack-sodexo-assembly-0.1.1-SNAPSHOT.jar" "s3://sodexo-slack/sodexo.jar"

aws --region eu-west-1 lambda update-function-code --function-name "$functionName" --s3-bucket "sodexo-slack" --s3-key "sodexo.jar" || {
    echo "update failed, creating the function"
    aws --region eu-west-1 lambda create-function --timeout 60 --function-name "$functionName" --runtime "java8" --role "arn:aws:iam::285072396330:role/lambda_basic_execution" --handler "de.is24.Sodexo::handleCall" --code 'S3Bucket=sodexo-slack,S3Key=sodexo.jar'
}
aws --region eu-west-1 lambda update-function-configuration --function-name "$functionName" --memory-size 256 --timeout 60

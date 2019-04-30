#!/bin/bash

functionName="Sodexo"
slackFunctionName="sodexo-slack-command"

echo "Build binary"
./sbt assembly
echo "create bucket"
aws --region eu-west-1 s3 mb "s3://sodexo-slack" || echo "Bucket already exists"
aws s3api put-bucket-policy --bucket "sodexo-slack" --policy "file://bucketPolicy.json"

echo "upload update function $functionName"
aws s3 cp "target/scala-2.11/slack-sodexo-assembly-0.1.1-SNAPSHOT.jar" "s3://sodexo-slack/sodexo.jar"
echo "upload slack function $slackFunctionName"
rm lamda-slack-command.zip
zip lamda-slack-command.zip js/*.js
aws s3 cp "lamda-slack-command.zip" "s3://sodexo-slack/lambda-slack-command.zip"

echo "Create or update image fetching function"
aws --region eu-west-1 lambda update-function-code --function-name "$functionName" --s3-bucket "sodexo-slack" --s3-key "sodexo.jar" || {
    echo "update failed, creating the function"
    aws --region eu-west-1 lambda create-function --timeout 60 --function-name "$functionName" --runtime "java8" --role "arn:aws:iam::285072396330:role/lambda_basic_execution" --handler "de.is24.Sodexo::handleCall" --code 'S3Bucket=sodexo-slack,S3Key=sodexo.jar'
}

echo "Create or update slack lambda function"
aws --region eu-west-1 lambda update-function-code --function-name "$slackFunctionName" --s3-bucket "sodexo-slack" --s3-key "lambda-slack-command.zip" || {
    echo "update failed, creating the function"
    aws --region eu-west-1 lambda create-function --timeout 3 --function-name "$slackFunctionName" --runtime "nodejs" --role "arn:aws:iam::285072396330:role/lambda_basic_execution" --handler "js/index.handler" --code 'S3Bucket=sodexo-slack,S3Key=lambda-slack-command.zip'
}
aws --region eu-west-1 lambda update-function-configuration --function-name "$functionName" --memory-size 512 --timeout 60
aws --region eu-west-1 lambda update-function-configuration --function-name "$slackFunctionName" --memory-size 128 --timeout 3

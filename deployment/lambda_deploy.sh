#!/bin/bash

# IMPORTANT
# Run this script in the root directory of the project

set -e
# Assemble the JAR file
sbt clean compile assembly

# Define variables
JAR_PATH="target/scala-2.13/PithyBackendRoot.jar"  # Adjust path as needed
S3_BUCKET="pithydeployment"
S3_KEY="PithyBackendRoot.jar"
LAMBDA_FUNCTION_NAME="processUserInput"

# Upload JAR to S3
aws s3 cp $JAR_PATH s3://$S3_BUCKET/$S3_KEY

# Update Lambda function
aws lambda update-function-code \
  --function-name $LAMBDA_FUNCTION_NAME \
  --s3-bucket $S3_BUCKET \
  --s3-key $S3_KEY \
  --output text   # this makes the output response more concise

echo "Lambda function updated with new JAR."
#!/bin/bash

# IMPORTANT
# Run this script in the root directory of the project


rm deployment/response.json

# Invoke the Lambda function by sending a test payload
aws lambda invoke --function-name processUserInput \
    --cli-binary-format raw-in-base64-out \
    --payload file://deployment/testUserInput.json deployment/response.json

echo "Payload sent to Lambda function. Check response.json for output."
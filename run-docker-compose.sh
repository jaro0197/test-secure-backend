#!/bin/bash

# Start the application in the background
docker-compose up -d

# Initialize elapsed time
ELAPSED_TIME=0

# Wait for the application to start
while ! nc -z localhost 4001; do
  # If the elapsed time reaches 1800 seconds (30 minutes), exit with error
  if [ $ELAPSED_TIME -eq 1800 ]; then
    echo "Application did not start within 30 minutes. Exiting."
    exit 1
  fi

  # Sleep for 1 second
  sleep 1

  # Increase the elapsed time
  ELAPSED_TIME=$((ELAPSED_TIME+1))

  # Print the elapsed time every 60 seconds
  if [ $((ELAPSED_TIME%60)) -eq 0 ]; then
    echo "Waiting for application to start. Elapsed time: $ELAPSED_TIME seconds."
  fi
done

echo "Application started successfully."

# Run smoke test
RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X 'POST' \
  'http://localhost:4001/users/signin' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "admin",
  "password": "admin"
}')

if [ $RESPONSE_CODE -ne 200 ]; then
  echo "Smoke test failed. Response code: $RESPONSE_CODE. Exiting."
  exit 1
fi

echo "Smoke test passed successfully."
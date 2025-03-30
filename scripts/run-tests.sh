#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Spring Boot User API Test Runner ===${NC}"

# Check if PostgreSQL is running
echo "Checking PostgreSQL status..."
docker ps | grep springboot-db > /dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}PostgreSQL container not running. Please start it first.${NC}"
    exit 1
fi
echo -e "${GREEN}PostgreSQL is running.${NC}"

# Create test database if it doesn't exist
echo "Setting up test database..."
docker exec springboot-db-1 psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'shopdevjava_test'" | grep -q 1
if [ $? -ne 0 ]; then
    echo "Creating shopdevjava_test database..."
    docker exec springboot-db-1 psql -U postgres -c "CREATE DATABASE shopdevjava_test;"
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed to create test database.${NC}"
        exit 1
    fi
    echo -e "${GREEN}Test database created successfully.${NC}"
else
    echo -e "${GREEN}Test database already exists.${NC}"
fi

# Build the project
echo "Building the project..."
./mvnw clean package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}Build failed. Please fix compilation errors first.${NC}"
    exit 1
fi
echo -e "${GREEN}Build successful.${NC}"

# Run the tests
echo "Running tests..."
./mvnw test
TEST_RESULT=$?

# Display result
if [ $TEST_RESULT -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
else
    echo -e "${RED}Some tests failed. Check the logs above for details.${NC}"
fi

# Open test reports
echo -e "${YELLOW}Test reports are available at:${NC}"
echo "target/surefire-reports/"

exit $TEST_RESULT 
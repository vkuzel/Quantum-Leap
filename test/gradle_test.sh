#!/bin/sh

test_command() {
    echo "Running $1..."
    command="$1 > /dev/null 2>&1"
    eval ${command}
    if [ $? -gt 0 ]; then
        echo "Error!!!"
        exit 1
    fi
}

test_command "gradle clean build"
# test_command "gradle clean bootRun"
test_command "gradle clean bootRepackage"
test_command "gradle clean test"
test_command "gradle clean jar"

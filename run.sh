#!/bin/bash
cd "$(dirname "$0")/Code/tanks_scaffold"
gradle wrapper
./gradlew run

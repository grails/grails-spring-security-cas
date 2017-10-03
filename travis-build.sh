#!/usr/bin/env bash

set -e
echo "TRAVIS_TAG          : $TRAVIS_TAG"
echo "TRAVIS_BRANCH       : $TRAVIS_BRANCH"
echo "TRAVIS_PULL_REQUEST : $TRAVIS_PULL_REQUEST"
echo "Publishing archives for branch $TRAVIS_BRANCH"
rm -rf build

EXIT_STATUS=0

./gradlew -q clean
./gradlew -q build || EXIT_STATUS=$?

if [[ $EXIT_STATUS -ne 0 ]]; then
    echo "Check failed"
    exit $EXIT_STATUS
fi

./gradlew :spring-security-cas:bintrayUpload || EXIT_STATUS=$?

exit 0
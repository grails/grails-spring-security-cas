#!/usr/bin/env bash

set -e

EXIT_STATUS=0

./gradlew clean check install --stacktrace || EXIT_STATUS=$?

if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]; then

  echo "Publishing archives for branch $TRAVIS_BRANCH"

  if [[ -n $TRAVIS_TAG ]]; then

    echo "Pushing build to Bintray for tag $TRAVIS_TAG"

    ./gradlew bintrayUpload || EXIT_STATUS=$?
    
    ./publish-docs.sh

  fi
fi

exit $EXIT_STATUS

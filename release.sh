rm -rf build/release
mkdir -p build/release
cd build/release
git clone git@github.com:grails-plugins/grails-spring-security-cas.git
cd grails-spring-security-cas
grails clean
grails compile

gradle bintrayUpload --stacktrace

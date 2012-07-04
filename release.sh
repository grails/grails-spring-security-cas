rm -rf target/release
mkdir target/release
cd target/release
git clone git@github.com:grails-plugins/grails-spring-security-cas.git
cd grails-spring-security-cas
grails clean
grails compile
grails publish-plugin --noScm --stacktrace
#grails publish-plugin --noScm --stacktrace --snapshot

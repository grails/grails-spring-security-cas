rm -rf target/release
mkdir target/release
cd target/release
git clone git@github.com:grails-plugins/grails-spring-security-acl.git
cd grails-spring-security-acl
grails clean
grails compile
grails publish-plugin --noScm --stacktrace
#grails publish-plugin --noScm --stacktrace --snapshot

rm -rf target/release
mkdir target/release
cd target/release
git clone -b 2.x  git@github.com:grails-plugins/grails-spring-security-cas.git
cd grails-spring-security-cas
grails clean
grails compile

#grails publish-plugin --snapshot --stacktrace
grails publish-plugin --stacktrace

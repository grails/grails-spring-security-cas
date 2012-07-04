rm -rf target/release
mkdir target/release
cd target/release
git clone git@github.com:burtbeckwith/grails-app-info.git
cd grails-app-info
grails clean
grails compile
grails publish-plugin --noScm --stacktrace
#grails publish-plugin --noScm --stacktrace --snapshot

grails.project.work.dir = 'target'
grails.project.source.level = 1.6
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		compile('org.springframework.security:spring-security-cas-client:3.0.7.RELEASE') {
			excludes 'spring-security-core', 'spring-security-web', 'servlet-api',
			         'spring-tx', 'spring-test', 'cas-client-core', 'ehcache',
			         'junit', 'mockito-core', 'jmock-junit4'
		}
		compile('org.jasig.cas.client:cas-client-core:3.1.12') {
			excludes 'xmlsec', 'opensaml', 'spring-beans', 'spring-test', 'spring-core',
			         'spring-context', 'log4j', 'junit', 'commons-logging', 'servlet-api'
		}
	}

	plugins {
		compile ':spring-security-core:1.2.7.3'

		build(':release:2.0.3', ':rest-client-builder:1.0.2') {
			export = false
		}

		runtime(":hibernate:$grailsVersion") {
			export = false
		}
	}
}

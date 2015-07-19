grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolver="maven"
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
		mavenRepo 'http://repo.spring.io/milestone' // TODO remove
	}

	dependencies {
		String springSecurityVersion = '3.2.7.RELEASE'

		compile "org.springframework.security:spring-security-cas:$springSecurityVersion", {
			excludes 'cas-client-core', 'commons-logging', 'ehcache', 'fest-assert', 'jcl-over-slf4j', 'junit',
			         'logback-classic', 'mockito-core', 'spring-beans', 'spring-context', 'spring-core',
			         'spring-security-core', 'spring-security-web', 'spring-test', 'spring-web', 'tomcat-servlet-api'
		}

		compile 'org.jasig.cas.client:cas-client-core:3.3.3', {
			excludes 'commons-codec', 'commons-logging', 'junit', 'log4j', 'opensaml', 'servlet-api',
			         'spring-beans', 'spring-context', 'spring-core', 'spring-test', 'xmlsec'
		}
	}

	plugins {
		compile ':spring-security-core:2.0-RC5'

		build (":release:3.1.1", ":rest-client-builder:2.1.1") {
			export = false
		}
	}
}

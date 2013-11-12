grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

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
		String springSecurityVersion = '3.2.0.RC1'

		compile "org.springframework.security:spring-security-cas:$springSecurityVersion", {
			excludes 'cas-client-core', 'commons-logging', 'ehcache', 'fest-assert', 'jcl-over-slf4j', 'junit',
			         'logback-classic', 'mockito-core', 'spring-beans', 'spring-context', 'spring-core',
			         'spring-security-core', 'spring-security-web', 'spring-test', 'spring-web', 'tomcat-servlet-api'
		}

		compile 'org.jasig.cas.client:cas-client-core:3.2.1', {
			excludes 'commons-codec', 'commons-logging', 'junit', 'log4j', 'opensaml', 'servlet-api',
			         'spring-beans', 'spring-context', 'spring-core', 'spring-test', 'xmlsec'
		}
	}

	plugins {
		compile ':spring-security-core:2.0-RC2'

		compile ":hibernate:$grailsVersion", {
			export = false
		}

		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}

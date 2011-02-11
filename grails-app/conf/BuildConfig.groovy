grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir	= 'target/test-reports'
grails.project.docs.output.dir = 'docs' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolution = {

	inherits('global') {
		excludes 'commons-codec' // Grails ships with 1.3, need 1.4
	}

	log 'warn'

	repositories {        
		grailsPlugins()
		grailsHome()
		grailsCentral()

		ebr() // SpringSource  http://www.springsource.com/repository
	}

	dependencies {
		compile('org.springframework.security:org.springframework.security.cas:3.0.4.RELEASE') {
			transitive = false
		}
		compile('org.jasig.cas:com.springsource.org.jasig.cas.client:3.1.8') {
			transitive = false
		}
	}
}

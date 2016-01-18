package spring.security.cas.test

import org.springframework.context.annotation.PropertySource

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

@PropertySource('classpath:/local.properties')
class Application extends GrailsAutoConfiguration {
	static void main(String[] args) {
		GrailsApp.run Application, args
	}
}

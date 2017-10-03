package spring.security.cas

import groovy.transform.CompileStatic
import org.springframework.context.annotation.PropertySource
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

@CompileStatic
@PropertySource('classpath:/local.properties')
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder as SCH

class SecureController {

	@Secured('ROLE_ADMIN')
	def admins() {
		render 'Logged in with ROLE_ADMIN'
	}

	@Secured('ROLE_USER')
	def users() {
		render 'Logged in with ROLE_USER'
	}

	@Secured(['IS_AUTHENTICATED_FULLY'])
	def showAttributes() {
		[principal: SCH.context?.authentication.principal]
	}
}

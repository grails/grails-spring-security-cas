import com.testcas.Role
import com.testcas.User
import com.testcas.UserRole

class BootStrap {

	def springSecurityService

	def init = { servletContext ->

		def roleAdmin = new Role(authority: 'ROLE_ADMIN').save()
		def roleUser = new Role(authority: 'ROLE_USER').save()

		def user = new User(username: 'user', enabled: true,
				password: springSecurityService.encodePassword('user')).save()
		def admin = new User(username: 'admin', enabled: true,
				password: springSecurityService.encodePassword('admin')).save()

		UserRole.create user, roleUser
		UserRole.create admin, roleUser
		UserRole.create admin, roleAdmin, true
	}
}

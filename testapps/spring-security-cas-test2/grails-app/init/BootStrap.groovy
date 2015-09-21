import com.test.Role
import com.test.User
import com.test.UserRole

class BootStrap {

	def init = {

		def roleAdmin = new Role('ROLE_ADMIN').save()
		def roleUser = new Role('ROLE_USER').save()

		def user = new User('user', 'user').save()
		def admin = new User('admin', 'admin').save()

		UserRole.create user, roleUser
		UserRole.create admin, roleUser
		UserRole.create admin, roleAdmin, true
	}
}

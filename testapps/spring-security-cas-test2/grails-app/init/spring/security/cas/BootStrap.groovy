package spring.security.cas

import com.test.Role
import com.test.User
import com.test.UserRole
import groovy.transform.CompileStatic

@CompileStatic
class BootStrap {

    def init = { servletContext ->
        Role roleAdmin = new Role('ROLE_ADMIN').save()
        Role roleUser = new Role('ROLE_USER').save()

        User user = new User('user', 'user').save()
        User admin = new User('admin', 'admin').save()

        UserRole.create user, roleUser
        UserRole.create admin, roleUser
        UserRole.create admin, roleAdmin, true
    }
    def destroy = {
    }
}

includeTargets << grailsScript('_GrailsBootstrap')

serverUrlPrefix = 'http://localhost:9090/cas'
proxyReceptorUrl = '/secure/receptor'
appnumber = 0
projectfiles = new File(basedir, 'webtest/projectFiles')
grailsHome = null
dotGrails = null
projectDir = null
appName = null
pluginVersion = null
pluginZip = null
testprojectRoot = null
deleteAll = false

target(createCasTestApps: 'Creates CAS test apps') {

	def configFile = new File(basedir, 'testapps.config.groovy')
	if (!configFile.exists()) {
		error "$configFile.path not found"
	}

	new ConfigSlurper().parse(configFile.text).each { name, config ->
		echo "\nCreating app based on configuration $name: ${config.flatten()}\n"
		appnumber = 0
		2.times {
			appnumber++
			init name, config
			createApp()
			installPlugin()
			runQuickstart()
			createProjectFiles()
		}
	}
}

private void init(String name, config) {

	pluginVersion = config.pluginVersion
	if (!pluginVersion) {
		error "pluginVersion wasn't specified for config '$name'"
	}

	pluginZip = new File(basedir, "grails-spring-security-cas-${pluginVersion}.zip")
	if (!pluginZip.exists()) {
		error "plugin $pluginZip.absolutePath not found"
	}

	grailsHome = config.grailsHome
	if (!new File(grailsHome).exists()) {
		error "Grails home $grailsHome not found"
	}

	projectDir = config.projectDir
	appName = 'spring-security-cas-test-' + name + '-' + appnumber
	testprojectRoot = "$projectDir/$appName"
	dotGrails = config.dotGrails
}

private void createApp() {

	ant.mkdir dir: projectDir

	deleteDir testprojectRoot
	deleteDir "$dotGrails/projects/$appName"

	callGrails(grailsHome, projectDir, 'dev', 'create-app') {
		ant.arg value: appName
	}
}

private void installPlugin() {

	// install plugins in local dir to make optional STS setup easier
	new File("$testprojectRoot/grails-app/conf/BuildConfig.groovy").withWriterAppend {
		it.writeLine 'grails.project.plugins.dir = "plugins"'
	}

	ant.mkdir dir: "${testprojectRoot}/plugins"
	callGrails(grailsHome, testprojectRoot, 'dev', 'install-plugin') {
		ant.arg value: pluginZip.absolutePath
	}
}

private void runQuickstart() {
	callGrails(grailsHome, testprojectRoot, 'dev', 's2-quickstart') {
		ant.arg value: 'com.testcas'
		ant.arg value: 'User'
		ant.arg value: 'Role'
	}
}

private void createProjectFiles() {
	ant.copy file: "${projectfiles.path}/SecureController.groovy",
	         todir: "${testprojectRoot}/grails-app/controllers"

	ant.copy file: "${projectfiles.path}/BootStrap.groovy",
	         todir: "${testprojectRoot}/grails-app/conf",
	         overwrite: true

	new File("$testprojectRoot/grails-app/conf/Config.groovy").withWriterAppend {
		it.writeLine ''
		it.writeLine "grails.plugins.springsecurity.cas.loginUri = '/login'"
		it.writeLine "grails.plugins.springsecurity.cas.serviceUrl = 'http://localhost:808$appnumber/$appName/j_spring_cas_security_check'"
		it.writeLine "grails.plugins.springsecurity.cas.serverUrlPrefix = '$serverUrlPrefix'"
		it.writeLine "grails.plugins.springsecurity.cas.proxyCallbackUrl = 'http://localhost:808$appnumber/$appName/secure/receptor'"
		it.writeLine "grails.plugins.springsecurity.cas.proxyReceptorUrl = '$proxyReceptorUrl'"
	}

	new File("$testprojectRoot/run.sh").withWriter {
		it.writeLine 'grails clean'
		it.writeLine "grails -Dserver.port=808$appnumber run-app"
	}
	ant.chmod file: "$testprojectRoot/run.sh", perm: '+x'
}

private void deleteDir(String path) {
	if (new File(path).exists() && !deleteAll) {
		String code = "confirm.delete.$path"
		ant.input message: "$path exists, ok to delete?", addproperty: code, validargs: 'y,n,a'
		def result = ant.antProject.properties[code]
		if ('a'.equalsIgnoreCase(result)) {
			deleteAll = true
		}
		else if (!'y'.equalsIgnoreCase(result)) {
			ant.echo "\nNot deleting $path"
			exit 1
		}
	}

	ant.delete dir: path
}

private void error(String message) {
	ant.echo "\nERROR: $message"
	exit 1
}

private void callGrails(String grailsHome, String dir, String env, String action, extraArgs = null) {
	ant.exec(executable: "${grailsHome}/bin/grails", dir: dir, failonerror: 'true') {
		ant.env key: 'GRAILS_HOME', value: grailsHome
		ant.arg value: env
		ant.arg value: action
		extraArgs?.call()
	}
}

setDefaultTarget 'createCasTestApps'

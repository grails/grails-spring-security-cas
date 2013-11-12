includeTargets << new File(springSecurityCorePluginDir, "scripts/_S2Common.groovy")

projectfiles = new File(basedir, 'webtest/projectFiles')
grailsHome = null
dotGrails = null
projectDir = null
appName = null
pluginVersion = null
testprojectRoot = null
deleteAll = false
serverUrlPrefix = 'http://localhost:9090/cas'
proxyReceptorUrl = '/secure/receptor'
appnumber = 0

target(createCasTestApps: 'Creates CAS test apps') {

	def configFile = new File(basedir, 'testapps.config.groovy')
	if (!configFile.exists()) {
		error "$configFile.path not found"
	}

	new ConfigSlurper().parse(configFile.text).each { name, config ->
		printMessage "\nCreating app based on configuration $name: ${config.flatten()}\n"
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

	def pluginZip = new File(basedir, "grails-spring-security-cas-${pluginVersion}.zip")
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
	dotGrails = config.dotGrails + '/' + config.grailsVersion
}

private void createApp() {

	ant.mkdir dir: projectDir

	deleteDir testprojectRoot
	deleteDir "$dotGrails/projects/$appName"

	callGrails grailsHome, projectDir, 'dev', 'create-app', [appName]
}

private void installPlugin() {

	File buildConfig = new File(testprojectRoot, 'grails-app/conf/BuildConfig.groovy')
	String contents = buildConfig.text

	contents = contents.replace('grails.project.class.dir = "target/classes"', "grails.project.work.dir = 'target'")
	contents = contents.replace('grails.project.test.class.dir = "target/test-classes"', '')
	contents = contents.replace('grails.project.test.reports.dir = "target/test-reports"', '')

	contents = contents.replace('//mavenLocal()', 'mavenLocal()')
	contents = contents.replace('repositories {', '''repositories {
mavenRepo 'http://repo.spring.io/milestone' // TODO remove
''')

	contents = contents.replace('grails.project.fork', 'grails.project.forkDISABLED')

	contents = contents.replace('plugins {', """plugins {
runtime ":spring-security-cas:$pluginVersion"
""")

	buildConfig.withWriter { it.write contents }

	callGrails grailsHome, testprojectRoot, 'dev', 'compile', null, true // can fail when installing the functional-test plugin
	callGrails grailsHome, testprojectRoot, 'dev', 'compile'
}

private void runQuickstart() {
	callGrails grailsHome, testprojectRoot, 'dev', 's2-quickstart', ['com.testcas', 'User', 'Role']
}

private void createProjectFiles() {
	ant.copy file: "${projectfiles.path}/SecureController.groovy",
	         todir: "${testprojectRoot}/grails-app/controllers"

	ant.copy file: "${projectfiles.path}/BootStrap.groovy",
	         todir: "${testprojectRoot}/grails-app/conf",
	         overwrite: true

	new File(testprojectRoot, 'grails-app/conf/Config.groovy').withWriterAppend {
		it.write """
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.cas.loginUri = '/login'
grails.plugin.springsecurity.cas.serviceUrl = 'http://localhost:808$appnumber/$appName/j_spring_cas_security_check'
grails.plugin.springsecurity.cas.serverUrlPrefix = '$serverUrlPrefix'
grails.plugin.springsecurity.cas.proxyCallbackUrl = 'http://localhost:808$appnumber/$appName/secure/receptor'
grails.plugin.springsecurity.cas.proxyReceptorUrl = '$proxyReceptorUrl'
"""
	}

	new File(testprojectRoot, 'run.sh').withWriter {
		it.write """\
grails clean
grails compile
grails -Dserver.port=808$appnumber run-app
"""
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
			printMessage "\nNot deleting $path"
			exit 1
		}
	}

	ant.delete dir: path
}

private void error(String message) {
	errorMessage "\nERROR: $message"
	exit 1
}

private void callGrails(String grailsHome, String dir, String env, String action, List extraArgs = null, boolean ignoreFailure = false) {

	String resultproperty = 'exitCode' + System.currentTimeMillis()
	String outputproperty = 'execOutput' + System.currentTimeMillis()

	println "Running 'grails $env $action ${extraArgs?.join(' ') ?: ''}'"

	ant.exec(executable: "${grailsHome}/bin/grails", dir: dir, failonerror: false,
				resultproperty: resultproperty, outputproperty: outputproperty) {
		ant.env key: 'GRAILS_HOME', value: grailsHome
		ant.arg value: env
		ant.arg value: action
		extraArgs.each { ant.arg value: it }
		ant.arg value: '--stacktrace'
	}

	println ant.project.getProperty(outputproperty)

	int exitCode = ant.project.getProperty(resultproperty) as Integer
	if (exitCode && !ignoreFailure) {
		exit exitCode
	}
}

setDefaultTarget 'createCasTestApps'

<html>

<head>
	<title>View CAS Attributes</title>
</head>

<body>
	<H1>CAS Username:</H1>
	<ul>
		<li><sec:username /></li>
	</ul>

	<h1>SpringSecurity Granted Authorities</h1>
	<ul>
		<g:each var="i" in="${principal.authorities}" >
			<li>${i}</li>
		</g:each>
	</ul>

	<h1>Other Attributes</h1>
	<ul>
    	<g:each var="attr" in="${principal.attributes}" >
			<li><h4>${attr.key}</h4>
			<ul>
				<g:if test="${attr.value.class.name == 'java.util.ArrayList'}">
					<g:each var="val" in="${attr.value}" >
		        		<li>${val}</li>
					</g:each>
				</g:if>
				<g:else>
					<li>${attr.value}</li>
				</g:else>
			</ul></li>
        </g:each>
	</ul>
</body>

</html>

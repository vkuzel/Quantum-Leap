# Security

* Only Spring Security's @Pre* and @Post* annotations are enabled. Do not use @Secure or JSR-250 annotations.
* By default all endpoints are secured except for static content on path /assets/** and /webjars/**
* HttpSecurity is based on security of endpoints. If you want to provide an access to an endpoint use @PreAuthorize("permitAll()") annotation.
* Only available authentication method is OAuth2. Tested with google OAuth. The project do not store any passwords.
* Only users that has an email in core.person table can be authenticated.
* Thymeleaf sec:authorize-url attribute does not check the role or authority. This means if the user is authenticated but does not have permission to access the url he will see 410 unauthorized. Use explicit sec:authorize="hasRole('ADMIN')" if you want to check authority.

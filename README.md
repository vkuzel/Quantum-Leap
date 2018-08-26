# Source code cloning 

* After the code is cloned to your local repository, generate missing domain objects so the project can compile.

    `gradle generateJooqDomainObjects`

# Security

* Security is based on Spring Security 5.
* Spring Security `@Pre*` and `@Post*` annotations works. Do not use @Secure or JSR-250 annotations.
* By default all `RequestMapping` endpoints are protected and user has to be authenticated to access any of those. Static content paths like `/assets/**`, `/webjars/**` or `/storage/*` are left unprotected.
* To make an endpoint accessible for everyone, use `@PreAuthorize("permitAll()")` annotation.
* Authentication was tested against Google OAuth2 but it should work with any OAuth2 service. Quantum Leap does not store any passwords.
* Spring Security OAuth2 method works with a login page. If there is only one authentication method configured a login page is skipped and an user is redirected directly to authentication service.
* Login page can be configured by `quantumleap.security.loginPageUrl`, default login page is set to an index page path `\`. This implies two things: a) an index page should be able to display authentication errors and b) Spring Security's [login page generation](https://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html#jc-form) is suppressed. 
* Users that has an email stored in the `core.person` table can be authenticated.
* Thymeleaf `sec:authorize-url` attribute does not check the role or authority. This means if the user is authenticated but does not have permission to access the url he will see 410 unauthorized. Use explicit `sec:authorize="hasRole('ADMIN')"` in case you want to check the authority.

# Files storage

* By default all files are stored in `${user.dir}/storage/` directory. This can be changed by the `quantumleap.file.storage.dir` configuration directive.
* Temporary files are stored in storage subdirectory `tmp/`. Temporary files are automatically deleted one month after their creation time by a cleanup job. Temporary directory can be used as a cache. It is used for persisting generated image thumbnails.
 
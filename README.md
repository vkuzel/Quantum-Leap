# Security

* Spring Security `@Pre*` and `@Post*` annotations works. Do not use @Secure or JSR-250 annotations.
* By default all endpoints are secured except for static content paths `/assets/**` and `/webjars/**`
* HttpSecurity is based on security of endpoints. If you want to provide an access to an endpoint you will have to use `@PreAuthorize("permitAll()")` annotation.
* There is OAuth2, the only available authentication method. Tested with Google OAuth. Quantum Leap does not store any passwords.
* Users that has an email stored in the `core.person` table can be authenticated.
* Thymeleaf `sec:authorize-url` attribute does not check the role or authority. This means if the user is authenticated but does not have permission to access the url he will see 410 unauthorized. Use explicit `sec:authorize="hasRole('ADMIN')"` in case you want to check an authority.

# Files storage

* By default all files are stored in `${user.dir}/storage/` directory. This can be changed by the `file.storage.dir` configuration directive.
* Temporary files are stored in storage subdirectory `tmp/`. Temporary files are automatically deleted one month after their creation time by a cleanup job. Temporary directory can be used as a cache. It is used for persisting generated image thumbnails.
 
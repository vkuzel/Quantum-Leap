# Security

* Only Spring Security's @Pre* and @Post* annotations are enabled. Do not use @Secure or JSR-250 annotations.
* By default all endpoints are secured except for static content on path /assets/** and /webjars/**
* HttpSecurity is based on security of endpoints. If you want to provide an access to an endpoint use @PreAuthorize("permitAll()") annotation.
* Only available authentication method is OAuth2. Tested with google OAuth. The project do not store any passwords.
* Only users that has an email in core.person table can be authenticated.
* Thymeleaf sec:authorize-url attribute does not check the role or authority. This means if the user is authenticated but does not have permission to access the url he will see 410 unauthorized. Use explicit sec:authorize="hasRole('ADMIN')" if you want to check authority.

# Files storage

* By default all files are stored into `${user.dir}/storage` directory. This can be changed by the `file.storage.dir` configuration directive.
* Image thumbnails are stored into `quantum-leap` subdirectory of operating system's temporary directory. It is recommended to set up some kind of cleanup job. On Linux this can be done by running a script `find /tmp/quantum-leap -mtime +30 -type f -exec rm {} \;` as a cronjob.
 
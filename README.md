# Natch 

The start of a REST interface for a forum.

## Release plan

### 0.1

* ~~register: shouldRegisterWithUsernameAndPassword~~
* ~~register: shouldSeeErrorJsonOnBlanksPassed~~
* ~~register: shouldSeeErrorJsonOnExistingUsername~~
* ~~register: shouldSeeErrorJsonOnBadJsonPassed -- deferring until later~~
* ~~register: shouldSeeErrorJsonOnUnknownError~~
* ~~register: shouldSaltPassword~~
* ~~login: shouldLoginWithGoodCredentials~~
* ~~login: shouldSeeJsonErrorOnBadCredentials~~
* ~~login: shouldSeeJsonErrorOnBadJson -- deferring until later~~
* ~~login: shouldSeeJsonErrorOnBlanksPassed~~
* ~~login: shouldReturnAuthKey~~
* ~~login: shouldBeAbleToLoginTwice~~
* ~~login: shouldLoginWithAuthKey~~
* logout: shouldLogout
* logout: shouldSeeErrorJsonIfNotLoggedInAsThatUser

### 0.1 Tech tasks

* Logging intergration
* Re-enable db locking 
* Strings file somewhere
* Tests for entity queries or refactor somewhere else
* Login UUID collision prevention
* ~~tech task: JDNI instead of singleton? -- seems not~~
* ~~tech task: Gradle build~~
* ~~tech task: Gradle war install~~
* ~~tech task: Gradle tests run~~ 
* ~~tech task: Integration tests setup~~
* ~~tech task: Check for existing username in register~~

### Misc tech tasks

* Better war deploy method than cping the war to the directory.
* Better way to clear database on functional tests - put tests in war and access the jpa that way?
* Speed improvements?

### Later features
* Superuser
* Groups
* Password reset
 * Email users
* Make post
* Edit post
* List posts
* Filter posts
* Delete post
* Delete user
* Change user details
* View own user details
* View user details publicly
* Choose what details to display publicly
* Message a user
* Thread posts
* Base64 login / reg
* Intercept json parsing errors and return 400s: register, login
* Login timeout?

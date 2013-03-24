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
* login: shouldReturnAuthKey
* ~~login: shouldBeAbleToLoginTwice~~
* user details: shouldSeeUserDetails
* user details: shouldSeeJsonErrorIfNotLoggedIn
* logout: shouldLogout
* logout: shouldSeeErrorJsonIfNotLoggedIn

### 0.1 Tech tasks

* tech task: Logging intergration
* tech task: Re-enable db locking 
* tech task: Strings file somewhere
* tech task: Tests for entity queries or refactor somewhere else
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
* Message a user
* Thread posts
* Base64 login / reg
* Intercept json parsing errors and return 400s: register, login

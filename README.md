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
* login: shouldntBeAbleToLoginTwiceAndLogoutBackInWithTheFirstKey
* ~~logout: shouldLogout~~
* ~~logout: shouldSeeErrorJsonIfNotLoggedInAsThatUser -- shows 401 if bad auth data~~

### 0.1 Tech tasks

* Logging intergration
* Re-enable db locking 
* Tests for entity queries or refactor somewhere else
* Tests for login header filter
* Login UUID collision prevention
* ~~Strings file somewhere~~
* ~~tech task: JDNI instead of singleton? -- seems not~~
* ~~tech task: Gradle build~~
* ~~tech task: Gradle war install~~
* ~~tech task: Gradle tests run~~ 
* ~~tech task: Integration tests setup~~
* ~~tech task: Check for existing username in register~~

### Misc tech tasks

* Better war deploy method than cping the war to the directory.
* Better way to clear database on functional tests - put tests in war and access the jpa that way?
* Stop the jpa unknown entity problem
* Speed improvements?
* Intercept json parsing errors and return 400s: register, login
* ~~Login timeout? -- No, next login will kill old anyway~~
* Strings file in both test and main java resources?
* ~~Strings file access from unit tests and functional tests~~


### Later features
* Site management
 * Superuser
 * Groups
 * Permissions for actions
* Make post
 * List posts
 * Edit post
 * Filter posts
 * Thread posts
 * Delete post
* View users publicly 
 * Choose what details to display publicly
* Site comms
 * Message a user
* User management 
 * View user details
 * Change user details
 * Password reset
  * Email users
 * Delete user
* Misc
 * Base64 login / reg or maybe ssl?

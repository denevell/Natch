# Natch 

The start of a REST interface for a forum.

## Release plan

### 0.1

* register: shouldRegisterWithUsernameAndPassword
* register: shouldSeeErrorJsonOnBlanksPassed
* register: shouldSeeErrorJsonOnExistingUsername
* register: shouldSeeErrorJsonOnBadJsonPassed
* register: shouldSaltPassword
* login: shouldLoginWithGoodCredentials
* login: shouldReturnAuthKey
* login: shouldSeeJsonErrorOnBadCredentials
* login: shouldSeeJsonErrorOnBlanksPassed
* login: shouldSeeJsonErrorOnBadJson
* login: shouldBeAbleToLoginTwice
* user details: shouldSeeUserDetails
* user details: shouldSeeJsonErrorIfNotLoggedIn
* logout: shouldLogout
* logout: shouldSeeErrorJsonIfNotLoggedIn
* tech task: Logging intergration
* tech task: Gradle build
* tech task: Check for existing username in register

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

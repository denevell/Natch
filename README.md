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
* ~~login: shouldntBeAbleToLoginTwiceAndLogoutBackInWithTheFirstKey~~
* ~~logout: shouldLogout~~
* ~~logout: shouldSeeErrorJsonIfNotLoggedInAsThatUser -- shows 401 if bad auth data~~

### 0.2

* ~~posts: shouldMakePost (creation date, modification date, subject, content, thread id)~~
 * ~~posts: shouldSeeJsonErrorPostMake~~
 * ~~posts: shouldSeeUnauthorisedPostMake~~
* posts: shouldEditPost (subject, content, mod date)
 * posts: shouldSeeJsonErrorEdit
 * posts: shouldSeeUnauthorisedPostEdit
* posts: shouldDeletePost (hard delete)
 * posts: shouldSeeJsonErrorDelete
 * posts: shouldSeeUnauthorisedPostDelete
* threaded posts: Should make a threaded post in response to another
* threaded posts: Should be able to edit thread title and keep thread
* ~~list posts: by modification date~~
* list posts: by thread id, earliest as main post
* list posts: posts in thread by date 

### 0.2 Tech tasks
* ~~Figure out how to relate user to post.~~
* ~~Return UserEntity when logged in~~
* Create a method to delete all posts for testing and update list posts test
  
### Backlog 

* Tech task: Re-enable db locking 
* Tech task: Speed improvements?
* Tech task: Intercept json parsing errors and return 400s: register, login
* Tech task: Eclipse gradle integration 
* Tech task: We're not closing the entity managers when we return with bad user input in models
* Test task: Tests for entity queries or refactor somewhere else
* Test task: Test for post resources adapter
* ~~Test task: Junit tests in a war using normal classes? Jacksons xc and persistence in the war libs seems to have solved it~~
* ~~Tech task: Use JPA from java se.~~
* ~~Tech task: Stop the jpa unknown entity problem~~
* ~~Tech task: Better way to clear database on functional tests - put tests in war and access the jpa that way?~~
* ~~Better war deploy method than cping the war to the directory.~~
* ~~Login timeout? -- No, next login will kill old anyway~~
* ~~Strings file in both test and main java resources?~~
* ~~Strings file access from unit tests and functional tests~~

### Later features
* Site management
 * Superuser
 * Groups
 * Permissions for actions
 * Category/tags only available to some users
* Make post
 * List by category
 * Filter posts
 * Soft delete post
* Site comms
 * Message a user
* User management 
 * View user details
 * View users publicly 
     * Choose what details to display publicly
 * Change user details
     * Change username
 * Password reset
     * Email users
 * Delete user
* Users
    * User id is number not name
        * Able to change username
* Misc
 * Base64 login / reg or maybe ssl?

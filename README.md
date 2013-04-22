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
* ~~posts: shouldEditPost (subject, content, mod date)~~
 * ~~posts: shouldSeeJsonErrorEdit~~
 * ~~posts: shouldSeeUnauthorisedPostEdit~~
* ~~posts: shouldDeletePost (hard delete)~~
 * ~~posts: shouldSeeJsonErrorDelete~~
 * ~~posts: shouldSeeUnauthorisedPostDelete~~
* ~~threaded posts: Should make a threaded post in response to another~~
* ~~threaded posts: Should be able to edit thread title and keep thread -- in main edit functional test~~
* ~~list posts: by modification date~~
* ~~list posts: by thread id, earliest as main post~~
* ~~list posts: posts in thread by date~~

### 0.3

* ~~posts: should escape html content~~
* ~~posts: should add a tag to a post~~
* ~~list posts: should see tags on a post~~
* edit post: edit tag
* threaded posts: should list threads by tag (in last modified order)
* ~~threaded posts: should list threads by date of their last entry~~
* list posts: should paginate list all posts by x number 
* list posts: should paginate list single thread posts by x number 
* list posts: should paginate list threads by x number 

### 0.3 Tech tasks

* threaded posts: move tags to the thread object and not individual posts?
* threaded posts: move subject to thread object?
* threaded posts: moved creation date to thread?
* ~~threaded posts: factory object for creating the thread~~
* ~~threaded posts: fix unit tests for adding a post re the thread factory~~
* ~~threaded posts: listing threads refactor~~
* misc: separate out thread and post code and tests

### Backlog 

* Tech task ###: Add memory dump line to tomcat config.
* Tech task ###: Start using DI
* Tech task ###: Move over to openjpa or fix the strange language problem with eclipselink
* Tech task ###: We're not closing the entity managers when we return with bad user input in models - memory problem.
* Tech task ##: Re-enable db locking 
* Tech task ##: Entity bean null / blank problem?
* Tech task #: Decide whether to use entity query classes - if so test them.
* Test task #: Tests for post resources adapters
* Tech task #: Intercept json parsing errors and return 400s: register, login
* Tech task #: Speed improvements?
* ~~Tech task #: Json auto generated docs~~
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
* Posts
 * Test when a thread has no children
 * Thread rest list should list latest post as well
 * Soft delete post
 * Search
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
 * Limit the thread ids, tags and usernames to certain character
 * Some kind of captcha thing on registeration.

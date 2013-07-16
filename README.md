# Natch 

The start of a REST interface for a forum.

### Current

* Backup database
* New installation and database with newly inserted backup
* Test performance with multiple connections
* Map sql via xml, to ease changes of database / provider, database migration

### Backlog


### Dev complete

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
* ~~posts: shouldMakePost (creation date, modification date, subject, content, thread id)~~
* ~~posts: shouldSeeJsonErrorPostMake~~
* ~~posts: shouldSeeUnauthorisedPostMake~~
* ~~posts: shouldEditPost (subject, content, mod date)~~
* ~~posts: shouldSeeJsonErrorEdit~~
* ~~posts: shouldSeeUnauthorisedPostEdit~~
* ~~posts: shouldDeletePost (hard delete)~~
* ~~posts: shouldSeeJsonErrorDelete~~
* ~~posts: shouldSeeUnauthorisedPostDelete~~
* ~~posts: should add a tag to a post~~
* ~~threaded posts: Should make a threaded post in response to another~~
* ~~threaded posts: Should be able to edit thread title and keep thread -- in main edit functional test~~
* ~~threaded posts: Make it so you can list a new Thread object from the db~~
* ~~edit post: edit tag~~
* ~~threaded posts: should list threads by tag (in last modified order)~~
* ~~threaded posts: should list threads by date of their last entry~~
* ~~list posts: should escape html content~~
* ~~list posts: by modification date~~
* ~~list posts: by thread id, earliest as main post~~
* ~~list posts: posts in thread by date~~
* ~~list posts: should see tags on a post~~
* ~~list posts: should paginate list all posts by x number~~
* ~~list posts: should paginate list single thread posts by x number~~
* ~~list posts: should paginate list threads by x number~~
* ~~list posts: should paginate list threads with tag~~

### Tech Tasks 

* ##: Cannot pass in single item tags list.
* #: Change error return strings to ints
* #: Test for still getting 5 posts if one root, but not the thread, is deleted.
* #: Return user data with logon call
* #: Entity bean null / blank problem?
* #: Start using DI
* #: Intercept json parsing errors and return 400s: register, login
* #: threaded posts: move tags to the thread object and not individual posts?
* #: threaded posts: move subject to thread object?
* #: threaded posts: moved creation date to thread?
* #: Inject the EntityManager via annotations
* #: Re-enable db locking 
* #: Update swagger
* #: Update so you do not need two db calls to get thread.
* ~~Tech task: Disable REST test utilties in production war~~
* ~~Create production war~~
* ~~Return threadid on new thread~~
* ~~Access jpa database in the test.~~
* ~~Refactor functional test to delete the db the jpa way.~~
* ~~Delete the api to delete the database.~~
* ~~Add init method to models to start the entity manager there~~
* ~~Null pointer when deleting the head of a thread~~
* ~~Packaged json objects for use in the frontend.~~
* ~~Update swagger documentation so 'thread' appears as 'threadId'~~
* ~~Move back to eclipselink to test performance with sqlite~~
* ~~Move JPA persistence provider to tomcat lib~~
* ~~Make swagger understand that the 'tags' resource in add post is actually called 'posttags'~~
* ~~Make swaggers requests work - extend .json to request.~~
* ~~Move over to openjpa or fix the strange language problem with eclipselink~~
* ~~Add memory dump line to tomcat config.~~
* ~~Profile speed~~
* ~~Check the memory leak is really fixed in long running tomcat instances~~
* ~~We are not closing the entity managers when we return with bad user input in models~~
* ~~Json auto generated docs~~
* ~~Junit tests in a war using normal classes? Jacksons xc and persistence in the war libs seems to have solved it~~
* ~~Use JPA from java se.~~
* ~~Stop the jpa unknown entity problem~~
* ~~Better way to clear database on functional tests - put tests in war and access the jpa that way?~~
* ~~Better war deploy method than cping the war to the directory.~~
* ~~Login timeout? -- No, next login will kill old anyway~~
* ~~Strings file in both test and main java resources?~~
* ~~Strings file access from unit tests and functional tests~~

### Icebox 

* Code quality based
 * Refactor list thread model to have subject, thread etc and a list of paginated posts.
 * Change ListPostResources to a thread specific entity. 
* Site management
 * Superuser
 * Groups
 * Permissions for actions
 * Category/tags only available to some users
* Posts
 * Move posts into existing threads or new threads
 * More sensible threadid?
 * Test / delete when a thread has no children
 * Thread rest list should list latest post as well
 * Show total number of posts in this listing for pagination information
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

# Natch 

The start of a REST interface for a forum.

### Current
* #: Refactor list thread model to have subject, thread etc and a list of paginated posts.
* ~~Tech task: Functional tests for listing threads~~
* ~~Tech task: Model unit tests for editing a thread~~
* ~~Tech task: Resource unit tests for editing a thread~~
* ~~Tech task: Functional tests for editing a thread~~
* ~~Tech task: Model unit tests for deleting a thread~~
* ~~Tech task: Resource unit tests for deleting a thread~~
* ~~Tech task: Functional tests for deleting a thread~~
* ~~Tech task: Functional tests for listing threads when edited~~
* ~~Tech task: What about a thread text is updated but the latest post has not?~~ 
* ~~Tech task: Unit tests for add post~~
* ~~Tech task: Resource tests for add post~~
* Tech task: Functional tests for add post
* Tech task: Method to list all the posts in a thread
* Tech task: Unit tests for edit post
* Tech task: Resource tests for edit post
* Tech task: Functional tests for edit post
* Tech task: Unit tests for del post
* Tech task: Resource tests for del post
* Tech task: Functional tests for del post
* Tech task: Functional tests for listing threads when added a new post 
* Tech task: Functional tests for listing threads when edited a latest post
* Tech task: Functional tests for listing threads when deleted a latest post

### Backlog

* Get number of posts from the list threads call
* Fuctional test for listing threads when last post in thread was deleted
* Test performance with multiple connections
* Re-enable db locking 

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
* ~~threaded posts: should list threads by thread last modified latest post~~
* ~~list posts: should escape html content~~
* ~~list posts: by modification date~~
* ~~list posts: by thread id, earliest as main post~~
* ~~list posts: posts in thread by date~~
* ~~list posts: should see tags on a post~~
* ~~list posts: should paginate list all posts by x number~~
* ~~list posts: should paginate list single thread posts by x number~~
* ~~list posts: should paginate list threads by x number~~
* ~~list posts: should paginate list threads with tag~~
* ~~Tech task: Disable REST test utilties in production war~~
* ~~Tech task: Create production war~~
* ~~Tech task: Return threadid on new thread~~
* ~~Tech task: Access jpa database in the test.~~
* ~~Tech task: Refactor functional test to delete the db the jpa way.~~
* ~~Tech task: Delete the api to delete the database.~~
* ~~Tech task: Add init method to models to start the entity manager there~~
* ~~Tech task: Null pointer when deleting the head of a thread~~
* ~~Tech task: Packaged json objects for use in the frontend.~~
* ~~Tech task: Update swagger documentation so 'thread' appears as 'threadId'~~
* ~~Tech task: Move back to eclipselink to test performance with sqlite~~
* ~~Tech task: Move JPA persistence provider to tomcat lib~~
* ~~Tech task: Make swagger understand that the 'tags' resource in add post is actually called 'posttags'~~
* ~~Tech task: Make swaggers requests work - extend .json to request.~~
* ~~Tech task: Move over to openjpa or fix the strange language problem with eclipselink~~
* ~~Tech task: Add memory dump line to tomcat config.~~
* ~~Tech task: Profile speed~~
* ~~Tech task: Check the memory leak is really fixed in long running tomcat instances~~
* ~~Tech task: We are not closing the entity managers when we return with bad user input in models~~
* ~~Tech task: Json auto generated docs~~
* ~~Tech task: Junit tests in a war using normal classes? Jacksons xc and persistence in the war libs seems to have solved it~~
* ~~Tech task: Use JPA from java se.~~
* ~~Tech task: Stop the jpa unknown entity problem~~
* ~~Tech task: Better way to clear database on functional tests - put tests in war and access the jpa that way?~~
* ~~Tech task: Better war deploy method than cping the war to the directory.~~
* ~~Tech task: Login timeout? -- No, next login will kill old anyway~~
* ~~Tech task: Strings file in both test and main java resources?~~
* ~~Tech task: Strings file access from unit tests and functional tests~~
* ~~Tech task: Gradle task to check for tomcat deployment~~
* ~~Tech task: Sensible production / development war names.~~
* ~~Tech task: Backup database~~
* ~~Tech task: New installation and database with newly inserted backup~~
* ~~Tech task: Map sql via xml, to ease changes of database / provider, database migration~~
* ~~Tech task: Cannot pass in single item tags list.~~
* ~~Tech task: Escape text in tags.~~
* ~~Tech task: Change ListPostResources to a thread specific entity.~~ 

### Icebox 

* Site management
 * ### Superuser
 * ## Move posts into existing threads or new threads
* Posts
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
 * Change username
 * Password reset
     * Email users
 * Delete user
 * User id is number not name
* Misc
 * Base64 login / reg or maybe ssl?
 * Some kind of captcha thing on registeration.
 * Start using DI
 * Inject the EntityManager via annotations
 * Entity bean null / blank problem?
 * Change error return strings to ints
 * Update swagger
 * Update so you do not need two db calls to get thread.
 * Intercept json parsing errors and return 400s: register, login
 * Return user data with logon call

# Natch 

The REST interface for a forum.

See the README for Natch-Runner pr Natch-JerseyMvc for the icebox, backlog and in dev work.


## TODO

~~ * Post add tests
~~ * Check for jersey error return bodies
~~ * Check for listed posts in add posts tests
* Post delete tests
* Post edit tests
* Thread add tests
* Thread delete tests
* Thread edit tests
* Thread from post tests
* Posts list tests
* Post single tests
* Pust ids test
* Return thread id in add thread and move post to new thread
* Return thread id in move post to new thread

## Later

* Make user service generator give a better logging name so as not to conflict
* Some services generator squashing the loader file
* Fix multiple paths etc in testutils
* Make adding a post with a non existent post id an error
* Stop directory listing

### Dev complete

* ~~register: shouldRegisterWithUsernameAndPassword~~
* ~~register: shouldSeeErrorJsonOnBlanksPassed~~
* ~~register: shouldSeeErrorJsonOnExistingUsername~~
* ~~register: shouldSeeErrorJsonOnBadJsonPassed -- deferring until later~~
* ~~register: shouldSeeErrorJsonOnUnknownError~~
* ~~register: shouldSaltPassword~~
* ~~register: First user is admin user~~
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
* users: Can list all users if admin
* users: Admin can toggle as user as admin
* posts: Should be able to move post as a new thread, keeping the original author, with the admin edited flag set
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
* ~~Admin user can edit a post and leave a marker~~
* ~~Admin user can delete a post and leave a marker~~
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

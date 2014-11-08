package org.denevell.natch.model;


public class ThreadEntityUtils {
	
	public static void updateThreadToRemovePost(ThreadEntity te, PostEntity pe) {
		te.posts.remove(pe);
		if(te.rootPost!=null && te.rootPost.id==pe.id) {
			te.rootPost = null;
		}
		if(te.latestPost!=null && te.latestPost.id==pe.id && te.posts!=null && te.posts.size()>=1) {
			te.latestPost = (te.posts.get(te.posts.size()-1));
		}
		te.numPosts = (te.numPosts-1);
	}	

}

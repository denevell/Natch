package org.denevell.natch.model;

import java.util.Date;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PostEditModel {
  public static int EDITED = 0;
  public static int NOT_YOURS = 1;
  public static int DOESNT_EXIST = 2;

  /**
   * @return -1 on error on a static constant in this class
   */
  int edit(long id, String user, PostEntity postWithEditedData, boolean adminEditing);

  @Service
  public static class PostEditModelImpl implements PostEditModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);

    public int edit(final long id, final String username, final PostEntity editedPostEntity, final boolean adminEditing) {
      try {
        int result = mPostModel.startTransaction().updateEntityOnPermission(id, new Jrappy.UpdateItemOnPermissionCorrect<PostEntity>() {
          @Override
          public boolean update(PostEntity item) {
            if (!adminEditing && !item.username.equals(username)) {
              return false;
            }
            item.content = (editedPostEntity.getContent());
            if (editedPostEntity.getSubject() != null)
              item.subject = (editedPostEntity.getSubject());
            if (editedPostEntity.getTags() != null)
              item.tags = (editedPostEntity.getTags());
            item.modified = (new Date().getTime());
            if (!username.equals(item.username) && adminEditing) {
              item.adminEdited = true;
            }
            return true;
          }
        }, PostEntity.class);
        switch (result) {
        case Jrappy.NOT_FOUND:
          return PostEditModel.DOESNT_EXIST;
        case Jrappy.UPDATED:
          return PostEditModel.EDITED;
        case Jrappy.PERMISSION_DENIED:
          return PostEditModel.NOT_YOURS;
        default:
          return -1;
        }
      } finally {
        mPostModel.commitAndCloseEntityManager();

      }
    }

  }
}

package org.denevell.natch.model;

import java.util.Date;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.ModelResponse;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PostEditModel {

  ModelResponse<Void> edit(long id, String user, PostEntity postWithEditedData, boolean adminEditing);

  @Service
  public static class PostEditModelImpl implements PostEditModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);

    public ModelResponse<Void> edit(final long id, final String username, final PostEntity editedPostEntity, final boolean adminEditing) {
      try {
        int result = mPostModel.startTransaction().updateEntityOnPermission(id, new Jrappy.UpdateItemOnPermissionCorrect<PostEntity>() {
          @Override
          public boolean update(PostEntity item) {
            if (!adminEditing && !item.username.equals(username)) {
              return false;
            }
            item.content = (editedPostEntity.content);
            if (editedPostEntity.subject != null)
              item.subject = (editedPostEntity.subject);
            if (editedPostEntity.tags != null)
              item.tags = (editedPostEntity.tags);
            item.modified = (new Date().getTime());
            if (!username.equals(item.username) && adminEditing) {
              item.adminEdited = true;
            }
            return true;
          }
        }, PostEntity.class);
        switch (result) {
        case Jrappy.NOT_FOUND:
          return new ModelResponse<Void>(404, null);
        case Jrappy.UPDATED:
          return new ModelResponse<Void>(200, null);
        case Jrappy.PERMISSION_DENIED:
          return new ModelResponse<Void>(403, null);
        default:
          return new ModelResponse<Void>(500, null);
        }
      } finally {
        mPostModel.commitAndCloseEntityManager();

      }
    }

  }
}

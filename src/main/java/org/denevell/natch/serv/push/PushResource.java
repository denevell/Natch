package org.denevell.natch.serv.push;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.db.entities.PushEntity;

@XmlRootElement
public class PushResource {
	
	private ArrayList<PushEntity> ids = new ArrayList<PushEntity>();
	
	public ArrayList<PushEntity> getIds() {
		return ids;
	}

	public void setIds(ArrayList<PushEntity> ids) {
		this.ids = ids;
	}

}

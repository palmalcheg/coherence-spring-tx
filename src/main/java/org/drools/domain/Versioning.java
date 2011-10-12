package org.drools.domain;

import java.io.Serializable;

public interface Versioning extends Serializable{
	
	int getVersion();
	void setVersion(int ver);

}

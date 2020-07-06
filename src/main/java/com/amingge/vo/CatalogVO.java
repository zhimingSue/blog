package com.amingge.vo;

import com.amingge.pojo.Catalog;

import java.io.Serializable;
/*
* VO用在商业逻辑层和表示层。各层操作属于该层自己的数据对象，
  这样就可以降低各层之间的耦合，便于以后系统的维护和扩展。
*/

public class CatalogVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Catalog catalog;

    public CatalogVO(){

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
}

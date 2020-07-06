package com.amingge.service;

import com.amingge.pojo.Catalog;
import com.amingge.pojo.User;

import java.util.List;

public interface CatalogService {
//    保存Catalog
    Catalog saveCatalog(Catalog catalog);
//    删除Catalog
    void removeCatalog(Long id);
//    根据id获取Catalog
    Catalog getCatalogById(Long id);
//    获取Catalog列表
    List<Catalog>listCatalogs(User user);

    Catalog insertAnonymousCatalog(Catalog catalog);
}


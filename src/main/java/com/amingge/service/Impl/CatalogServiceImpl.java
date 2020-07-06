package com.amingge.service.Impl;

import com.amingge.pojo.Catalog;
import com.amingge.pojo.User;
import com.amingge.repository.CatalogRepository;
import com.amingge.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    private CatalogRepository catalogRepository;

    @Override
    public Catalog saveCatalog(Catalog catalog) {
        //判断重复
        List<Catalog>list = catalogRepository.findByUserAndName(catalog.getUser(),catalog.getName());
        if (list!=null && list.size()>0){
            throw new IllegalArgumentException("该分类已经存在");
        }
        return catalogRepository.save(catalog);
    }

    @Override
    @Transactional
    public void removeCatalog(Long id) {
        catalogRepository.deleteBlogsByCatalogId(id);
        catalogRepository.deleteById(id);
    }

    @Override
    public Catalog getCatalogById(Long id) {
        return catalogRepository.findById(id).orElse(null);
    }

    @Override
    public List<Catalog> listCatalogs(User user) {
        return catalogRepository.findByUser(user);
    }

    @Override
    public Catalog insertAnonymousCatalog(Catalog catalog) {
        return catalogRepository.saveAndFlush(catalog);
    }
}

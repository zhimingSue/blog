package com.amingge.repository;

import com.amingge.pojo.Catalog;
import com.amingge.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog,Long> {
//     根据用户查询
    List<Catalog>findByUser(User user);
//    根据用户名查询
    List<Catalog>findByUserAndName(User user,String name);

    @Modifying
    @Query(value="DELETE from blog where catalog_id=?",nativeQuery=true)
    void deleteBlogsByCatalogId(Long id);
}

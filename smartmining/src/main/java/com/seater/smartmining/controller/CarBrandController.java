package com.seater.smartmining.controller;

import com.seater.smartmining.entity.CarBrand;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.service.CarBrandServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyPermission;


@RestController
@RequestMapping("/api/carBrand")
public class CarBrandController {
    @Autowired
    private CarBrandServiceI carBrandServiceI;

    @RequestMapping("/query")
//    @RequiresPermissions(PermissionConstants.CAR_BRAND_QUERY)
    public Object query(){
        return carBrandServiceI.findAllByOrderById();
    }

    @RequestMapping("/queryByParam")
    public Result query(CarType type){
        Specification<CarBrand> spec = new Specification<CarBrand>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<CarBrand> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(type != null)
                    list.add(criteriaBuilder.equal(root.get("type").as(CarType.class), type));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(carBrandServiceI.queryAllByParams(spec));
    }

    @RequestMapping("/save")
    @Transactional
    @RequiresPermissions(PermissionConstants.CAR_BRAND_SAVE)
    public Object save(CarBrand carBrand)
    {
        try {
            carBrandServiceI.save(carBrand);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    @RequiresPermissions(PermissionConstants.CAR_BRAND_DELETE)
    public Object delete(Long id)
    {
        try {
            carBrandServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}

package com.seater.smartmining.controller;

import com.seater.smartmining.entity.CarModel;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.service.CarModelServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/carModel")
public class CarModelController {
    @Autowired
    private CarModelServiceI carModelServiceI;

    @RequestMapping("/query")
//    @RequiresPermissions(PermissionConstants.CAR_MODEL_QUERY)
    public Object query(CarType type)
    {
        if(type != null)
            return carModelServiceI.findByTypeOrderById(type);
        else
            return carModelServiceI.findAllByOrderById();
    }

    @RequestMapping("/queryByParam")
    public Result query(CarType type, Long brandId){
        Specification<CarModel> spec = new Specification<CarModel>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<CarModel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(type != null)
                    list.add(criteriaBuilder.equal(root.get("type").as(CarType.class), type));
                if(brandId != null)
                    list.add(criteriaBuilder.equal(root.get("brandId").as(Long.class), brandId));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(carModelServiceI.queryByParams(spec));
    }

    @RequestMapping("/save")
    @Transactional
    @RequiresPermissions(PermissionConstants.CAR_MODEL_SAVE)
    public Object save(CarModel carModel)
    {
        try {
            carModelServiceI.save(carModel);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    @RequiresPermissions(PermissionConstants.CAR_MODEL_DELETE)
    public Object delete(Long id)
    {
        try {
            carModelServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}

package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.Nozzle;
import com.seater.smartmining.service.NozzleServiceI;
import com.seater.smartmining.service.impl.NozzleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description 油枪
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:03
 */
@RestController
@RequestMapping("/api/nozzle")
public class NozzleController {

    @Autowired
    NozzleServiceI nozzleServiceI;
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(@RequestBody Nozzle nozzle, HttpServletRequest request) {
        try {
            Specification<Nozzle> spec = new Specification<Nozzle>() {
                @Override
                public Predicate toPredicate(Root<Nozzle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    list.add(cb.equal(root.get("port").as(Integer.class), nozzle.getPort()));
                    list.add(cb.equal(root.get("oilCarId").as(Long.class), nozzle.getOilCarId()));
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<Nozzle> nozzles = nozzleServiceI.queryWx(spec);
            if (nozzles.size() > 0){
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "已存在油枪端口号");
                }}; 
            }
            nozzleServiceI.save(nozzle);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            nozzleServiceI.delete(id);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/inValid")
    @Transactional
    public Object inValid(Long id) {
        try {
            Nozzle nozzle = nozzleServiceI.get(id);
            nozzle.setValid(!nozzle.getValid());
            nozzleServiceI.save(nozzle);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }
    
    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String port, HttpServletRequest request) {

        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<Nozzle> spec = new Specification<Nozzle>() {
            @Override
            public Predicate toPredicate(Root<Nozzle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (port != null && !port.isEmpty()) {
                    list.add(cb.like(root.get("port").as(String.class), "%" + port + "%"));
                }
                list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));

                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        return nozzleServiceI.query(spec, PageRequest.of(cur, page));
    }

    @RequestMapping("/get")
    public Object get(HttpServletRequest request) {
        try {
            return nozzleServiceI.get(Long.parseLong(request.getHeader("projectId")));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }
}

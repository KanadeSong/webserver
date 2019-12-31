package com.seater.smartmining.controller;

import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.service.InterPhoneMemberServiceI;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:27
 */
@RestController
@RequestMapping("/api/interPhoneMember")
public class InterPhoneMemberController extends BaseController {

    @Autowired
    private InterPhoneMemberServiceI interPhoneMemberServiceI;

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, UserObjectType userObjectType, String interPhoneAccount, Long interPhoneGroupId) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);

        Specification<InterPhoneMember> spec = new Specification<InterPhoneMember>() {

            @Override
            public Predicate toPredicate(Root<InterPhoneMember> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (!ObjectUtils.isEmpty(projectId)) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                }
                if (!ObjectUtils.isEmpty(userObjectType)) {
                    list.add(cb.equal(root.get("userObjectType").as(UserObjectType.class), userObjectType));
                }
                if (!ObjectUtils.isEmpty(interPhoneGroupId)) {
                    list.add(cb.equal(root.get("interPhoneGroupId").as(Long.class), interPhoneGroupId));
                }
                if (!StringUtils.isEmpty(interPhoneAccount)) {
                    list.add(cb.equal(root.get("interPhoneAccount").as(String.class), interPhoneAccount));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return interPhoneMemberServiceI.query(spec, PageRequest.of(cur, page));
    }

    @PostMapping("/save")
    @Transactional
    public Object save(InterPhoneMember interPhoneMember) {
        try {
            interPhoneMemberServiceI.save(interPhoneMember);
            return Result.ok();
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }


    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            interPhoneMemberServiceI.delete(id);
            return Result.ok();
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

}

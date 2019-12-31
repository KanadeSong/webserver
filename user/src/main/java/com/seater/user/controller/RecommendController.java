package com.seater.user.controller;

import com.seater.helpers.DateEditor;
import com.seater.user.entity.JoinStatus;
import com.seater.user.entity.Recommend;
import com.seater.user.entity.RecommendType;
import com.seater.user.entity.SysUser;
import com.seater.user.service.RecommendServiceI;
import com.seater.user.service.SysUserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:44
 */
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    RecommendServiceI recommendServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
    }

    /**
     * 推荐注册
     *
     * @param reCommendId
     * @param beRecommendOpenId
     * @return
     */
    @PostMapping("/join")
    @Transactional
    public Object save(Long reCommendId, String beRecommendOpenId) {

        try {
            //  被推荐人查询数据库
            SysUser sysUser_b = sysUserServiceI.getByOpenId(beRecommendOpenId);
            
            if (sysUser_b == null) {
                // 创建推荐关系
                Recommend recommend = new Recommend();
                recommend.setBeRecommendOpenId(beRecommendOpenId);
                recommend.setRecommendId(reCommendId);
                recommend.setValid(true);
                recommend.setRecommendType(RecommendType.Wx);
                recommendServiceI.save(recommend);
                return new HashMap<String, Object>() {{
                    put("status", "true");
                    put("msg", "操作成功");
                }};
            }
            else{
                return new HashMap<String, Object>() {{
                    put("status", "true");
                    put("msg", "被推荐人已经是会员");
                }};
            }
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

}

package com.seater.smartmining.enums;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:20
 */
public enum ApplyStatus {
    
    Edit("编辑中"),
    Apply("提交审核"),
    Pass("审核通过"), 
    Rejected("已拒绝"),
    Disable("失效"),
    ;
    
    private String value;
    
    ApplyStatus(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}

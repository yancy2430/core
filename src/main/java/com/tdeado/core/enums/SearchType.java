package com.tdeado.core.enums;

public enum SearchType implements BaseEnum<String>{
    NOT(0, "不需要查询"),
    LIKE(1, "模糊查询"),
    EQ(2, "等于"),
    BW(3, "范围"),
    IN(4, "包含"),
    DATE(5, "日期"),
    DATETIME(6, "日期时间");
    private final int code;
    private final String descp;
    SearchType(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    @Override
    public String getValue() {
        return descp;
    }
    /**
     * 根据code获取去value
     * @param code
     * @return
     */
    public static SearchType getValueByCode(Integer code){
        for(SearchType platformFree:SearchType.values()){
            if(code.equals(platformFree.getCode())){
                return platformFree;
            }
        }
        return  null;
    }
}

package com.tdeado.core.enums;

public enum  FieldAlign implements BaseEnum<String>{
    LEFT(1, "left"),
    RIGHT(2, "right"),
    CENTER(3, "center");
    private final int code;
    private final String descp;
    FieldAlign(int code, String descp) {
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
    public static FieldAlign getValueByCode(Integer code){
        for(FieldAlign platformFree:FieldAlign.values()){
            if(code.equals(platformFree.getCode())){
                return platformFree;
            }
        }
        return  null;
    }
}

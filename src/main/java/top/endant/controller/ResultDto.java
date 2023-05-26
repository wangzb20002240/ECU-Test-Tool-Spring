package top.endant.controller;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class ResultDto<T> implements Serializable {
    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map<String, Object> map = new HashMap<>(); //动态数据

    public static <T> ResultDto<T> success(T object) {
        ResultDto<T> r = new ResultDto<>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> ResultDto<T> error(String msg) {
        ResultDto<T> r = new ResultDto<>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public ResultDto<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}

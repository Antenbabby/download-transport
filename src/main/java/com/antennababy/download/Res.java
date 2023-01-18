package com.antennababy.download;

import lombok.Data;

import java.io.Serializable;

@Data
public class Res implements Serializable {
    private String code;
    private String msg;
    private Object data;

    public static Res success(Object data){
        Res res = new Res();
        res.setData(data);
        res.setMsg("成功");
        res.setCode("0");
        return res;
    }
    public static Res success(String msg){
        Res res = new Res();
        res.setMsg(msg);
        res.setCode("0");
        return res;
    }
    public static Res success(){
        Res res = new Res();
        res.setMsg("成功");
        res.setCode("0");
        return res;
    }
    public static Res fail(String msg){
        Res res = new Res();
        res.setMsg(msg);
        res.setCode("1");
        return res;
    }
    public static Res fail(String code,String msg){
        Res res = new Res();
        res.setMsg(msg);
        res.setCode(code);
        return res;
    }
}

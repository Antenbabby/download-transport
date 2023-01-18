package com.antennababy.download;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ListQueryReq implements Serializable {
    String keyWords;
    Integer pageIndex;
}

package com.antennababy.download.app;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ListQueryReq implements Serializable {
    String keyWords;
    Integer pageIndex;
}

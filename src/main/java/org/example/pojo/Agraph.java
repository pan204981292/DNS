package org.example.pojo;

import java.util.ArrayList;
import java.util.List;

/*
    图的邻接表类
 */
public class Agraph {
//    public int n = 8; //顶点数
    public int n = 400; //顶点数
//    public int n = 100; //顶点数
    public List<Integer>[] vnode = new ArrayList[n];
    public int[] venumber = new int[n]; //此顶点连接的边数
}

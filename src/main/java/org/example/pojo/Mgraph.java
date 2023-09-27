package org.example.pojo;
/*
    图的邻接矩阵类
 */
public class Mgraph {
//    public int n = 8; //顶点数
    public int n = 400; //顶点数
//    public int n = 100; //顶点数
    public int e; //边数
    public Double[][] edges = new Double[n][n]; //邻接矩阵
    public int[][] info = new int[n][n]; //边的信息
}

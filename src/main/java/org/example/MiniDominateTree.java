package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yaml.snakeyaml.Yaml;
import org.example.pojo.Road;
import org.example.pojo.TwoNum;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class MiniDominateTree {
    private static double largeNumber = 10000.00;
    private static int vNumber = 0;
    private static int eNumber = 0;
    private static double treeWeight = largeNumber;
    private static double tempTreeWeight = largeNumber;
    private static int tabuLengthX = 0;
    private static int tabuLengthXPlus = 0;
    private static int step = 0;
    private static int round = 0;
    private static Random r;
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/parameter.yaml");
        try (InputStream inputStream = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Map<String, Object>> data = yaml.load(inputStream);
            DecimalFormat df = new DecimalFormat("#0.00");

            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter the dataset number you want to calculate (1-72) : ");
            int datasetNumber = sc.nextInt();

            for (int i = datasetNumber; i <= datasetNumber; i++) {
                String datasetName = "dataset" + i;
                Map<String, Object> dataset = data.get(datasetName);
                String path = (String) dataset.get("path");
                double optimalValue = (double) dataset.get("optimalValue");
                long totalTime = 0;
                double totalValue = 0;
                double bestResult = largeNumber;
                Map<String, Object> map = new HashMap<>();
                for(int j = 1; j <= 10; j++){
                    map = mainProgram(path, optimalValue, j);
                    long bestTime = (long) map.get("bestTime");
                    double result = (double) map.get("result");
                    totalTime += bestTime;
                    totalValue += result;
                    if(result < bestResult){
                        bestResult = result;
                    }
                    System.out.println();
                }
                double averageValue = totalValue / 10;
                long averageTime = totalTime / 10;
                bestResult = Double.parseDouble(df.format(bestResult));
                averageValue = Double.parseDouble(df.format(averageValue));
                System.out.print("数据集 " + path + " 求得的最好值为：" + bestResult + "，");
                if(bestResult >= optimalValue + 0.005){
                    System.out.println("没有到达最优值*****");
                }else if(bestResult < optimalValue + 0.005 && bestResult >= optimalValue - 0.005){
                    System.out.println("到达了最优值*****");
                }else{
                    System.out.println("超过了最优值*****");
                }
                System.out.println("数据集 " + path + " 的平均值是：" + averageValue + "，" + "平均时间是：" + averageTime + " 秒**********");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void writeExeclFile(String execlName, int columnCount, Object[] data) throws IOException {

        FileInputStream inputStream = new FileInputStream("F:/ExamResultTable/" + execlName + ".xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);


        int lastRowIndex = sheet.getLastRowNum();


        for (int i = 1; i <= 1; i++) {
            Row row = sheet.createRow(lastRowIndex + i);
            for (int j = 0; j < columnCount; j++) {
                Cell cell = row.createCell(j);
                if (j < data.length) {
                    cell.setCellValue(data[j].toString());
                } else {
                    cell.setCellValue("");
                }
            }
        }


        FileOutputStream outputStream = new FileOutputStream("F:/ExamResultTable/" + execlName + ".xlsx");
        workbook.write(outputStream);
        outputStream.flush();
        workbook.close();
        inputStream.close();
        outputStream.close();
    }


    private static void createExeclFile(String execlName) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(execlName);


        FileOutputStream outputStream = new FileOutputStream("F:/ExamResultTable/" + execlName + ".xlsx");
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    private static Map<String, Object> mainProgram(String path, double optimalValue, int seed) throws IOException {
        initGlobalVariables(seed);
        long startTime = System.currentTimeMillis();
        long runTime, endTime;
        long bestTime = 0;
        ArrayList<String> arrayList = new ArrayList<>();
        readDataSet(arrayList, path);
        vNumber = Integer.parseInt(arrayList.get(0).split(" ")[0]);
        eNumber = Integer.parseInt(arrayList.get(0).split(" ")[1]);
        double[][] mgraph = new double[vNumber][vNumber];
        ArrayList<Integer>[] agraph = new ArrayList[vNumber];
        initMgraphAndAgraph(mgraph, agraph);
        giveNumberToMgraphAndAgraph(mgraph, agraph, arrayList);
//        outputMgraphAndAgraph(mgraph, agraph);
        ArrayList<Road> treeArrayList = new ArrayList<>();
        ArrayList<Road> tempTreeArrayList = new ArrayList<>();
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> xPlus = new ArrayList<>();
        ArrayList<Integer> xMin = new ArrayList<>();
        int[] uniFind = new int[vNumber];
        kruskal(treeArrayList, arrayList, uniFind);
        giveNumberToX(x);
//        System.out.println("最小生成树的总权值为：" + treeWeight);
//        outputTree(treeArrayList);
        while(true){
            int size = x.size();
            removeLeaveNode(uniFind, treeArrayList, x, agraph);
            if(x.size() == size){
                break;
            }
        }
//        System.out.println("初始支配树的总权值为：" + treeWeight);
//        outputTree(treeArrayList);
        giveNumberToXPlus(xPlus, x);
//        System.out.println("初始支配树中的节点个数为：" + x.size());
        ArrayList<Road> roads = new ArrayList<>();
        initAndSortRoad(roads, x, agraph, mgraph);
        int[] fNodeArray = new int[vNumber];
        double[] fWeightArray = new double[vNumber];
        int[] tabuArray = new int[vNumber];
        boolean firstFlag = true;
        boolean disturbFlag = false;
        while (true) {
            if(!disturbFlag){
                tabuLengthX = x.size() / 2;
                tabuLengthXPlus = xPlus.size() / 2;
            }
            updatef1andf2(x, xPlus, fNodeArray, fWeightArray, agraph, mgraph, roads);
            selectNode(x, xPlus, fNodeArray, fWeightArray, tabuArray, xMin, agraph, mgraph, roads, disturbFlag);
            updateTabuArray(tabuArray);
            step++;
            if(firstFlag && step == 36 * vNumber){
                disturbFlag = true;
                firstFlag = false;
                step = 0;
            }
            if(disturbFlag){
                if(eNumber >= 200){
                    tabuLengthX = 1;
                    tabuLengthXPlus = 4;
                }else{
                    tabuLengthX = x.size() / 3;
                    tabuLengthXPlus = xPlus.size() / 3;
                }
            }
            endTime = System.currentTimeMillis();
            runTime = (endTime - startTime) / 1000;
            if(runTime > 1000){
                System.out.println("此次的最好结果为：" + treeWeight);
//                resetGlobalVariables();
                Map<String, Object> map = new HashMap<>();
                map.put("result", treeWeight);
                map.put("bestTime", bestTime);
                return map;
            }
            if(tempTreeWeight < treeWeight){
                bestTime = runTime;
                treeWeight = tempTreeWeight;
                System.out.println("最小支配树的权值更新为：" + treeWeight + " ****************************************************************");
//                System.out.println("此次更新用了 " + step +" 步");
                step = 0;
                round = 0;
                tempTreeArrayList.clear();
                calculateMinSpanTree(x, tempTreeArrayList, roads);
                treeArrayList.clear();
                treeArrayList.addAll(tempTreeArrayList);
//                verifyFunction(x, xPlus, treeArrayList, agraph);
                if(treeWeight < optimalValue + 0.005){
                    System.out.println("此时的最小支配树权值为：" + treeWeight);
                    System.out.println("此时的随机种子是：" + seed);
//                    System.out.println("此时的参数为："+path+" "+lengthX+" "+lengthXPlus+" "+optimalValue+" "+bestResult+" "+disturbPeriod+" "+disturbFactor+" "+convergStep+" "+tabuReduction+" "+largeDisturbPeriod);
                    verifyFunction(x, xPlus, treeArrayList, agraph);
                    System.out.println("所用时间为：" + runTime + " 秒");
                    System.out.println("x.size == " + x.size());
                    outputTree(treeArrayList);
                    Map<String, Object> map = new HashMap<>();
                    map.put("result", treeWeight);
                    map.put("bestTime", bestTime);
                    return map;
                }
            }
        }
    }


    private static void initGlobalVariables(int seed) {
        largeNumber = 10000.00;
        vNumber = 0;
        eNumber = 0;
        treeWeight = largeNumber;
        tempTreeWeight = largeNumber;
        tabuLengthX = 0;
        tabuLengthXPlus = 0;
        step = 0;
        round = 0;
        r = new Random(seed);
    }


    private static void resetGlobalVariables() {
        largeNumber = 10000.00;
        vNumber = 0;
        eNumber = 0;
        tempTreeWeight = 0;
        tabuLengthX = 0;
        tabuLengthXPlus = 0;
        step = 0;
        round = 0;
        r = new Random(1);
    }


    private static void initMgraphAndAgraph(double[][] mgraph, ArrayList<Integer>[] agraph) {
        for(int i = 0; i < vNumber; i++){
            agraph[i] = new ArrayList();
        }
        for(int i = 0; i < vNumber; i++){
            for(int j = 0; j < vNumber; j++){
                mgraph[i][j] = largeNumber;
            }
        }
    }


    private static void verifyFunction(ArrayList<Integer> x, ArrayList<Integer> xPlus, ArrayList<Road> treeArrayList, ArrayList<Integer>[] agraph) {
        boolean flag = true;
        if((x.size() + xPlus.size()) != vNumber){
            flag = false;
        }
        int[] array = new int[vNumber];
        for(int i = 0; i < x.size(); i++){
            array[x.get(i)] = 1;
        }
        for(int i = 0; i < xPlus.size(); i++){
            array[xPlus.get(i)] = 1;
        }
        for(int i = 0; i < vNumber; i++){
            if(array[i] == 0){
                flag = false;
            }
        }
        int[] array2 = new int[vNumber];
        int node;
        for(int i = 0; i < x.size(); i++){
            node = x.get(i);
            for(int j = 0; j < agraph[node].size(); j++){
                array2[agraph[node].get(j)] = 1;
            }
        }
        for(int i = 0; i < vNumber; i++){
            if(array2[i] == 0){
                flag = false;
            }
        }
        int[] ds = new int[vNumber];
        for(int i = 0; i < vNumber; i++){
            ds[i] = i;
        }
        for(int i = 0; i < treeArrayList.size(); i++){
            int a = getRoot(treeArrayList.get(i).a, ds);
            int b = getRoot(treeArrayList.get(i).b, ds);
            if(a != b){
                ds[b] = a;
            }
        }
        int root = getRoot(x.get(0), ds);
        for(int i = 0; i < x.size(); i++){
            if(getRoot(x.get(i), ds) != root){
                flag = false;
            }
        }
        if(treeArrayList.size() != (x.size() - 1)){
            flag = false;
        }
        double num = 0;
        for(int i = 0; i < treeArrayList.size(); i++){
            num += treeArrayList.get(i).w;
        }
        if(num != treeWeight){
            flag = false;
        }
        if(flag){
            System.out.println("验证函数：结果正确");
        }else{
            System.out.println("验证函数：结果错误");
        }
    }


    private static void updateTabuArray(int[] tabuArray) {
        for(int i = 0; i < vNumber; i++){
            if(tabuArray[i] <= -1){
                continue;
            }
            tabuArray[i]--;
        }
    }


    private static void calculateMinSpanTree(ArrayList<Integer> x, ArrayList<Road> tempTreeArrayList, ArrayList<Road> roads) {
        tempTreeWeight = 0;
        int[] ds = new int[vNumber];
        for(int i = 0; i < vNumber; i++){
            ds[i] = -1;
        }
        int a, b;
        int n = 0;
        for(int i = 0; i < roads.size(); i++){
            a = roads.get(i).a;
            b = roads.get(i).b;
            while(ds[a] >= 0){
                a = ds[a];
            }
            while(ds[b] >= 0){
                b = ds[b];
            }
            if(a != b){
                if(a < b){
                    ds[a] += ds[b];
                    ds[b] = a;
                }else{
                    ds[b] += ds[a];
                    ds[a] = b;
                }
                tempTreeArrayList.add(roads.get(i));
                tempTreeWeight += roads.get(i).w;
                n++;
            }
            if(n == (x.size() - 1)){
                break;
            }
        }
    }


    private static void initAndSortRoad(ArrayList<Road> roads, ArrayList<Integer> x, ArrayList<Integer>[] agraph, double[][] mgraph) {
        for(int i = 0; i < x.size(); i++){
            int node = x.get(i);
            for(int j = 0; j < agraph[node].size(); j++){
                if(x.contains(node) && x.contains(agraph[node].get(j))){
                    if (agraph[node].get(j) > node) {
                        Road road = new Road();
                        road.a = node;
                        road.b = agraph[node].get(j);
                        road.w = mgraph[road.a][road.b];
                        roads.add(road);
                    }
                }
            }
        }
        Collections.sort(roads);
    }


    private static void selectNode(ArrayList<Integer> x, ArrayList<Integer> xPlus, int[] fNodeArray, double[] fWeightArray, int[] tabuArray, ArrayList<Integer> xMin, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads, boolean disturbFlag) {
        if (step == 15 && disturbFlag && !xPlus.isEmpty()) {
            disturb(xPlus, x, tabuArray, fNodeArray, agraph, mgraph, roads, fWeightArray);
            return;
        }
        ArrayList<Integer> arrayList1 = new ArrayList<>();
        calcuNoDominMinArrayList(arrayList1, fNodeArray, tabuArray, xMin);
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        boolean flag = calcuWeightAddMinArrayList(arrayList1, arrayList2, fWeightArray);
        if(!flag){
            arrayList2.clear();
            if(xPlus.isEmpty()){
                calculateConnectArrayListFromX(fWeightArray, arrayList2, x);
            }else{
                calcuConnectListFromXPlus(fWeightArray, arrayList2, xPlus);
            }
        }
        int node = arrayList2.get(r.nextInt(arrayList2.size()));
        int selectNodeX = -1;
        int selectNodeXPlus = -1;
        if(x.contains(node)){
            selectNodeX = node;
        }else{
            selectNodeXPlus = node;
        }
        Map<String, Object> map;
        int xToXPlusNodeInTabu, xPlusToXNodeInTabu;
        map = selectTheBestInTabu(fNodeArray, fWeightArray, tabuArray, x, xPlus, xMin);
        xToXPlusNodeInTabu = (int) map.get("xToXPlusNodeInTabu");
        xPlusToXNodeInTabu = (int) map.get("xPlusToXNodeInTabu");
        if(xToXPlusNodeInTabu != -1){
            if(fWeightArray[xToXPlusNodeInTabu] < treeWeight && fNodeArray[xToXPlusNodeInTabu] == 0){
                x.remove((Integer) xToXPlusNodeInTabu);
                xPlus.add(xToXPlusNodeInTabu);
                tabuArray[xToXPlusNodeInTabu] = tabuLengthXPlus;
                tempTreeWeight = fWeightArray[xToXPlusNodeInTabu];
                calcuRoadsWhenDelete(x, xToXPlusNodeInTabu, agraph, mgraph, roads);
                fNodeArray[xToXPlusNodeInTabu] = 0;
                return;
            }
        }
        if(xPlusToXNodeInTabu != -1){
            if(fWeightArray[xPlusToXNodeInTabu] < treeWeight && fNodeArray[xPlusToXNodeInTabu] == 0){
                xPlus.remove((Integer) xPlusToXNodeInTabu);
                x.add(xPlusToXNodeInTabu);
                tabuArray[xPlusToXNodeInTabu] = tabuLengthX;
                tempTreeWeight = fWeightArray[xPlusToXNodeInTabu];
                calcuRoadsWhenAdd(x, xPlusToXNodeInTabu, agraph, mgraph, roads);
                return;
            }
        }
        if(fWeightArray[node] == largeNumber){
            if(xToXPlusNodeInTabu != -1){
                if(fNodeArray[xToXPlusNodeInTabu] == 0 && fWeightArray[xToXPlusNodeInTabu] < largeNumber){
                    x.remove((Integer) xToXPlusNodeInTabu);
                    xPlus.add(xToXPlusNodeInTabu);
                    tabuArray[xToXPlusNodeInTabu] = tabuLengthXPlus;
                    tempTreeWeight = fWeightArray[xToXPlusNodeInTabu];
                    calcuRoadsWhenDelete(x, xToXPlusNodeInTabu, agraph, mgraph, roads);
                    fNodeArray[xToXPlusNodeInTabu] = 0;
                    return;
                }
            }
            if(xPlusToXNodeInTabu != -1){
                if(fNodeArray[xPlusToXNodeInTabu] == 0 && fWeightArray[xPlusToXNodeInTabu] < largeNumber){
                    xPlus.remove((Integer) xPlusToXNodeInTabu);
                    x.add(xPlusToXNodeInTabu);
                    tabuArray[xPlusToXNodeInTabu] = tabuLengthX;
                    tempTreeWeight = fWeightArray[xPlusToXNodeInTabu];
                    calcuRoadsWhenAdd(x, xPlusToXNodeInTabu, agraph, mgraph, roads);
                    return;
                }
            }
        }
        if(selectNodeX != -1){
            if(fNodeArray[selectNodeX] == 0 && fWeightArray[selectNodeX] < largeNumber){
                x.remove((Integer) selectNodeX);
                xPlus.add(selectNodeX);
                tabuArray[selectNodeX] = tabuLengthXPlus;
                tempTreeWeight = fWeightArray[selectNodeX];
                calcuRoadsWhenDelete(x, selectNodeX, agraph, mgraph, roads);
                fNodeArray[selectNodeX] = 0;
//            System.out.println("x——>xPlus：" + selectNodeX);
            }
        }
        if(selectNodeXPlus != -1) {
            if(fNodeArray[selectNodeXPlus] == 0 && fWeightArray[selectNodeXPlus] < largeNumber){
                xPlus.remove((Integer) selectNodeXPlus);
                x.add(selectNodeXPlus);
                tabuArray[selectNodeXPlus] = tabuLengthX;
                tempTreeWeight = fWeightArray[selectNodeXPlus];
                calcuRoadsWhenAdd(x, selectNodeXPlus, agraph, mgraph, roads);
//            System.out.println("xPlus——>x：" + selectNodeXPlus);
            }
        }
    }


    private static void disturb(ArrayList<Integer> xPlus, ArrayList<Integer> x, int[] tabuArray, int[] fNodeArray, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads, double[] fWeightArray) {
        round++;
//        System.out.println("扰动" + round);
        Collections.sort(x);
//        System.out.println("此时树的权值为：" + tempTreeWeight);
        int disturbNumber = 0;
        if (round < 3000) {
            if(eNumber >= 200){
                disturbNumber = 8;
            }
        }
        if(round >= 3000 && eNumber >= 200){
            disturbNumber = xPlus.size() / 3 + 1;
            round = 0;
        }
        if(round >= 500 && eNumber < 200){
            disturbNumber = xPlus.size() / 3 + 1;
            round = 0;
        }
//        disturbNumber = xPlus.size() / disturbFactor + 1;
        for (int i = 0; i < disturbNumber; i++) {
            disturbFunction(xPlus, x, tabuArray, agraph, mgraph, roads, fWeightArray);
//                disturbFunction2(xPlus, x, tabuArray, fNodeArray, xMin, agraph, mgraph, roads, fWeightArray);
            if(i == (disturbNumber - 1)){
                break;
            }
            updatef1andf2(x, xPlus, fNodeArray, fWeightArray, agraph, mgraph, roads);
        }
        step = 0;
    }


    private static void calculateConnectArrayListFromX(double[] fWeightArray, ArrayList<Integer> arrayList, ArrayList<Integer> x) {
        int n = 0;
        for(int i = 0; i < x.size(); i++){
            if(fWeightArray[x.get(i)] < largeNumber){
                n++;
                if(n == 1){
                    arrayList.add(x.get(i));
                }else{
                    for(int j = 0; j < arrayList.size(); j++){
                        if(fWeightArray[x.get(i)] < fWeightArray[arrayList.get(j)]){
                            arrayList.add(j, x.get(i));
                            break;
                        }
                        if(j == (arrayList.size() - 1)){
                            arrayList.add(x.get(i));
                            break;
                        }
                    }
                }
            }
        }
    }


    private static void disturbFunction(ArrayList<Integer> xPlus, ArrayList<Integer> x, int[] tabuArray, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads, double[] fWeightArray) {
        int disturbNode = xPlus.get(r.nextInt(xPlus.size()));
        xPlus.remove((Integer) disturbNode);
        x.add(disturbNode);
        tabuArray[disturbNode] = tabuLengthX;
        calcuRoadsWhenAdd(x, disturbNode, agraph, mgraph, roads);
        tempTreeWeight = fWeightArray[disturbNode];
    }


    private static void calcuRoadsWhenAdd(ArrayList<Integer> x, int selectNodeXPlus, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads) {
        for(int i = 0; i < agraph[selectNodeXPlus].size(); i++){
            if(x.contains(agraph[selectNodeXPlus].get(i))){
                Road road = new Road();
                if(selectNodeXPlus < agraph[selectNodeXPlus].get(i)){
                    road.a = selectNodeXPlus;
                    road.b = agraph[selectNodeXPlus].get(i);
                }else{
                    road.a = agraph[selectNodeXPlus].get(i);
                    road.b = selectNodeXPlus;
                }
                road.w = mgraph[road.a][road.b];

                int index = Collections.binarySearch(roads, road, new Comparator<Road>() {
                    @Override
                    public int compare(Road o1, Road o2) {
                        if(o1.w > o2.w){
                            return 1;
                        }else if(o1.w < o2.w){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });
                if(index >= 0){
                    roads.add(index, road);
                }else{
                    roads.add(-index - 1, road);
                }
            }
        }
    }


    private static void calcuRoadsWhenDelete(ArrayList<Integer> x, int selectNodeX, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads) {
        for(int i = 0; i < agraph[selectNodeX].size(); i++){
            if(x.contains(agraph[selectNodeX].get(i))){
                Road road = new Road();
                if(selectNodeX < agraph[selectNodeX].get(i)){
                    road.a = selectNodeX;
                    road.b = agraph[selectNodeX].get(i);
                }else{
                    road.a = agraph[selectNodeX].get(i);
                    road.b = selectNodeX;
                }
                road.w = mgraph[road.a][road.b];
                for(int j = 0; j < roads.size(); j++){
                    if(roads.get(j).a == road.a && roads.get(j).b == road.b && roads.get(j).w == road.w){
                        roads.remove(j);
                    }
                }
            }
        }
    }


    private static void calcuConnectListFromXPlus(double[] fWeightArray, ArrayList<Integer> arrayList2, ArrayList<Integer> xPlus) {
        ArrayList<TwoNum> list = new ArrayList<>();
        for(int i = 0; i < xPlus.size(); i++){
            TwoNum twoNum = new TwoNum();
            twoNum.a = xPlus.get(i);
            twoNum.b = fWeightArray[xPlus.get(i)];
            list.add(twoNum);
        }
        Collections.sort(list, new Comparator<TwoNum>() {
            @Override
            public int compare(TwoNum o1, TwoNum o2) {
                if((double) o1.b > (double) o2.b){
                    return 1;
                }else if((double) o1.b < (double) o2.b){
                    return -1;
                }else{
                    return 0;
                }
            }
        });
        for(int i = 0; i < (xPlus.size()/vNumber*5 + 1); i++){
            arrayList2.add((Integer) list.get(i).a);
        }
    }


    private static void calculateXMinWhenAdd(int selectNodeXPlus, ArrayList<Integer> xMin, ArrayList<Integer> x, ArrayList<Integer>[] agraph, double[][] mgraph) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < agraph[selectNodeXPlus].size(); i++){
            if(xMin.contains(agraph[selectNodeXPlus].get(i))){
                list.add(agraph[selectNodeXPlus].get(i));
            }
        }
        xMin.removeAll(list);
    }


    private static void calcuLateXMinWhenDelete(int selectNodeX, ArrayList<Integer> xMin, ArrayList<Integer> x, ArrayList<Integer>[] agraph, double[][] mgraph) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < agraph[selectNodeX].size(); i++){
            if(!x.contains(agraph[selectNodeX].get(i))){
                list.add(agraph[selectNodeX].get(i));
            }
        }
        for(int i = 0; i < list.size(); i++){
            boolean flag = false;
            for(int j = 0; j < x.size(); j++){
                if(mgraph[list.get(i)][x.get(j)] < largeNumber){
                    flag = true;
                }
            }
            if(!flag){
                xMin.add(list.get(i));
            }
//            if(xMin.size() == noDominNumber){
//                break;
//            }
        }
    }


    private static Map<String, Object> selectTheBestInTabu(int[] fNodeArray, double[] fWeightArray, int[] tabuArray, ArrayList<Integer> x, ArrayList<Integer> xPlus, ArrayList<Integer> xMin) {
        Map<String, Object> map = new HashMap<>();
        ArrayList<Integer> arrayList1 = new ArrayList<>();
        calcuInTabuMinArrayList(arrayList1, fNodeArray, tabuArray, xMin);
        int node = -1;
        if (!arrayList1.isEmpty()) {
            ArrayList<Integer> arrayList2 = new ArrayList<>();
            calcuWeightAddMinArrayList(arrayList1, arrayList2, fWeightArray);
//            node = arrayList2.get(r.nextInt(arrayList2.size()));
            node = selectMaxTabuLengthNode(arrayList2, tabuArray);
        }
        int xToXPlusNodeInTabu = -1;
        int xPlusToXNodeInTabu = -1;
        if(x.contains(node)){
            xToXPlusNodeInTabu = node;
        }else{
            xPlusToXNodeInTabu = node;
        }
        map.put("xToXPlusNodeInTabu", xToXPlusNodeInTabu);
        map.put("xPlusToXNodeInTabu", xPlusToXNodeInTabu);
        return map;
    }


    private static int selectMaxTabuLengthNode(ArrayList<Integer> arrayList, int[] tabuArray) {
        int node = -1;
        double min = largeNumber;
        for(int i = 0; i < arrayList.size(); i++){
            if(tabuArray[arrayList.get(i)] < min){
                min = tabuArray[arrayList.get(i)];
                node = arrayList.get(i);
            }
        }
        return node;
    }


    private static void calcuInTabuMinArrayList(ArrayList<Integer> arrayList, int[] fNodeArray, int[] tabuArray, ArrayList<Integer> xMin) {
        int min = vNumber;
        for(int i = 0; i < vNumber; i++){
            if(xMin.contains(i)){
                continue;
            }
            if(fNodeArray[i] < min && tabuArray[i] > 0){
                min = fNodeArray[i];
                arrayList.clear();
            }
            if(fNodeArray[i] == min && tabuArray[i] > 0){
                arrayList.add(i);
            }
        }
    }


    private static boolean calcuWeightAddMinArrayList(ArrayList<Integer> arrayList1, ArrayList<Integer> arrayList2, double[] fWeightArray) {
        double min = largeNumber;
        int node;
        for(int i = 0; i < arrayList1.size(); i++){
            node = arrayList1.get(i);
            if(fWeightArray[node] < min){
                min = fWeightArray[node];
                arrayList2.clear();
            }
            if(fWeightArray[node] == min){
                arrayList2.add(node);
            }
        }
        return min != largeNumber && !arrayList2.isEmpty();
    }


    private static void calcuNoDominMinArrayList(ArrayList<Integer> arrayList, int[] fNodeArray, int[] tabuArray, ArrayList<Integer> xMin) {
        int min = vNumber;
        for(int i = 0; i < vNumber; i++){
            if(xMin.contains(i)){
                continue;
            }
            if(fNodeArray[i] < min && tabuArray[i] <= 0){
                min = fNodeArray[i];
                arrayList.clear();
            }
            if(fNodeArray[i] == min && tabuArray[i] <= 0){
                arrayList.add(i);
            }
        }
    }


    private static void updatef1andf2(ArrayList<Integer> x, ArrayList<Integer> xPlus, int[] fNodeArray, double[] fWeightArray, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads){
        int deleteNodeX;
        for(int i = 0; i < x.size(); i++){
            deleteNodeX = x.get(i);
            x.remove(i);
            fNodeArray[deleteNodeX] = calcuNoDominateNumberWhenDelete(deleteNodeX, x, agraph, mgraph);
            fWeightArray[deleteNodeX] = calcuTreeWeightIncrementWhenDelete(x, agraph, mgraph, roads, deleteNodeX);
            x.add(i, deleteNodeX);
        }
        int deleteNodeXPlus;
        for(int i = 0; i < xPlus.size(); i++){
            deleteNodeXPlus = xPlus.get(i);
            x.add(deleteNodeXPlus);
//            fNodeArray[deleteNodeXPlus] = calcuNoDominateNumberWhenAdd(deleteNodeXPlus, xMin, x, agraph, mgraph);
            fWeightArray[deleteNodeXPlus] = calcuTreeWeightIncrementWhenAdd(x, agraph, mgraph, roads, deleteNodeXPlus);
            x.remove(x.size() - 1);
        }
    }


    private static int calcuNoDominateNumberWhenDelete(int deleteNodeX, ArrayList<Integer> x, ArrayList<Integer>[] agraph, double[][] mgraph) {
        int number = 0;
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < agraph[deleteNodeX].size(); i++){
            if(!x.contains(agraph[deleteNodeX].get(i))){
                list.add(agraph[deleteNodeX].get(i));
            }
        }
        for(int i = 0; i < list.size(); i++){
            boolean flag = false;
            for(int j = 0; j < x.size(); j++){
                if(mgraph[list.get(i)][x.get(j)] < largeNumber){
                    flag = true;
                }
            }
            if(!flag){
                number++;
            }
        }
        return number;
    }


    private static double calcuTreeWeightIncrementWhenAdd(ArrayList<Integer> x, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads, int deleteNodeXPlus) {
        ArrayList<Road> tempRoads = (ArrayList<Road>) roads.clone();
        int[] ds = new int[vNumber];
        double weight = 0;
        int edgeNum = 0;
        for(int i = 0; i < vNumber; i++){
            ds[i] = -1;
        }
        for(int i = 0; i < agraph[deleteNodeXPlus].size(); i++){
            if(x.contains(agraph[deleteNodeXPlus].get(i))){
                Road road = new Road();
                if(deleteNodeXPlus < agraph[deleteNodeXPlus].get(i)){
                    road.a = deleteNodeXPlus;
                    road.b = agraph[deleteNodeXPlus].get(i);
                }else{
                    road.a = agraph[deleteNodeXPlus].get(i);
                    road.b = deleteNodeXPlus;
                }
                road.w = mgraph[road.a][road.b];

                int index = Collections.binarySearch(tempRoads, road, new Comparator<Road>() {
                    @Override
                    public int compare(Road o1, Road o2) {
                        if(o1.w > o2.w){
                            return 1;
                        }else if(o1.w < o2.w){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });
                if(index >= 0){
                    tempRoads.add(index, road);
                }else{
                    tempRoads.add(-index - 1, road);
                }
            }
        }
        int a, b;
        for(int i = 0; i < tempRoads.size(); i++){
            a = tempRoads.get(i).a;
            b = tempRoads.get(i).b;
            while(ds[a] >= 0){
                a = ds[a];
            }
            while(ds[b] >= 0){
                b = ds[b];
            }
            if(a != b){
                if(a < b){
                    ds[a] += ds[b];
                    ds[b] = a;
                }else{
                    ds[b] += ds[a];
                    ds[a] = b;
                }
                weight += tempRoads.get(i).w;
                edgeNum++;
            }
            if(edgeNum == (x.size() - 1)){
                break;
            }
        }
        if(edgeNum < (x.size() - 1)){
            return largeNumber;
        }
        return weight;
    }


    private static double calcuTreeWeightIncrementWhenDelete(ArrayList<Integer> x, ArrayList<Integer>[] agraph, double[][] mgraph, ArrayList<Road> roads, int deleteNodeX) {
        ArrayList<Road> tempRoads = (ArrayList<Road>) roads.clone();
        int[] ds = new int[vNumber];
        double weight = 0;
        int edgeNum = 0;
        for(int i = 0; i < vNumber; i++){
            ds[i] = -1;
        }
        for(int i = 0; i < agraph[deleteNodeX].size(); i++){
            if(x.contains(agraph[deleteNodeX].get(i))){
                Road road = new Road();
                if(deleteNodeX < agraph[deleteNodeX].get(i)){
                    road.a = deleteNodeX;
                    road.b = agraph[deleteNodeX].get(i);
                }else{
                    road.a = agraph[deleteNodeX].get(i);
                    road.b = deleteNodeX;
                }
                road.w = mgraph[road.a][road.b];
                for(int j = 0; j < tempRoads.size(); j++){
                    if(tempRoads.get(j).a == road.a && tempRoads.get(j).b == road.b && tempRoads.get(j).w == road.w){
                        tempRoads.remove(j);
                    }
                }
            }
        }
        int a, b;
        for(int i = 0; i < tempRoads.size(); i++){
            a = tempRoads.get(i).a;
            b = tempRoads.get(i).b;
            while(ds[a] >= 0){
                a = ds[a];
            }
            while(ds[b] >= 0){
                b = ds[b];
            }
            if(a != b){
                if(a < b){
                    ds[a] += ds[b];
                    ds[b] = a;
                }else{
                    ds[b] += ds[a];
                    ds[a] = b;
                }
                weight += tempRoads.get(i).w;
                edgeNum++;
            }
            if(edgeNum == (x.size() - 1)){
                break;
            }
        }
        if(edgeNum < (x.size() - 1)){
            return largeNumber;
        }
        return weight;
    }


    private static void giveNumberToX(ArrayList<Integer> x) {
        for(int i = 0; i < vNumber; i++){
            x.add(i);
        }
    }


    private static void giveNumberToXPlus(ArrayList<Integer> xPlus, ArrayList<Integer> x){
        for(int i = 0; i < vNumber; i++){
            xPlus.add(i);
        }
        for(int i = 0; i < x.size(); i++){
            xPlus.remove((Integer) x.get(i));
        }
    }


    private static void outputTree(ArrayList<Road> treeArrayList) {
        int n = 0;
        System.out.print("这棵树为：");
        for(int i = 0; i < treeArrayList.size(); i++){
            System.out.print(treeArrayList.get(i).a + "——>" + treeArrayList.get(i).b + "  ");
            n++;
            if(n % 20 == 0){
                System.out.println();
            }
        }
        System.out.println();
    }


    private static void removeLeaveNode(int[] uniFind, ArrayList<Road> treeArrayList, ArrayList<Integer> x, ArrayList<Integer>[] agraph) {
        ArrayList<Integer> list = new ArrayList<>(x);
        for(int i = 0; i < vNumber; i++){
            int n = 0;
            for(int j = 0; j < treeArrayList.size(); j++){
                if(treeArrayList.get(j).a == i){
                    n++;
                }
                if(treeArrayList.get(j).b == i){
                    n++;
                }
            }
            if(n > 1){
                list.remove((Integer) i);
            }
        }
        sortLeaveNode(list, treeArrayList);
        for(int i = 0; i < list.size(); i++){
            int deleteNode = list.get(i);
            boolean flag = judgeWhetherDomin(deleteNode, x, agraph);
            if (flag) {
                for(int j = 0; j < treeArrayList.size(); j++){
                    if(treeArrayList.get(j).a == list.get(i) || treeArrayList.get(j).b == list.get(i)){
                        treeWeight -= treeArrayList.get(j).w;
                        treeArrayList.remove(j);
                        uniFind[list.get(i)] = list.get(i);
                        break;
                    }
                }
                break;
            }
        }
    }


    private static void sortLeaveNode(ArrayList<Integer> list, ArrayList<Road> treeArrayList) {
        Collections.sort(treeArrayList, new Comparator<Road>(){
            @Override
            public int compare(Road o1, Road o2){
                if(o1.w > o2.w){
                    return -1;
                }else if(o1.w < o2.w){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
        ArrayList<Integer> list1 = new ArrayList<>();
        for(int i = 0; i < treeArrayList.size(); i++){
            for(int j = 0; j < list.size(); j++){
                if(treeArrayList.get(i).a == list.get(j) || treeArrayList.get(i).b == list.get(j)){
                    list1.add(list.get(j));
                }
            }
        }
        list.clear();
        list.addAll(list1);
    }


    private static boolean judgeWhetherDomin(int deleteNode, ArrayList<Integer> x, ArrayList<Integer>[] agraph) {
        int[] array = new int[vNumber];
        x.remove((Integer) deleteNode);
        for(int j = 0; j < x.size(); j++){
            for(int k = 0; k < agraph[x.get(j)].size(); k++){
                array[agraph[x.get(j)].get(k)] = 1;
            }
        }
        for(int j = 0; j < vNumber; j++){
            if(array[j] == 0){
                x.add(deleteNode);
                return false;
            }
        }
        return true;
    }


    private static void kruskal(ArrayList<Road> treeArrayList, ArrayList<String> arrayList, int[] uniFind) {
        Road[] road = new Road[eNumber];
        for(int i = 0; i < eNumber; i++){
            road[i] = new Road();
        }
        for(int i = 0; i < vNumber; i++){
            uniFind[i] = -1;
        }
        initRoad(arrayList, road);
        Arrays.sort(road);
        int a, b;
        int n = 0;
        for(int i = 0; i < eNumber; i++){
            a = road[i].a;
            b = road[i].b;
            while(uniFind[a] >= 0){
                a = uniFind[a];
            }
            while(uniFind[b] >= 0){
                b = uniFind[b];
            }
            if(a != b){
                if(a < b){
                    uniFind[a] += uniFind[b];
                    uniFind[b] = a;
                }else{
                    uniFind[b] += uniFind[a];
                    uniFind[a] = b;
                }
                treeArrayList.add(road[i]);
                treeWeight += road[i].w;
                n++;
            }
            if(n == (vNumber - 1)){
                break;
            }
        }
    }


    private static int getRoot(int a, int[] uniFind) {
        while(a != uniFind[a]){
            a = uniFind[a];
        }
        return a;
    }


    private static void initRoad(ArrayList<String> arrayList, Road[] road) {
        String s1, s2, s3;
        for(int i = 1; i < arrayList.size(); i++){
            s1 = arrayList.get(i).split(" ")[0];
            s2 = arrayList.get(i).split(" ")[1];
            s3 = arrayList.get(i).split(" ")[2];
            road[i - 1].a = Integer.parseInt(s1);
            road[i - 1].b = Integer.parseInt(s2);
            road[i - 1].w = Double.parseDouble(s3);
        }
    }


    private static void outputMgraphAndAgraph(double[][] mgraph, ArrayList<Integer>[] agraph) {
        for(int i = 0; i < vNumber; i++){
            for(int j = 0; j < vNumber; j++){
                System.out.print(mgraph[i][j] + "\t");
            }
            System.out.println();
        }
        for(int i = 0; i < vNumber; i++){
            System.out.print(i + " => ");
            for(int j = 0; j < agraph[i].size(); j++){
                System.out.print(agraph[i].get(j) + " ");
            }
            System.out.println();
        }
    }


    private static void giveNumberToMgraphAndAgraph(double[][] mgraph, ArrayList<Integer>[] agraph, ArrayList<String> arrayList) {
        int s1, s2;
        double s3;
        for(int i = 1; i <= eNumber; i++){
            s1 = Integer.parseInt(arrayList.get(i).split(" ")[0]);
            s2 = Integer.parseInt(arrayList.get(i).split(" ")[1]);
            s3 = Double.parseDouble(arrayList.get(i).split(" ")[2]);
            mgraph[s1][s2] = s3;
            mgraph[s2][s1] = s3;
            agraph[s1].add(s2);
            agraph[s2].add(s1);
        }
    }


    private static void readDataSet(ArrayList<String> arrayList, String path) throws IOException {
//        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/test.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Range_100/ins_400_1.txt"));
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + path + ".txt"));
        String st;
        while((st = br.readLine()) != null){
            arrayList.add(st);
        }
        br.close();
    }
}
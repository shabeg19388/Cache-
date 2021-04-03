import java.util.*;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.io.*;
//assuming binary address to be of 16 bits 
class cacheMemory1{
    static int[][] mainmem; // 16 bit -> 64KB
    static int cache_Size;
    static int cache_Lines;
    static int block_Size;
    static int mem_Size;
    static int[][] cache;
    static int offset;
    static int sets;
    static int set_Size;
    static int SetnBinLen;
    static HashMap<Integer,String> tagArr;
    static ArrayList<Integer> priority_Q;
    static ArrayList<ArrayList<Integer>> priorityM;
    static int[][] set_Add_List;
    public static void main(String argss[]){
        Scanner scanner;
        priority_Q= new ArrayList<Integer>();
        priorityM=new ArrayList<ArrayList<Integer>>();
        scanner = new Scanner(System.in);
        System.out.println("Enter the cache size: ");
        int cSize;
        cSize= scanner.nextInt();
        System.out.println("Enter the number of cache lines: ");
        int cl;
        cl= scanner.nextInt();
        System.out.println("Enter the block size: ");
        int bSize;
        bSize= scanner.nextInt();
        boolean cSizeBool;
        cSizeBool=powerOf2(cSize); 
        boolean clBool;
        clBool=powerOf2(cl); 
        boolean bSizeBool;
        bSizeBool=powerOf2(bSize); 
        
        if(!cSizeBool){
            System.out.println("Invalid input for cSize");
            System.exit(0);
        }
        if(!clBool){
            System.out.println("Invalid input for cl");
            System.exit(0);
        }
        if(!bSizeBool){
            System.out.println("Invalid input for bSize");
            System.exit(0);
        }
        cache=new int[cl][cSize/cl];
        cache_Size=cSize;
        cache_Lines=cl;
        block_Size=bSize;
        offset=Integer.toBinaryString(bSize-1).length();
        mem_Size=(64*1024)/bSize;
        mainmem= new int[mem_Size][bSize];
        tagArr= new HashMap<Integer,String>();
        for(int i=0;i<cl;i++){
            tagArr.put(i, "");
        }
        for(int i=0;i<mem_Size;i++){
            for(int j=0;j<bSize;j++)
                mainmem[i][j]=-1;
        }
        System.out.println();
        System.out.println("The 3 mappings are: ");
        System.out.println();
        System.out.println("1.Direct Mapping ");
        System.out.println("2.Associative Mapping ");
        System.out.println("3.K-way Associative Mapping ");
        System.out.println();
        System.out.println("Enter the mapping you want: ");
        int input;
        input=scanner.nextInt();
        if(input==1){
            directmapping();
        }
        else if(input==2){
            associativemapping();
        }
        else if(input==3){
            System.out.println("Set size :");
            set_Size=scanner.nextInt();
            sets=cl/set_Size;
            for(int ctr=0;ctr<sets;ctr++)
                priorityM.add(new ArrayList<Integer>());
            SetnBinLen=Integer.toBinaryString(set_Size-1).length();
            set_Add_List= new int[sets][set_Size];
            kWayAssociativeMapping();
        }
        else{
            System.out.println("wrong input");
        }
    }

    // first mapping 
    public static void directmapping(){
        //direct process
        String[] in;
        in=takeInput();
        for(int ctr=0;ctr<in.length;ctr++){
            int i=0;
            String[] arr;
            arr=in[ctr].trim().split(" "); //putting in array
            //System.out.print((ctr+1)+(". "));
            System.out.print("Line No "+(ctr+1)+"\t ");
            if(arr.length==2){
                // write
                int add;
                add=Integer.parseInt(arr[0]);
                int data;
                data=Integer.parseInt(arr[1]);
                String binAdd;
                binAdd=Integer.toBinaryString(add);
                int b;
                b= binAdd.length();
                for(int x=0; x<16-b; x++){
                    binAdd="0"+binAdd; //repeat func
                }
                int cacheLineBinLen;
                cacheLineBinLen=Integer.toBinaryString(cache_Lines-1).length();
                String tag;
                tag=binAdd.substring(0, 16-offset-cacheLineBinLen);
                String offsetStr;
                offsetStr=binAdd.substring(16-offset, 16);
                int blockAdd;
                blockAdd=Integer.parseInt(binAdd.substring(0, 16-offset), 2);
                try{
                    int a;
                    a=mainmem[add/block_Size][Integer.parseInt(offsetStr, 2)]; //blockAdd
                }
                catch(Exception e){
                    System.out.println("Invalid adress at line number:"+(i+1));
                }
                int lineNo;
                lineNo=blockAdd%cache_Lines;
                if(tagArr.get(lineNo).equals("")){
                    cache[lineNo]=mainmem[blockAdd];
                    tagArr.put(lineNo,tag);
                    cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
                    System.out.println("Cache Miss, Block added from main memory");
                }
                else if( tagArr.get(lineNo).equals(tag)){
                    cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
                    System.out.println("Cache Hit");
                }
                else{
                    cache[lineNo]=mainmem[blockAdd];
                    tagArr.put(lineNo,tag);
                    cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
                    System.out.println("Cache Miss, Block replaced");
                }
            }
            else if(arr.length==1){
                // read
                int add;
                add=Integer.parseInt(arr[0]);
                String binAdd;
                binAdd=Integer.toBinaryString(add);
                int b;
                b=binAdd.length();
                for(int y=0; y<16-b; y++){
                    binAdd="0"+binAdd; //repeat func
                }
                int cacheLineBinLen;
                cacheLineBinLen=Integer.toBinaryString(cache_Lines-1).length();
                String tag;
                tag=binAdd.substring(0, 16-offset-cacheLineBinLen);
                String offsetStr;
                offsetStr=binAdd.substring(16-offset, 16);
                int blockAdd;
                blockAdd=Integer.parseInt(binAdd.substring(0, 16-offset), 2);
                try{
                    int a;
                    a=mainmem[add/block_Size][Integer.parseInt(offsetStr, 2)]; //blockAdd
                }
                catch(Exception e){
                    System.out.println("Invalid adress at line number:"+(i+1));
                }
                int lineNo;
                lineNo=blockAdd%block_Size;
                if(tagArr.get(lineNo).equals("")){
                    cache[lineNo]=mainmem[blockAdd];
                    tagArr.put(lineNo,tag);
                    int data;
                    data=cache[lineNo][Integer.parseInt(offsetStr, 2)];
                    if(data==-1){
                        System.out.println("Cache Miss, No data available at the given memory address");
                    }
                    else{
                        
                        System.out.println("Data "+data+"-Cache miss, Block added from Main memory");
                    }
                }
                else if(tagArr.get(lineNo).equals(tag)){
                    int data;
                    data=cache[lineNo][Integer.parseInt(offsetStr, 2)];
                    if(data==-1){
                        System.out.println("Cache Miss, No data available at the given memory address");

                    }
                    else{
                        System.out.println("Data "+data+"-Cache hit");
                    }
                }
                else{
                    cache[lineNo]=mainmem[blockAdd];
                    tagArr.put(lineNo,tag);
                    int data;
                    data=cache[lineNo][Integer.parseInt(offsetStr, 2)];
                    if(data!=-1){
                        System.out.println("Data "+data+"-Cache miss, Block Replaced");
                    }
                    else{
                        System.out.println("Cache Miss, No data available at the given memory address");
                    }
                }
            }
            else{
                System.out.println("Invalid input at line number:"+(i+1));
            }
        }
    }

    // 2nd mapping 
    public static void associativemapping(){
        String[] in;
        in=takeInput();
        int ctr1=0;
        //System.out.println(in.length+"sdsasda");
        while(ctr1<in.length){
        //for(int ctr1=0;ctr1<in.length;ctr1++){
            int i=0;
            String[] arr;
            arr=in[ctr1].trim().split(" ");
            //System.out.println(Arrays.toString(arr)+" asdasdas");
            System.out.print("Line No "+(ctr1+1)+"\t ");
            if(arr.length==2){
                // write
                int add;
                add=Integer.parseInt(arr[0]);
                int data;
                data=Integer.parseInt(arr[1]);
                String binAdd;
                binAdd=Integer.toBinaryString(add);
                int b;
                b=binAdd.length();
                for(int x=0; x<16-b; x++){
                    binAdd="0"+binAdd; //repeat func
                }
                String tag;
                tag=binAdd.substring(0,16-offset);
                String offsetStr;
                offsetStr=binAdd.substring(16-offset,16);
                int blockAdd;
                blockAdd=Integer.parseInt(binAdd.substring(0,16-offset),2);
                try{
                    int a;
                    a=mainmem[add/block_Size][Integer.parseInt(offsetStr,2)];
                }
                catch(Exception e){
                    System.out.println("Invalid adress at line number: "+(i+1));
                }
                int lineNo;
                lineNo=-1;
                for(int j=0; j<cache_Lines; j++){
                    if(tagArr.get(j).equals("")|| tagArr.get(j).equals(tag)){
                        lineNo=j;
                        break;
                    }
                }
                if(lineNo==-1){
                    int index;
                    index=priority_Q.get(0);
                    cache[index]=mainmem[blockAdd];
                    tagArr.put(index,tag);
                    priority_Q.remove(0);
                    priority_Q.add(index);
                    System.out.println("Cache Miss, Block replaced");
                }
                else if(tagArr.get(lineNo).equals("")){
                    if(priority_Q.contains(lineNo)){
                        priority_Q.remove(priority_Q.indexOf(lineNo));
                    }
                    priority_Q.add(lineNo);
                    System.out.println("Cache Miss, Block added from main memory") ;

                    cache[lineNo]=mainmem[blockAdd];
                    tagArr.put(lineNo,tag);
                    cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
            
                }
                else if(tagArr.get(lineNo).equals(tag)){
                    if(priority_Q.contains(lineNo)){
                        priority_Q.remove(priority_Q.indexOf(lineNo));
                    }
                    priority_Q.add(lineNo);
                    System.out.println("Cache Hit");
                    cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
                }   
            }
            else if(arr.length==1){
                // read
                int add;
                add=Integer.parseInt(arr[0]);
                String binAdd;
                binAdd=Integer.toBinaryString(add);
                int b;
                b=binAdd.length();
                for(int y=0; y<16-b; y++){
                    binAdd="0"+binAdd; //repeat func
                }
                String tag;
                tag=binAdd.substring(0,16-offset);
                String offsetStr;
                offsetStr=binAdd.substring(16-offset,16);
                int blockAdd;
                blockAdd=Integer.parseInt(binAdd.substring(0,16-offset),2);
                try{
                    // checking if the address is a valid address or not
                    int a;
                    a=mainmem[add/block_Size][Integer.parseInt(offsetStr,2)];
                }
                catch(Exception e){
                    System.out.println("Invalid adress at line number: "+(i+1));
                    System.exit(0);
                }
                int lineNo=-1;
                for(int m=0; m<cache_Lines; m++){
                    if(tagArr.get(m).equals(tag)){
                        lineNo=m;
                        break;
                    }
                }
                if(lineNo==-1){
                    // block required not present
                    for(i=0;i<cache_Lines;i++){
                        if(tagArr.get(i).equals("")){
                            lineNo=i;
                            break;
                        }
                    }
                    if(lineNo==-1){
                        int index;
                        index= priority_Q.get(0);
                        cache[index]=mainmem[blockAdd];
                        tagArr.put(index,tag);
                        priority_Q.remove(0);
                        priority_Q.add(index);
                        int data1;
                        data1=cache[index][Integer.parseInt(offsetStr,2)];
                        if(data1==-1){
                            System.out.println("Cache Miss, No data available at the given memory address");
                        }
                        else{
                            System.out.println("Data "+data1+"-Cache Miss, Block replaced");
                        }
                    }
                    else{
                        if(priority_Q.contains(lineNo)){
                            priority_Q.remove(priority_Q.indexOf(lineNo));
                        }
                        priority_Q.add(lineNo);
                        cache[lineNo]=mainmem[blockAdd];
                        tagArr.put(lineNo,tag);
                        int data2;
                        data2=cache[lineNo][Integer.parseInt(offsetStr,2)];
                        if(data2==-1){
                            System.out.println("Cache Miss, No data available at the given memory address");
                        }
                        else{
                            System.out.println("Data "+data2+"- Cache Miss, Block added");
                        }
                    }
                }
                else{
                    if(priority_Q.contains(lineNo)){
                        priority_Q.remove(priority_Q.indexOf(lineNo));
                    }
                    priority_Q.add(lineNo);
                    int data3;
                    data3=cache[lineNo][Integer.parseInt(offsetStr, 2)];
                    if(data3==-1){
                        System.out.println("Cache Miss, No data available at the given memory address");
                    }
                    else{
                        System.out.println("Data "+data3+"- Cache Hit");
                    }
                }
            }
            else{
                // invalid input
                System.out.println("Invalid input at line number:"+(i+1));
            }
            ctr1++;
        }
        
    }
    public static void kWayAssociativeMapping(){
        String[] in=takeInput();
        int i;
        i=0;
        for(int j=0;j<in.length;j++){
            String[] arr;
            arr=in[j].trim().split(" ");
            System.out.print("Line No "+(j+1)+"\t ");
            if(arr.length==2){
                // write
                int add;
                add=Integer.parseInt(arr[0]);
                int data;
                data=Integer.parseInt(arr[1]);
                String binAdd;
                binAdd=Integer.toBinaryString(add);
                int b;
                b=binAdd.length();
                for(int x=0; x<16-b; x++){
                    binAdd="0"+binAdd; //repeat
                }
                String tag;
                tag=binAdd.substring(0,16-offset-SetnBinLen);
                String offsetStr;
                offsetStr=binAdd.substring(16-offset,16);
                int blockAdd;
                blockAdd=Integer.parseInt(binAdd.substring(0,16-offset),2);
                try{
                    int a;
                    a=mainmem[add/block_Size][Integer.parseInt(offsetStr,2)];
                }
                catch(Exception e){
                    System.out.println("Invalid adress at line number:"+(i+1));
                }
                int setNo;
                setNo=blockAdd%sets;
                String setNoBin;
                setNoBin=Integer.toBinaryString(setNo); 
                if(setNoBin.length()<setNo){
                    int temp;
                    temp=setNo-setNoBin.length();
                    for(int y=0; y<temp; y++) {
                        setNoBin="0"+setNoBin;
                    }
                }
                int lineNo=-1;
                for(i=0;i<set_Size;i++){
                    int cacheLine;
                    cacheLine=set_Add_List[setNo][i];
                    if(tagArr.get(cacheLine).equals(tag)){
                        lineNo=cacheLine;
                        break;
                    }
                }
                if(lineNo==-1){ //tag not found
                    for(i=0; i<set_Size; i++){
                        int cacheLine;
                        cacheLine=set_Add_List[setNo][i];
                        if(tagArr.get(cacheLine).equals("")){
                            lineNo=cacheLine;
                            break;
                        }
                    }
                    if(lineNo==-1){ //cache is full and block not there
                        int index;
                        index=priorityM.get(setNo).get(0);
                        tagArr.put(index,tag);
                        cache[index]=mainmem[blockAdd];
                        cache[index][Integer.parseInt(offsetStr, 2)]=data;
                        priorityM.get(setNo).remove(0);
                        priorityM.get(setNo).add(index);
                        System.out.println("Cache Miss, Block replaced");
                    }
                    else{
                        tagArr.put(lineNo,tag);
                        cache[lineNo]=mainmem[blockAdd];
                        cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
                        for(i=0; i<priorityM.get(setNo).size(); i++){
                            if(priorityM.get(setNo).get(i)==lineNo){
                                priorityM.get(setNo).remove(i);
                                break;
                            }
                        }
                        priorityM.get(setNo).add(lineNo);
                        System.out.println("Cache Miss, Block added from main memory");
                    }
                }
                else{
                    cache[lineNo][Integer.parseInt(offsetStr, 2)]=data;
                    for(i =0; i<priorityM.get(setNo).size(); i++){
                        if(priorityM.get(setNo).get(i)==lineNo){
                            priorityM.get(setNo).remove(i);
                            break;
                        }
                    }
                    priorityM.get(setNo).add(lineNo);
                    System.out.println("Cache Hit");
                }
            }
            else if(arr.length==1){ 
                // read
                int add;
                add=Integer.parseInt(arr[0]);
                String binAdd;
                binAdd=Integer.toBinaryString(add);
                int b;
                b=binAdd.length();
                for(int z=0; z<16-b; z++){
                    binAdd="0"+binAdd; //repeat func
                }
                String tag;
                tag=binAdd.substring(0,16-offset-SetnBinLen);
                String offsetStr;
                offsetStr=binAdd.substring(16-offset,16);
                int blockAdd;
                blockAdd=Integer.parseInt(binAdd.substring(0,16-offset),2);
                try{
                    int a;
                    a=mainmem[add/block_Size][Integer.parseInt(offsetStr,2)];
                }
                catch(Exception e){
                    System.out.println("Invalid adress at line number: "+(i+1));
                }
                int setNo;
                setNo=blockAdd%sets;
                String SetNoBin;
                SetNoBin=Integer.toBinaryString(setNo);
                if(SetNoBin.length()<SetnBinLen){
                    int temp;
                    temp=SetnBinLen-SetNoBin.length();
                    for(int w=0; w<temp; w++){
                        SetNoBin="0"+SetNoBin; //repeat func
                    }
                }
                int lineNo=-1;
                for(i=0;i<set_Size;i++){
                    int cacheLine;
                    cacheLine=set_Add_List[setNo][i];
                    if(tagArr.get(cacheLine).equals(tag)){
                        lineNo=cacheLine;
                        break;
                    }
                }
                if(lineNo==-1){
                    // requested block not in cache
                    for(i=0;i<set_Size;i++){
                        int cacheLine;
                        cacheLine=set_Add_List[setNo][i];
                        if(tagArr.get(cacheLine).equals(" ")){
                            lineNo=cacheLine;
                            break;
                        }
                    }
                    if(lineNo==-1){
                        int index;
                        index=priorityM.get(setNo).get(0);
                        tagArr.put(index,tag);
                        cache[index]=mainmem[blockAdd];
                        priorityM.get(setNo).remove(0);
                        priorityM.get(setNo).add(index);
                        int data;
                        data=cache[index][Integer.parseInt(offsetStr, 2)];
                        if(data!=-1){
                            System.out.println("Data "+data+"- Cache Miss, Block replaced");
                        }
                        else{
                            System.out.println("Cache Miss, No data available at the given memory address");
                        }
                    }
                    else{
                        tagArr.put(lineNo,tag);
                        cache[lineNo]=mainmem[blockAdd];
                        int data;
                        data=cache[lineNo][Integer.parseInt(offsetStr, 2)];
                        if(data!=-1){
                            System.out.println("Data "+data+"- Cache Miss, Block added from main memory");
                        }
                        else{
                            System.out.println("Cache Miss, No data available at the given memory address");
                        }
                        for(i=0;i<priorityM.get(setNo).size();i++){
                            if(priorityM.get(setNo).get(i)==lineNo){
                                priorityM.get(setNo).remove(i);
                                break;
                            }
                        }
                        priorityM.get(setNo).add(lineNo);
                    }
                }
                else{
                    int data;
                    data=cache[lineNo][Integer.parseInt(offsetStr, 2)];
                    if(data==-1){
                        System.out.println("Cache Miss, No data available at the given memory address");
                    }
                    else{
                        System.out.println("Data "+data+"- Cache hit");
                    }
                    for(i=0;i<priorityM.get(setNo).size();i++){
                        if(priorityM.get(setNo).get(i)==lineNo){
                            priorityM.get(setNo).remove(i);
                            break;
                        }
                    }
                    priorityM.get(setNo).add(lineNo);
                }
            }
            else{
                // invalid input
                System.out.println("Invalid input at line number:"+(i+1));
            }
        }
    }
    static String[] takeInput(){
        Scanner s;
        s= new Scanner(System.in);  
        System.out.println("Enter the number of inputs you want to give:");
        int n;
        n = s.nextInt();
        String[] req;
        req=new String[n];
        int i;
        i=0;
        while(i<n){
            String a;
            a =s.nextLine().trim();
            if(!a.trim().isEmpty()){
                req[i]=a;
                i++;
            }
        }
        return req;
    }
    public static boolean powerOf2(int k){
        int rem;
        rem=0;
        while(k>1){
            rem=k%2;
            k=k/2;
            if(rem==1) //not divisible by 2
                return false;
        }
        return true;
    }
}
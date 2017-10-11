import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.Comparator;
public class DGraph {
	//邻接表链表节点
	private class ENode{
		int num;//点编号
		int cost;//边长
		int visit;//判断是否访问
		//boolean known;
		ENode nextEdge;
	}

	//邻接表顶点
	private class VNode{
		String word;
		ENode firstEdge;//第一条邻接边
	}

	//最短路各节点距离
	private class Node{
		int id;
		int cost = 100000;
	}

	//private static String[] context; //?
	private static ArrayList<String> context = new ArrayList<String>();
	private int vlen;
	private int elen;
	private ArrayList<VNode> Vlist = new ArrayList<VNode>();

	//按字符读取文件
	public  static int readFileByChars(String filePath) {
        int i = 0;
		String result = new String();
        File file = new File(filePath);
        Reader reader = null;
        try{
            //一次读一个字符 空格的ascii码 32 大写字母65到90 小写字母97到122
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;

            while((tempchar = reader.read()) != -1){
                if (tempchar == 10)
                    result = result+" ";

                else if (tempchar == 32 || (tempchar >= 97 && tempchar <= 122)){
                    result = result+(char)tempchar;
                }

                else if((tempchar >= 65 && tempchar <= 90)){
                    result = result+(char)(tempchar+32);
                }
            }
            reader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        String reg1 = "\\s+";//s表示空格、回车、换行等空白符
        String reg2 = "\\w+";//w表示单词字符(数字字母下划线)
		String str[] = result.split(reg1);//将读取的字符串按空格划分成单词
		for(String s:str) {
			if(s.matches(reg2)) {//剔除除英文字母以外的字符
				context.add(s);
				i++;
			}
		}
		return i;
    }

	//DGraph类自建函数
	public DGraph(String filePath) throws Exception {
		int i = 0;
		vlen = 1;
		VNode node;
		elen = DGraph.readFileByChars(filePath) - 1;//所有有向边的边数
		node = new VNode();
		node.word = context.get(0);
		node.firstEdge = null;
		Vlist.add(node);
		for(String s:context) {
			int flag=0;
			for(VNode p:Vlist) {//判断点是否已存在于点表中
				if(p.word.equals(s)) {
					flag = 1;
					break;
				}
			}
			if(flag == 0) {
				node = new VNode();
				node.word = context.get(i);
				node.firstEdge = null;
				Vlist.add(node);
				vlen++;
			}
			i++;
		}

		for(i = 0;i<elen;i++) {
			String s1 = context.get(i);
			String s2 = context.get(i+1);
			for(int j = 0;j<vlen;j++) {
				if(s1.matches(Vlist.get(j).word)) {
					if(Vlist.get(j).firstEdge==null) {
						int p;
						for(p = 0;p<vlen;p++) {
							if(s2.matches(Vlist.get(p).word)) {
								break;
							}
						}
						ENode newNode = new ENode();
						newNode.cost = 1;
						newNode.num = p;
						newNode.visit = 0;
						newNode.nextEdge = null;
						Vlist.get(j).firstEdge = newNode;
						break;
					}
					else {
						ENode nextNode = Vlist.get(j).firstEdge;
						while(nextNode!=null) {
							if(s2.matches(Vlist.get(nextNode.num).word)) {
								nextNode.cost++;
								break;
							}
							else {
								nextNode = nextNode.nextEdge;
							}
						}
						if(nextNode==null) {
							int p;
							for(p = 0;p<vlen;p++) {
								if(s2.matches(Vlist.get(p).word)) {
									break;
								}
							}
							ENode newNode = new ENode();
							newNode.num = p;
							newNode.cost = 1;
							newNode.visit = 0;
							newNode.nextEdge = Vlist.get(j).firstEdge;
							Vlist.get(j).firstEdge = newNode;
							break;
						}
					}
				}
			}
		}
	}

	//获取桥接词
	public String queryBridgeWords(String word1, String word2) {
		int p1,p2;//两个单词在点表中对应的位置编号
		int flag1 = 0,flag2=0;
		int bridgeNum = 0;
		String result = new String();
		ArrayList<String> bridgeWords = new ArrayList<String>();
		for(p1=0;p1<vlen;p1++) {
			if(word1.matches(Vlist.get(p1).word)) {
				flag1=1;
				break;
			}
		}
		for(p2=0;p2<vlen;p2++) {
			if(word2.matches(Vlist.get(p2).word)) {
				flag2=1;
				break;
			}
		}
		if(flag1==0 || flag2==0) {//判断两个单词是否都存在于有向图上
			//System.out.println("No "+word1+" or "+word2+" in the graph!");
			result = result + "No " + word1 + " or " + word2 + " in the graph!";
			return result;
		}

		for(ENode bridge=Vlist.get(p1).firstEdge;bridge!=null;bridge=bridge.nextEdge) {
			for(ENode node=Vlist.get(bridge.num).firstEdge;node!=null;node=node.nextEdge) {
				if(p2 == node.num) {
					bridgeWords.add(Vlist.get(bridge.num).word);
					bridgeNum++;
				}
			}
		}
		//判断桥接词个数
		if(bridgeNum == 0) {
			//System.out.println("No bridge word from "+word1+" to "+word2+"!");
			result = result + "No bridge word from " + word1 + " to " + word2+"!";
		}
		else if(bridgeNum == 1){
			//System.out.println("The bridge word from "+word1+" to "+word2+" is:"+bridgeWords.get(0)+".");
			result = result + "The bridge word from " + word1 + " to " + word2 + " is:" + bridgeWords.get(0) + ".";
		}
		else if(bridgeNum > 1){
			//System.out.print("The bridge words from "+word1+" to "+word2+" are:"+bridgeWords.get(0)+",");
			result = result + "The bridge words from " + word1 + " to " + word2 + " are:" + bridgeWords.get(0) + ",";
			for(int i=1;i<bridgeNum-1;i++) {
				//System.out.print(bridgeWords.get(i)+",");
				result = result + bridgeWords.get(i) + ",";
			}
			//System.out.print("and "+bridgeWords.get(bridgeNum-1)+".");
			result = result + "and " + bridgeWords.get(bridgeNum-1) + ".";
		}
		return result;
	}

	//通过桥接词生成新文本
	public String generateNewText(String inputText) {
		String reg1 = "\\s+";//s表示空格、回车、换行等空白符
		String str[] = inputText.split(reg1);
		//ArrayList<String> list = new ArrayList<String>();
		String newText = new String();
		int wordsNum = 0;
		for(String s:str) {
			wordsNum++;
		}
		for(int i=0;i<wordsNum-1;i++) {
			//求词桥
			int p1,p2;
			int flag1=0,flag2=0;
			String s1 = str[i];
			String s2 = str[i+1];
			int bridgeNum = 0;
			ArrayList<String> bridgeWords = new ArrayList<String>();
			for(p1=0;p1<vlen;p1++) {
				if(Vlist.get(p1).word.equals(s1.toLowerCase())) {
					flag1=1;
					break;
				}
			}
			for(p2=0;p2<vlen;p2++) {
				if(Vlist.get(p2).word.equals(s2.toLowerCase())) {
					flag2=1;
					break;
				}
			}
			if(flag1==1 && flag2==1) {
				for(ENode bridge=Vlist.get(p1).firstEdge;bridge!=null;bridge=bridge.nextEdge) {
					for(ENode node=Vlist.get(bridge.num).firstEdge;node!=null;node=node.nextEdge) {
						if(p2 == node.num) {
							bridgeWords.add(Vlist.get(bridge.num).word);
							bridgeNum++;
						}
					}
				}
			}
			//将词桥加入
			if(bridgeNum == 0) {
				newText = newText + str[i] + " ";
			}
			else {
				double p = Math.random()*(bridgeNum-1);
				int index = (new Double(p)).intValue();
				newText = newText + str[i] + " ";
				newText = newText + bridgeWords.get(index) + " ";
			}
		}
		newText = newText + str[wordsNum-1];
		return newText;
	}

	//输入两个单词获取最短路，展示所有长度最短的路径
	public String calcShortestPath(String word1,String word2){
		String result = new String();
		int p1,p2;
		for(p1=0;p1<vlen;p1++) {
			if(Vlist.get(p1).word.equals(word1.toLowerCase())) {
				break;
			}
		}
		for(p2=0;p2<vlen;p2++) {
			if(Vlist.get(p2).word.equals(word2.toLowerCase())) {
				break;
			}
		}

		if(p1>=vlen || p2>=vlen) {//判断两个单词是否都在有向图上
			result = "不可达";
			return result;
		}

		//利用dijsktra求最短路
		int[][] path = new int[vlen][vlen];//路径父节点
		int[] visit = new int[vlen];
		ArrayList<Node> dist = new ArrayList<Node>();
		Node distNode;
		for(int i=0;i<vlen;i++) {
			distNode = new Node();
			distNode.id = i;
			dist.add(distNode);
			for(int j=0;j<vlen;j++) {
				path[i][j] = -1;
			}
			visit[i] = 0;
		}

		Comparator<Node> orderIsdn = new Comparator<Node>() {

			@Override
			public int compare(Node node1, Node node2) {
				int num1 = node1.cost;
				int num2 = node2.cost;
				if(num1>num2) {
					return 1;
				}
				else if(num1<num2) {
					return -1;
				}
				else {
					return 0;
				}
			}

		};
		Queue<Node> priorityQueue = new PriorityQueue<Node>(orderIsdn);

		dist.get(p1).cost = 0;
		priorityQueue.add(dist.get(p1));
		while(!priorityQueue.isEmpty()) {
			Node cd = priorityQueue.poll();
			int cp = cd.id;
			//System.out.println("CP="+cp);
			if(visit[cp] == 1) {
				continue;
			}
			visit[cp] = 1;
			//System.out.println("---------");
			ENode edge = Vlist.get(cp).firstEdge;
			while(edge!=null) {
				int tempv = edge.num;
				int tempc = edge.cost;
				if(visit[tempv]==0 && dist.get(tempv).cost==dist.get(cp).cost+tempc) {
					dist.get(tempv).cost = dist.get(cp).cost+tempc;
					for(int i=0;i<vlen;i++) {
						if(path[tempv][i] == -1) {
							path[tempv][i] = cp;
							//System.out.println(tempv+" "+i + " " + cp);
							break;
						}
					}
					//priorityQueue.add(dist.get(tempv));
				}
				if(visit[tempv]==0 && dist.get(tempv).cost>dist.get(cp).cost+tempc) {
					dist.get(tempv).cost = dist.get(cp).cost+tempc;
					path[tempv][0] = cp;
					for(int i=1;i<vlen;i++) {
						if(path[tempv][i] == -1) {
							break;
						}
						path[tempv][i] = -1;
					}
					priorityQueue.add(dist.get(tempv));
				}
				edge = edge.nextEdge;
			}
		}
		//终点没有父节点，表明两点在有向图上不通
		if(path[p2][0]==-1) {
			result = "不可达";
			return result;
		}

		StringBuilder tempstring = new StringBuilder("");
		getSinglePath(p2,tempstring,"",path,p2);
		result = tempstring.toString().trim();
		return result;
	}

	//递归函数，利用父节点数组求两点间所有最短路径
	public void getSinglePath(int index,StringBuilder s,String locS,int path[][],int end) {
		for(int i=0;i<vlen;i++) {
			if(path[index][0]==-1) {
				s.insert(0, "\n" + Vlist.get(index).word + "->" + locS);
				return;
			}
			if(path[index][i]==-1) {
				break;
			}
			if(index == end) {
				getSinglePath(path[index][i],s,Vlist.get(index).word + locS,path,end);
			}
			else
			{
				getSinglePath(path[index][i],s,Vlist.get(index).word + "->" + locS,path,end);
			}
		}
	}

	//输入一个单词,求一个点到其余能到达点的最短路
	public String calcShortestPath(String word) {
		String result = new String();
		int p;
		for(p=0;p<vlen;p++) {
			if(Vlist.get(p).word.equals(word.toLowerCase())) {
				break;
			}
		}

		if(p>=vlen) {//该点在有向图中不存在
			result = "不可达";
			return result;
		}

		int[] path = new int[vlen];//父节点
		int[] visit = new int[vlen];
		ArrayList<Node> dist = new ArrayList<Node>();
		Node distNode;
		for(int i=0;i<vlen;i++) {
			distNode = new Node();
			distNode.id = i;
			dist.add(distNode);
			path[i] = -1;
			visit[i] = 0;
		}

		Comparator<Node> orderIsdn = new Comparator<Node>() {

			@Override
			public int compare(Node node1, Node node2) {//优先队列对比函数，比较边长
				int num1 = node1.cost;
				int num2 = node2.cost;
				if(num1>num2) {
					return 1;
				}
				else if(num1<num2) {
					return -1;
				}
				else {
					return 0;
				}
			}

		};
		Queue<Node> priorityQueue = new PriorityQueue<Node>(orderIsdn);
		//利用优先队列实现dijsktra算法
		dist.get(p).cost = 0;
		priorityQueue.add(dist.get(p));
		while(!priorityQueue.isEmpty()) {
			Node cd = priorityQueue.poll();
			int cp = cd.id;
			if(visit[cp] == 1) {
				continue;
			}
			visit[cp] = 1;
			ENode edge = Vlist.get(cp).firstEdge;
			while(edge!=null) {
				int tempv = edge.num;
				int tempc = edge.cost;
				if(visit[tempv]==0 && dist.get(tempv).cost>dist.get(cp).cost+tempc) {
					dist.get(tempv).cost = dist.get(cp).cost+tempc;
					path[tempv] = cp;
					priorityQueue.add(dist.get(tempv));
				}
				edge = edge.nextEdge;
			}
		}
		//将最短路路径打印返回
		for(int i=0;i<vlen;i++) {
			if(i==p) {
				continue;
			}
			int index = i;
			Stack<String> stack = new Stack<String>();
			while(path[index]!=-1) {
				stack.push(Vlist.get(index).word);
				index = path[index];
			}
			result = result + word + "->";
			while(!stack.isEmpty()&&!stack.peek().equals(Vlist.get(i).word)) {
				result = result + stack.pop() + "->";
			}
			result = result + Vlist.get(i).word + "\n";
		}

		return result;
	}

	//生成随机路径
	public String randomWalk() {

		ENode edge;
		ENode foreEdge;
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		int Path[] = new int[1000];

		double p;
		int startIndex;
		int index = 1;
		String s = new String();
		p = Math.random()*vlen;
		startIndex = (new Double(p)).intValue();
		//System.out.print(Vlist.get(startIndex).word+" ");
		s = s + Vlist.get(startIndex).word + " ";
		Path[0] = startIndex;
		edge = Vlist.get(startIndex).firstEdge;
		foreEdge = Vlist.get(startIndex).firstEdge;
		//判断条件出现第一条重复的边或进入的某个节点不存在出边
		while(edge!=null && edge.visit == 0) {
			while(edge.nextEdge!=null) {
				double randomNum = Math.random();
				//System.out.println(randomNum);
				if(randomNum>=0.5) {
					edge = edge.nextEdge;
				}
				else {
					break;
				}
			}
			Path[index]=edge.num;
			index++;
			s = s + Vlist.get(edge.num).word + " ";
			edge.visit = 1;
			edge = Vlist.get(edge.num).firstEdge;
		}
		if(edge!=null) {//以重复边结束随机路径
			//System.out.print(Vlist.get(edge.num).word);
			s = s + Vlist.get(edge.num).word;
		}
		//初始化visit为0
		for(int i=0;i<vlen;i++) {
			edge = Vlist.get(i).firstEdge;
			while(edge!=null) {
				edge.visit = 0;
				edge = edge.nextEdge;
			}
		}

		for(int i=0;i<index;i++) {
			System.out.println(Path[i]);
		}

	    //起始节点填充颜色标明
		gv.addln(Vlist.get(Path[0]).word+"[style=\"filled\",fillcolor=\"chartreuse\"];");
		for(int i = 0;i<vlen;i++) {
	    	  String p1,p2;
	    	  p1 = Vlist.get(i).word;
	    	  edge = Vlist.get(i).firstEdge;
	    	  while(edge!=null) {
	    		  int cost = edge.cost;
	    		  p2 = Vlist.get(edge.num).word;
	    		  int flag = 1;
	    		  for(int j=0;j<index-1;j++) {
	    			  if(Path[j]==i && Path[j+1]==edge.num) {
	    				  flag = 0;
	    				  break;
	    			  }
	    		  }
	    		  if(flag == 1) {//不是随机路径的边仍为黑色
	    			   gv.addln(p1+" -> "+p2+"[label="+cost+"]"+";");
	    		  }
	    		  else if(flag == 0) {//随机路径的边用绿色标明
	    			   gv.addln(p1+" -> "+p2+"[label="+cost+","+"color=\"yellowgreen\""+"]"+";");
	    		  }
	    		  edge = edge.nextEdge;
	    	  }
	      }

	    gv.addln(gv.end_graph());
	    System.out.println(gv.getDotSource());

	    File out = new File("c:/temp/randomPath." + "jpg");    // 在指定路径生成有向图jpg文件
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), "jpg" ), out );

		return s;
	}

	//展示生成的有向图
	public void showDirectedGraph(String type)
	   {
	      GraphViz gv = new GraphViz();
	      gv.addln(gv.start_graph());
	      for(int i = 0;i<vlen;i++) {
	    	  String p1,p2;
	    	  p1 = Vlist.get(i).word;
	    	  ENode edge = Vlist.get(i).firstEdge;
	    	  while(edge!=null) {
	    		  int cost = edge.cost;
	    		  p2 = Vlist.get(edge.num).word;
	    		  gv.addln(p1+" -> "+p2+"[label="+cost+"]"+";");//利用GraphViz类直接指向其命令语句
	    		  edge = edge.nextEdge;
	    	  }
	      }
	      gv.addln(gv.end_graph());
	      System.out.println(gv.getDotSource());

	      File out = new File("c:/temp/out." + type);    // 在指定路径生成有向图jpg文件
	      gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	   }
}


import java.awt.Event;
import java.awt.Image;
import java.awt.PageAttributes.OriginType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.acl.Group;
import java.util.Scanner;

import javax.management.loading.PrivateClassLoader;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.SHELLEXECUTEINFO;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.dnd.SwtUtil;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class main extends ApplicationWindow {
	private Text text;
	private DGraph graph;

	/**
	 * Create the application window.
	 */
	public main() {
		super(null);
		setShellStyle(SWT.DIALOG_TRIM);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		//parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		//建立控制容器
		Composite container = new Composite(parent, SWT.NONE);
		//org.eclipse.swt.graphics.Image img = new org.eclipse.swt.graphics.Image(Display.getCurrent(), "C:\\Users\\lumi\\Desktop\\新建文件夹 (2)\\timg.jpg");
		container.setBackgroundImage(SWTResourceManager.getImage("timg.jpg"));

		container.setLayout(null);
		container.setBackgroundMode(SWT.INHERIT_FORCE);//设置容器背景透明
//		建立文本输入框， 用于输入路径
		text = new Text(container, SWT.BORDER);
		text.setBounds(35, 115, 192, 25);
//		文件检索按钮
		Button Search = new Button(container, SWT.NONE);
		Search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					text.setText("");
					FileDialog filedlg=new FileDialog(new Shell(), SWT.OPEN);
					//设置文件对话框的标题
					filedlg.setText("文件选择");
					//设置初始路径
					filedlg.setFilterPath("SystemRoot");
					//打开文件对话框，返回选中文件的绝对路径
					String selected=filedlg.open();
					text.insert(selected);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		Search.setText("\u6D4F\u89C8");
		Search.setBounds(227, 114, 60, 27);
//设置确定按钮
		Button readfile = new Button(container, SWT.NONE);
		readfile.setBounds(287, 114, 60, 27);
		readfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = text.getText();
				System.out.println(path);
				try {
					//根据读取路径建立有向图

					graph = new DGraph(path);
					graph.showDirectedGraph("jpg");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					MessageBox dialog=new MessageBox(new Shell(),SWT.OK|SWT.ICON_INFORMATION);
			        dialog.setText("error");
			        dialog.setMessage("请输入正确路径");
			        dialog.open();
				}
			}
		});
		readfile.setText("\u786E\u5B9A");

//建立透明按钮，实现展示有向图
		Label labShowGraph = new Label(container, SWT.NONE);
		//设置透明按钮
		labShowGraph.setImage(SWTResourceManager.getImage("\u5C55\u793A\u6709\u5411\u56FE.png"));
		labShowGraph.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				//展示有向图
						ShowGraphShell();
			}
		});
		labShowGraph.setBounds(35, 146, 313, 62);

		//建立透明按钮，查询桥接词
		Label labBrid = new Label(container, SWT.NONE);
		labBrid.setImage(SWTResourceManager.getImage("\u67E5\u8BE2\u6865\u63A5\u8BCD.png"));
		labBrid.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				//查询桥接词
				BWShell();
			}
		});
		labBrid.setBounds(35, 205, 313, 66);

		//建立透明按钮，生成桥接词文章
		Label labBridgeArtcle = new Label(container, SWT.NONE);
		labBridgeArtcle.setImage(SWTResourceManager.getImage("\u751F\u6210\u6865\u63A5\u8BCD\u6587\u7AE0.png"));
		labBridgeArtcle.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				//生成桥接词文章
				BWArtcle();
			}
		});
		labBridgeArtcle.setBounds(36, 265, 313, 66);

		//建立透明按钮，实现随机游走
		Label labRandomWalk = new Label(container, SWT.NONE);
		labRandomWalk.setImage(SWTResourceManager.getImage("\u968F\u673A\u6E38\u8D70.png"));
		labRandomWalk.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				//随机游走
				RandomWalkShell();
			}
		});
		labRandomWalk.setBounds(35, 388, 313, 62);


		//建立透明按钮，实现最短路查询
		Label labShortestPath = new Label(container, SWT.NONE);
		labShortestPath.setImage(SWTResourceManager.getImage("\u67E5\u8BE2\u6700\u77ED\u8DEF.png"));
		labShortestPath.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				ShortPathShell();
			}
		});
		labShortestPath.setBounds(35, 327, 313, 62);


		return container;//返回container
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}


	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			main window = new main();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		super.configureShell(newShell);
		newShell.setText("New Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(387, 527);
	}
	/*
	 * 建立新窗口，并展示有向图，并实现滚动条功能
	 */
	protected void ShowGraphShell()
	{
		Shell ShowGraph = new Shell(SWT.DIALOG_TRIM);
		ShowGraph.setText("展示有向图");
		ShowGraph.setLayout(new FillLayout());

//		读取图片
		try {
			org.eclipse.swt.graphics.Image img = new org.eclipse.swt.graphics.Image(Display.getCurrent(), "c:/temp/out.jpg");
//			滚动条功能
	        final Point origin = new Point(0, 0);
	        Canvas canvas = new Canvas(ShowGraph, SWT.DOUBLE_BUFFERED|SWT.V_SCROLL|SWT.H_SCROLL);
	        canvas.setSize(Math.min(1600, img.getBounds().width), Math.min(900, img.getBounds().height));
	        final ScrollBar hBar = canvas.getHorizontalBar ();
	        final Color red = new Color(null,255,0,0);
	        hBar.setMaximum(ShowGraph.getBounds().width);
	        hBar.addListener (SWT.Selection, new Listener () {
	        	public void handleEvent (org.eclipse.swt.widgets.Event e) {
	                 System.out.println("HBar listener");
	                     int hSelection = hBar.getSelection ();
	                     int destX = -hSelection - origin.x;
	                     canvas.scroll (destX, 0, 0, 0, ShowGraph.getBounds().width, ShowGraph.getBounds().height, true);
	                     origin.x = -hSelection;
	                     System.out.println("HBar listener exit");
	                 }

	             });
	        final ScrollBar vBar = canvas.getVerticalBar ();
	            vBar.setMaximum(ShowGraph.getBounds().width);
	            vBar.addListener (SWT.Selection, new Listener () {
	                public void handleEvent (org.eclipse.swt.widgets.Event e) {
	                    int vSelection = vBar.getSelection ();
	                    int destY = -vSelection - origin.y;
	                     canvas.scroll (0, destY, 0, 0, ShowGraph.getBounds().width, ShowGraph.getBounds().height, true);
	                     origin.y = -vSelection;
	                 }
	             });
	        canvas.addPaintListener(new PaintListener() {
	              @Override
	              public void paintControl(PaintEvent event) {
	                  // TODO Auto-generated method stub
	            	  event.gc.drawImage(img, origin.x, origin.y);
	              }
	        });
	        ShowGraph.setSize(Math.min(1600, img.getBounds().width), Math.min(900, img.getBounds().height));
	        ShowGraph.open();
		} catch (Exception e) {
			// TODO: handle exception
			MessageBox dialog=new MessageBox(ShowGraph,SWT.OK|SWT.ICON_INFORMATION);
	        dialog.setText("error");
	        dialog.setMessage("找不到有向图");
	        dialog.open();
		}

	}
	//查询桥接词
	protected void BWShell() {
		// TODO Auto-generated method stub
		Shell BridgeWord = new Shell(SWT.DIALOG_TRIM);
		// 背景图片生成
		BridgeWord.setBackgroundImage(SWTResourceManager.getImage("timg.jpg"));
		BridgeWord.setText("查询桥接词");
		BridgeWord.setSize(400, 500);
		BridgeWord.open();
		// 输入第一个单词
		Text word1 = new Text(BridgeWord, SWT.BORDER);
		word1.setBounds(5, 10, 73, 26);
		word1.pack();
		// 输入第二个单词
		Text word2 = new Text(BridgeWord, SWT.BORDER);
		word2.setBounds(5, 40, 73, 26);
		word2.pack();
		// 桥接词
		Text word3 = new Text(BridgeWord, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL|SWT.READ_ONLY);
		word3.setBounds(5, 200, 300, 260);
		Button btnBW = new Button(BridgeWord, SWT.NONE);
		btnBW.setBounds(5, 141, 98, 30);
		btnBW.setText("ok");
		btnBW.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String result;
				// 查询桥接算法， result为桥接词
				result = graph.queryBridgeWords(word1.getText(), word2.getText());
				word3.setText(result);
				} catch (Exception e2) {
					// TODO: handle exception
					MessageBox dialog=new MessageBox(BridgeWord,SWT.OK|SWT.ICON_INFORMATION);
			        dialog.setText("error");
			        dialog.setMessage("请正确输入");
			        dialog.open();
				}
			}
		});
	}
	//生成桥接词文章
	protected void BWArtcle() {
		// TODO Auto-generated method stub
		Shell BridgeArtcle = new Shell(SWT.DIALOG_TRIM);
		BridgeArtcle.setBackgroundImage(SWTResourceManager.getImage("timg.jpg"));
		BridgeArtcle.setText("生成桥接词文章");
		BridgeArtcle.setSize(400, 500);
		BridgeArtcle.open();
		//待生成桥接词文章
		Text word1 = new Text(BridgeArtcle, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		word1.setBounds(5, 10, 300, 260);
		Button btnBA = new Button(BridgeArtcle, SWT.NONE);
		btnBA.setBounds(5, 400, 98, 30);
		btnBA.setText("ok");
		btnBA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String result;
				//生成桥接词文章算法， result为生成结果
				result = graph.generateNewText(word1.getText());
				word1.setText(result);
				System.out.println(result);
			}
		});
	}
	//最短路查询
	protected void ShortPathShell() {
		// TODO Auto-generated method stub
		Shell ShortPath = new Shell(SWT.DIALOG_TRIM);
		ShortPath.setBackgroundImage(SWTResourceManager.getImage("timg.jpg"));
		ShortPath.setText("最短路查询");
		ShortPath.setSize(400, 500);
		ShortPath.open();
		//word1 输入查询起点
		Text word1 = new Text(ShortPath, SWT.BORDER);
		word1.setBounds(5, 10, 73, 26);
		word1.pack();
		// word2 输入查询重点
		Text word2 = new Text(ShortPath, SWT.BORDER);
		word2.setBounds(5, 40, 73, 26);
		word2.pack();
		// word3 输出所有最短路
		Text word3 = new Text(ShortPath, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL|SWT.READ_ONLY);
		word3.setBounds(5, 200, 300, 260);
		//word3.pack();
		Button btnBW = new Button(ShortPath, SWT.NONE);
		btnBW.setBounds(5, 141, 98, 30);
		btnBW.setText("ok");
		btnBW.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String result;
				//最短路算法， result为查询结果
				System.out.println("word1="+word1.getText()+"\nword2="+word2.getText());
				if(!word2.getText().equals(""))result = graph.calcShortestPath(word1.getText(), word2.getText());
				else result = graph.calcShortestPath(word1.getText());
				word3.setText(result);
			}
		});
	}
	//随机游走
	protected void RandomWalkShell() {
		// TODO Auto-generated method stub
		Shell RandomWalk = new Shell(SWT.DIALOG_TRIM);
		RandomWalk.setBackgroundImage(SWTResourceManager.getImage("timg.jpg"));
		RandomWalk.setText("随机游走");
		RandomWalk.setSize(400, 500);
		//RandomWalk.open();
		Text word1 = new Text(RandomWalk, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		word1.setBounds(5, 10, 300, 260);
		String result;
		try {
			//随机游走算法
			result = graph.randomWalk();
			word1.setText(result);
			RandomWalk.open();
			System.out.print(result);
			@SuppressWarnings("resource")
			PrintWriter out = new PrintWriter("RandomWalk.txt");
			out.println(result);
			out.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			//未生成有向图
			MessageBox dialog=new MessageBox(RandomWalk,SWT.OK|SWT.ICON_INFORMATION);
	        dialog.setText("error");
	        dialog.setMessage("未生成有向图");
	        dialog.open();
		}


	}
}

package cn.winstonkenny;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
/*
 扫雷主框架类，程序启动类
 */
public class StartJFrame extends JFrame {
    private ScanBoard scanBoard;
    private JPanel toolbar;
    private JButton startButton,exitButton;

    private JMenuBar menuBar;
    private JMenu sysMenu;
    private JMenuItem startMenuItem,exitMenuItem,backMenuItem;
    //重新开始，退出，和悔棋菜单项
    public StartJFrame(){
        setTitle("扫雷游戏-Designed By Winston.Kenny");//设置标题
        scanBoard=new ScanBoard();

        Container contentPane=getContentPane();
        contentPane.add(scanBoard);
        scanBoard.setOpaque(true);

        //创建和添加菜单
        menuBar =new JMenuBar();//初始化菜单栏
        sysMenu=new JMenu("控制");//初始化菜单
        //初始化菜单项
        startMenuItem=new JMenuItem("重新开始");
        exitMenuItem =new JMenuItem("退出");
        //将三个菜单项添加到菜单上
        sysMenu.add(startMenuItem);
        sysMenu.add(exitMenuItem);
        //初始化按钮事件监听器内部类
        MyItemListener lis=new MyItemListener();
        //将三个菜单注册到事件监听器上
        this.startMenuItem.addActionListener(lis);
        exitMenuItem.addActionListener(lis);
        menuBar.add(sysMenu);//将系统菜单添加到菜单栏上
        setJMenuBar(menuBar);//将menuBar设置为菜单栏

        toolbar=new JPanel();//工具面板实例化
        //按钮初始化
        startButton=new JButton("重新开始");
        exitButton=new JButton("退出");
        //将工具面板按钮用FlowLayout布局
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        //将按钮添加到工具面板
        toolbar.add(startButton);
        toolbar.add(exitButton);
        //将按钮注册监听事件
        startButton.addActionListener(lis);
        exitButton.addActionListener(lis);
        //将工具面板布局到界面”南方“也就是下方
        add(toolbar,BorderLayout.SOUTH);
        add(scanBoard);//将面板对象添加到窗体上
        //设置界面关闭事件
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(800,800);
        pack();//自适应大小
    }

    private class MyItemListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Object obj=e.getSource();//获得事件源
            if(obj==StartJFrame.this.startMenuItem||obj==startButton){
                //重新开始
                //JFiveFrame.this内部类引用外部类
                System.out.println("重新开始");
                scanBoard.restartGame();
            }
            else if (obj==exitMenuItem||obj==exitButton)
                System.exit(0);
        }
    }

    public static void main(String[] args){
        StartJFrame f=new StartJFrame();//创建主框架
        f.setVisible(true);//显示主框架
    }
}
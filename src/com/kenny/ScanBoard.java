package com.kenny;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
/**
 * 扫雷--雷区排布类
 */

public class ScanBoard extends JPanel implements MouseListener {
    public static final int MARGIN=30;//边距
    public static final int GRID_SPAN=25;//网格间距
    public static final int ROWS=25;//雷区行数
    public static final int COLS=25;//雷区列数
    public static final int BOMBNUM=80;//雷数

    Point[][] pointList=new Point[ROWS][COLS];//初始每个数组元素为null
    Integer[] bombList=new Integer[BOMBNUM];

    boolean gameOver=false;//游戏是否结束
    int bombCount;
    int flagCount=0;//当前标记雷区的个数
    int xIndex,yIndex;//当前点下的索引

    Image img_bomb;
    Image img_bomb2;
    Image img_flag;
    Image img_blank;
    Image img_black;
    public ScanBoard(){

        //setBackground(Color.orange);//设置背景色为橘黄色
        img_bomb=Toolkit.getDefaultToolkit().getImage("img/bomb.png");
        img_bomb2=Toolkit.getDefaultToolkit().getImage("img/bomb2.png");
        img_flag=Toolkit.getDefaultToolkit().getImage("img/flag.png");
        img_blank=Toolkit.getDefaultToolkit().getImage("img/blank.png");
        img_black=Toolkit.getDefaultToolkit().getImage("img/black.png");
        addMouseListener(this);
        addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e){

            }

            public void mouseMoved(MouseEvent e){
                int x1=(e.getX()-MARGIN+GRID_SPAN/2)/GRID_SPAN;
                //将鼠标点击的坐标位置转成网格索引
                int y1=(e.getY()-30-MARGIN+GRID_SPAN/2)/GRID_SPAN;
                //游戏已经结束不能选择
                //落在雷盘外不能
                if(x1<=0||x1>ROWS||y1<=0||y1>COLS||gameOver)
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    //设置成默认状态
                else setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        makeWorld();
        makeBomb();
    }

    //绘制
    public void paintComponent(Graphics g){
        super.paintComponent(g);//画雷盘
        Font font = new Font("Arial", Font.PLAIN, GRID_SPAN);
        Font font1 = new Font("Arial", Font.PLAIN, 20);

        //绘制雷区
        for(int xPos=0;xPos<COLS;xPos++){
            for(int yPos=0;yPos<ROWS;yPos++){
                int mode=pointList[yPos][xPos].getMode();
                int bombNum;
                //System.out.println(i+" "+xPos+" "+yPos);
                if(mode>=0&&mode<=5){
                    g.drawImage(img_black, MARGIN + GRID_SPAN * xPos, 30+MARGIN + GRID_SPAN * yPos, GRID_SPAN, GRID_SPAN, this);
                }
                if(mode>=5) {
                    g.drawImage(img_blank, MARGIN + GRID_SPAN * xPos, 30+MARGIN + GRID_SPAN * yPos, GRID_SPAN, GRID_SPAN, this);
                }
                if(mode==1) {
                    //g.drawImage(img_bomb, MARGIN + GRID_SPAN * xPos, 30+MARGIN + GRID_SPAN * yPos, GRID_SPAN, GRID_SPAN, this);
                }
                if(mode==9){
                    g.drawImage(img_bomb2, MARGIN + GRID_SPAN * xPos, 30+MARGIN + GRID_SPAN * yPos, GRID_SPAN, GRID_SPAN, this);
                }
                if(mode>=3&&mode<=5) {
                    g.drawImage(img_flag, MARGIN + GRID_SPAN * xPos, 30+MARGIN + GRID_SPAN * yPos, GRID_SPAN, GRID_SPAN, this);
                }
                bombNum=getBombNum(xPos,yPos);
                String strShow=String.valueOf(bombNum);
                g.setFont(font);
                FontMetrics fm = g.getFontMetrics(font);
                int height = fm.getHeight();
                int width = fm.stringWidth(strShow);
                if(mode==8&&bombNum>0)
                    g.drawString(strShow, MARGIN + GRID_SPAN * xPos+(GRID_SPAN-width)/2, 30+MARGIN + GRID_SPAN * yPos+(GRID_SPAN-height)/2+(int)(height*0.83));
                strShow="Bomb Num: "+ String.valueOf(BOMBNUM)+"   Flag Num: "+ String.valueOf(flagCount)+"      https://github.com/WinstonKenny/";
                g.setFont(font1);
                g.drawString(strShow, MARGIN, 30);
            }
        }
    }

    public void mousePressed(MouseEvent e){//鼠标在组件上按下时调用

        //游戏结束时，不再能下
        if(gameOver) return;

        //将鼠标点击的坐标位置转换成网格索引
        xIndex=(e.getX()-MARGIN+GRID_SPAN)/GRID_SPAN-1;
        yIndex=(e.getY()-30-MARGIN+GRID_SPAN)/GRID_SPAN-1;

        //落在棋盘外不能下
        if(xIndex<0||xIndex>=ROWS||yIndex<0||yIndex>=COLS)
            return;
        System.out.println(xIndex+" "+yIndex);

        //如果x，y位置已经有旗子存在，不能左键点击
        if(e.getButton()==1){
            System.out.println("left");
            if(findMode(xIndex,yIndex)==1){setMode(xIndex,yIndex,9);gameOver=true;}
            if(findMode(xIndex,yIndex)==0){
                setMode(xIndex,yIndex,8);
                if(getBombNum(xIndex,yIndex)==0)
                    recursiveShow(xIndex,yIndex);
            }
        }
        else if(e.getButton()==2){
            System.out.println("middle");
            System.out.println("mode:"+findMode(xIndex,yIndex));
            System.out.println("num:"+getBombNum(xIndex,yIndex));
        }
        else if(e.getButton()==3){
            System.out.println("right"+findMode(xIndex,yIndex));
            if(findMode(xIndex,yIndex)>=0&&findMode(xIndex,yIndex)<=2){//加旗
                if(flagCount<BOMBNUM) {
                    setMode(xIndex, yIndex, findMode(xIndex, yIndex) + 3);
                    flagCount++;
                }
                else{
                    String msg=String.format("旗子已经使用完毕！未能成功标记所有地雷！");
                    JOptionPane.showMessageDialog(this, msg);
                }
                if(getRightNum()==BOMBNUM){
                    repaint();//通知系统重新绘制
                    String msg=String.format("恭喜你成功完成所有地雷的标记！现在开始下一局！");
                    JOptionPane.showMessageDialog(this, msg);
                    restartGame();
                    return;
                }
            }
            else if(findMode(xIndex,yIndex)>=3&&findMode(xIndex,yIndex)<=5){//去旗
                setMode(xIndex,yIndex,findMode(xIndex,yIndex)-3);
                flagCount--;
            }
        }
        repaint();//通知系统重新绘制
        if(gameOver){
            String msg=String.format("你点了地雷！重新开始游戏");
            JOptionPane.showMessageDialog(this, msg);
            restartGame();
        }
    }
    //覆盖mouseListener的方法
    public void mouseClicked(MouseEvent e){
        //鼠标按键在组件上单击时调用
    }

    public void mouseEntered(MouseEvent e){
        //鼠标进入到组件上时调用
    }
    public void mouseExited(MouseEvent e){
        //鼠标离开组件时调用
    }
    public void mouseReleased(MouseEvent e){
        //鼠标按钮在组件上释放时调用
    }
    //在棋子数组中查找索引为x，y的棋子的mode
    private int findMode(int x,int y){
        try{
            return pointList[y][x].getMode();
        }catch(RuntimeException e){
        }finally{}
        return 0;
    }
    private void setMode(int x,int y,int mode){
        pointList[y][x].setMode(mode);
    }

    public void restartGame(){
        gameOver=false;
        flagCount=0;
        makeWorld();
        makeBomb();
        repaint();//通知系统重新绘制
    }

    //矩形Dimension
    public Dimension getPreferredSize(){
        return new Dimension(MARGIN*2+GRID_SPAN*COLS,30+MARGIN*2
                +GRID_SPAN*ROWS);
    }

    public void makeBomb(){
        int random_bomb;
        boolean if_redo=false;
        //生成地雷
        bombCount=0;
        while(bombCount<BOMBNUM) {
            if_redo=false;
            random_bomb = (int) (Math.random() * (COLS * ROWS));
            if (bombCount == 0){
                bombList[bombCount] = random_bomb;
                bombCount++;
            }
            else{
                for(int i=0;i<bombCount;i++) {
                    if(random_bomb==bombList[i]){
                        if_redo=true;
                        break;
                    }
                }
                if(if_redo)continue;
                bombList[bombCount] = random_bomb;
                bombCount++;
            }
        }
        System.out.println("bombCount"+bombCount);
        for(int i=0;i<BOMBNUM;i++){
            System.out.print(" "+bombList[i]);
            pointList[bombList[i]/ROWS][bombList[i]%COLS].setMode(1);
        }
        System.out.println("bombCount"+bombCount);
    }
    public void makeWorld(){
        //生成世界区
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++) {
                Point world=new Point(j,i, 0);
                pointList[i][j]=world;
            }
        }
    }
    public int getBombNum(int x, int y){
        int num=0;
        if(findMode(x-1,y-1)==1||findMode(x-1,y-1)==4||findMode(x-1,y-1)==9){num++;}
        if(findMode(x,y-1)==1||findMode(x,y-1)==4||findMode(x,y-1)==9){num++;}
        if(findMode(x+1,y-1)==1||findMode(x+1,y-1)==4||findMode(x+1,y-1)==9){num++;}
        if(findMode(x-1,y)==1||findMode(x-1,y)==4||findMode(x-1,y)==9){num++;}
        if(findMode(x+1,y)==1||findMode(x+1,y)==4||findMode(x+1,y)==9){num++;}
        if(findMode(x-1,y+1)==1||findMode(x-1,y+1)==4||findMode(x-1,y+1)==9){num++;}
        if(findMode(x,y+1)==1||findMode(x,y+1)==4||findMode(x,y+1)==9){num++;}
        if(findMode(x+1,y+1)==1||findMode(x+1,y+1)==4||findMode(x+1,y+1)==9){num++;}
        return num;
    }

    public int getRightNum() {
        int rightNum=0;
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++) {
                if(pointList[i][j].getMode()==4)rightNum++;
            }
        }
        return rightNum;
    }
    public void recursiveShow(int x, int y){//递归显示无雷区
        if(y>0&&x>0&&y<ROWS-1&&x<COLS-1) {
            if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y==0&&x> 0&&y< ROWS-1&&x< COLS-1){
            //if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            //if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            //if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y> 0&&x==0&&y< ROWS-1&&x< COLS-1){
            if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            //if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            //if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            //if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y> 0&&x> 0&&y==ROWS-1&&x< COLS-1){
            if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            //if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            //if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            //if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y> 0&&x> 0&&y< ROWS-1&&x==COLS-1){
            if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            //if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            //if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            //if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y==0&&x==0&&y< ROWS-1&&x< COLS-1){
            //if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            //if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            //if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            //if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            //if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y==0&&x> 0&&y< ROWS-1&&x==COLS-1){
            //if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            //if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            //if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            //if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            //if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y> 0&&x==0&&y==ROWS-1&&x< COLS-1){
            if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            //if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            //if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            //if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            //if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            //if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
        else if(y> 0&&x> 0&&y==ROWS-1&&x==COLS-1){
            if (findMode(x, y - 1) == 0){setMode(x, y - 1, 8);if (getBombNum(x, y - 1) == 0) recursiveShow(x, y - 1);}
            if (findMode(x-1, y-1) == 0){setMode(x-1, y-1, 8);if (getBombNum(x-1, y-1) == 0) recursiveShow(x-1, y-1);}
            if (findMode(x - 1, y) == 0){setMode(x - 1, y, 8);if (getBombNum(x - 1, y) == 0) recursiveShow(x - 1, y);}
            //if (findMode(x-1, y+1) == 0){setMode(x-1, y+1, 8);if (getBombNum(x-1, y+1) == 0) recursiveShow(x-1, y+1);}
            //if (findMode(x + 1, y) == 0){setMode(x + 1, y, 8);if (getBombNum(x + 1, y) == 0) recursiveShow(x + 1, y);}
            //if (findMode(x+1, y-1) == 0){setMode(x+1, y-1, 8);if (getBombNum(x+1, y-1) == 0) recursiveShow(x+1, y-1);}
            //if (findMode(x, y + 1) == 0){setMode(x, y + 1, 8);if (getBombNum(x, y + 1) == 0) recursiveShow(x, y + 1);}
            //if (findMode(x+1, y+1) == 0){setMode(x+1, y+1, 8);if (getBombNum(x+1, y+1) == 0) recursiveShow(x+1, y+1);}
        }
    }
}

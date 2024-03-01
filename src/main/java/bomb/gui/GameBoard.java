package bomb.gui;

import bomb.constants.Base;
import bomb.constants.NodeState;
import bomb.entity.NodeButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameBoard extends JFrame {
    private int rows;
    private int cols;
    private JPanel panel;
    // 空白节点数
    private int blankNum;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * 初始化地图
     */
    public void init() {
        // 向当前窗体添加游戏菜单
        this.setJMenuBar(this.buildMenuBar());
        // 向当前窗体添加游戏主面板
        panel = this.buildPanel(rows, cols);
        this.calculateBombs();
        this.getContentPane().add(panel);

        this.setIconImage(new ImageIcon("src/main/resources/bomb.png").getImage());
        this.setBounds(800, 400, Base.BUTTON_WIDTH * cols, Base.BUTTON_HEIGHT * rows);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * 清除当前地图
     */
    public void clear() {
        this.getContentPane().remove(panel);
    }

    /**
     * 构建地图
     *
     * @param rows 行数
     * @param cols 列数
     * @return 地图
     */
    public JPanel buildPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols));
        // 初始空白节点数
        blankNum = rows * cols;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 随机初始化节点
                NodeButton nodeButton = new NodeButton();
                nodeButton.setRowNo(i);
                nodeButton.setColNo(j);
                // 地雷节点
                if (nodeButton.isBomb()) {
                    blankNum--;
                }
                nodeButton.loadIcon();
                nodeButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // 左键-BUTTON1，右键-BUTTON3
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            // 状态为关闭时，左击可以进行打开操作
                            if (nodeButton.getState() == NodeState.CLOSED) {
                                // 设为开始状态
                                nodeButton.setState(NodeState.OPENED);
                                // 打开炸弹
                                if (nodeButton.isBomb()) {
                                    // 炸弹爆炸,游戏结束
                                    nodeButton.loadIcon("src/main/resources/explosion.png");
                                    // 打开所有节点
                                    openAllNode();
                                } else {
                                    // 打开的空白节点周围炸弹为0
                                    if(nodeButton.getSurroundingBombs() == 0) {
                                        // 打开该节点周围所有节点
                                        openSurroundingNodes(nodeButton);
                                    }
                                    // 空白节点减少
                                    blankNum--;
                                    nodeButton.loadIcon();
                                    // 空白节点全部打开，游戏结束
                                    if (blankNum == 0) {
                                        // 打开所有节点
                                        openAllNode();
                                    }
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            // 不为打开状态时，右击可以进行标记、取消标记操作
                            if (nodeButton.getState() == NodeState.CLOSED) {
                                // 标记
                                nodeButton.setState(NodeState.MARKED);
                                nodeButton.loadIcon();
                            } else if (nodeButton.getState() == NodeState.MARKED) {
                                // 取消标记
                                nodeButton.setState(NodeState.CLOSED);
                                nodeButton.loadIcon();
                            }
                        } else {
                            nodeButton.loadIcon();
                            System.out.println("无效操作");
                        }
                    }
                });
                panel.add(nodeButton);
            }
        }

        return panel;
    }

    public void openSurroundingNodes(NodeButton nodeButton) {
        ArrayList<NodeButton> blankNodes = new ArrayList<>();
        blankNodes.add(nodeButton);
        while (!blankNodes.isEmpty()) {
            NodeButton node = blankNodes.remove(0);
            this.findSurroundingNodes(node).forEach(e -> {
                if(e != null && e.getState() != NodeState.OPENED) {
                    e.setState(NodeState.OPENED);
                    e.loadIcon();
                    if(e.getSurroundingBombs() == 0) {
                        blankNodes.add(e);
                    }
                }
            });
        }
    }

    public List<NodeButton> findSurroundingNodes(NodeButton nodeButton) {
        List<NodeButton> surroundingNodes = new ArrayList<>();
        int x = nodeButton.getRowNo();
        int y = nodeButton.getColNo();
        if(checkRange(x - 1, y - 1)) {
            surroundingNodes.add(this.findNodeButton(x - 1, y - 1));
        }
        if(checkRange(x - 1, y)) {
            surroundingNodes.add(this.findNodeButton(x - 1, y));
        }
        if(checkRange(x - 1, y + 1)) {
            surroundingNodes.add(this.findNodeButton(x - 1, y + 1));
        }
        if(checkRange(x, y - 1)) {
            surroundingNodes.add(this.findNodeButton(x, y - 1));
        }
        if(checkRange(x, y + 1)) {
            surroundingNodes.add(this.findNodeButton(x, y + 1));
        }
        if(checkRange(x + 1, y - 1)) {
            surroundingNodes.add(this.findNodeButton(x + 1, y - 1));
        }
        if(checkRange(x + 1, y)) {
            surroundingNodes.add(this.findNodeButton(x + 1, y));
        }
        if(checkRange(x + 1, y + 1)) {
            surroundingNodes.add(this.findNodeButton(x + 1, y + 1));
        }
        return surroundingNodes;
    }

    /**
     * 计算空白节点周围的地雷数
     */
    public void calculateBombs() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                NodeButton nodeButton = this.findNodeButton(i, j);
                if (!nodeButton.isBomb()) {
                    nodeButton.setSurroundingBombs(this.countSurroundingBombs(i, j));
                }
            }
        }
    }

    /**
     * 构建菜单栏
     *
     * @return 菜单栏
     */
    public JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("菜单");
        JMenuItem menuItemStart = new JMenuItem("开始游戏");
        menuItemStart.addActionListener(e -> {
            this.clear();
            this.rows = 16;
            this.cols = 16;
            this.init();
        });
        JMenuItem menuItemExit = new JMenuItem("结束游戏");
        menuItemExit.addActionListener(e -> System.exit(0));
        menu.add(menuItemStart);
        menu.add(menuItemExit);
        menuBar.add(menu);

        return menuBar;
    }

    /**
     * 获取指定位置的节点
     *
     * @param x 行号
     * @param y 列号
     * @return 节点
     */
    public NodeButton findNodeButton(int x, int y) {
        if (panel == null) {
            System.out.println("No Panels Built");
            return null;
        }
        return (NodeButton) panel.getComponent(x * cols + y);
    }

    /**
     * 检查坐标是否越界
     *
     * @param x 行号
     * @param y 列号
     * @return 是否越界
     */
    public boolean checkRange(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    /**
     * 计算当前节点的周围地雷数
     *
     * @param x 行号
     * @param y 列号
     * @return 周围地雷数
     */
    public Integer countSurroundingBombs(Integer x, Integer y) {
        int cntOfBombs = 0;
        // 节点上一行
        if (this.checkRange(x - 1, y - 1) && findNodeButton(x - 1, y - 1).isBomb()) {
            cntOfBombs++;
        }
        if (this.checkRange(x - 1, y) && findNodeButton(x - 1, y).isBomb()) {
            cntOfBombs++;
        }
        if (this.checkRange(x - 1, y + 1) && findNodeButton(x - 1, y + 1).isBomb()) {
            cntOfBombs++;
        }
        // 节点所在行
        if (this.checkRange(x, y - 1) && findNodeButton(x, y - 1).isBomb()) {
            cntOfBombs++;
        }
        if (this.checkRange(x, y + 1) && findNodeButton(x, y + 1).isBomb()) {
            cntOfBombs++;
        }
        // 节点下一行
        if (this.checkRange(x + 1, y - 1) && findNodeButton(x + 1, y - 1).isBomb()) {
            cntOfBombs++;
        }
        if (this.checkRange(x + 1, y) && findNodeButton(x + 1, y).isBomb()) {
            cntOfBombs++;
        }
        if (this.checkRange(x + 1, y + 1) && findNodeButton(x + 1, y + 1).isBomb()) {
            cntOfBombs++;
        }
        return cntOfBombs;
    }

    /**
     * 翻开所有未点击节点
     */
    public void openAllNode() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                NodeButton nodeButton = this.findNodeButton(i, j);
                if (nodeButton.getState() == NodeState.CLOSED) {
                    // 若为关闭状态，则直接显示
                    nodeButton.setState(NodeState.OPENED);
                    nodeButton.loadIcon();
                } else if (nodeButton.getState() == NodeState.MARKED) {
                    // 若为标记状态且为炸弹，则显示为正确标记
                    nodeButton.setState(NodeState.OPENED);
                    if (nodeButton.isBomb()) {
                        nodeButton.loadIcon("src/main/resources/correct.png");
                    } else {
                        nodeButton.loadIcon("src/main/resources/wrong.png");
                    }
                }
            }
        }
    }
}
package bomb.entity;

import bomb.constants.Base;
import bomb.constants.NodeState;
import bomb.constants.NodeType;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NodeButton extends JButton {
    // 该节点周围地雷数
    private int surroundingBombs = 0;
    // 类型： 0-空白 1-地雷
    private int type = 0;
    // 状态： 0-未翻开 1-已翻开 2-已标记
    private int state = 0;
    // 行号
    private int rowNo;
    // 列号
    private int colNo;


    public NodeButton() {
        super();
        randomSetBomb();
    }

    /**
     * 按概率确定节点类型
     */
    public void randomSetBomb() {
        Random random = new Random();
        double randomValue = random.nextDouble();
        if (randomValue < Base.BOMB_PERCENT) {
            this.type = NodeType.BOMB;
        }
    }

    /**
     * 检查是否为地雷
     */
    public boolean isBomb() {
        return type == NodeType.BOMB;
    }

    /**
     * 根据状态和类型加载按钮图标
     */
    public void loadIcon() {
        if (state == NodeState.CLOSED) {
            this.setBackground(Color.DARK_GRAY);
            this.setIcon(new ImageIcon("src/main/resources/void.png"));
        } else if (state == NodeState.MARKED) {
            this.setBackground(Color.WHITE);
            this.setIcon(new ImageIcon("src/main/resources/marked.png"));
        } else {
            this.setBackground(Color.WHITE);
            if (type == NodeType.BOMB) {
                this.setIcon(new ImageIcon("src/main/resources/bomb.png"));
            } else {
                if (surroundingBombs == 0) {
                    this.setIcon(new ImageIcon("src/main/resources/void.png"));
                } else {
                    this.setIcon(new ImageIcon("src/main/resources/" + surroundingBombs + ".png"));
                }
            }
        }
    }

    /**
     * 加载按钮图标
     *
     * @param imgUrl 图片地址
     */
    public void loadIcon(String imgUrl) {
        this.setBackground(Color.WHITE);
        this.setIcon(new ImageIcon(imgUrl));
    }

    public int getSurroundingBombs() {
        return surroundingBombs;
    }

    public void setSurroundingBombs(int surroundingBombs) {
        this.surroundingBombs = surroundingBombs;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public int getColNo() {
        return colNo;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
    }
}

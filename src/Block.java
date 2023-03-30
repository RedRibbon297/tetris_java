import java.awt.*;


public class Block {
    //обеспечит работу с минимальной конструкцией игры - блоком

    private int x, y;

    public Block(int x, int y) {
        setX(x);
        setY(y);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void paint(Graphics g, int color) {
        g.setColor(new Color(color));
        g.drawRoundRect(x * GameTetris.BLOCK_SIZE + 1, y * GameTetris.BLOCK_SIZE + 1, GameTetris.BLOCK_SIZE - 2,
                GameTetris.BLOCK_SIZE - 2, GameTetris.ARC_RADIUS, GameTetris.ARC_RADIUS);//рисуем прямоугольник
    }
}

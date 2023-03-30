import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Figure {
    private ArrayList<Block> figure = new ArrayList<Block>();
    private int[][] shape = new int[4][4];
    private int type;
    private int size;
    private int color;
    private int x = 3; //стартовые координаты
    private int y = 0;


    public Figure() {
        Random random = new Random();
        type = random.nextInt(GameTetris.SHAPES.length);
        size = GameTetris.SHAPES[type][4][0];
        color = GameTetris.SHAPES[type][4][1];

        if (size == 4) y = -1;

        for (int i = 0; i < size; i++) {
            System.arraycopy(GameTetris.SHAPES[type][i], 0, shape[i], 0, GameTetris.SHAPES[type][i].length);
            createFromShape();
        }
    }

    private void createFromShape() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (shape[y][x] == 1) {
                    figure.add(new Block(x + this.x, y + this.y));
                }
            }
        }
    }

    public void drop() {
        while (!isTouchGround()) stepDown();
    }

    public void rotate(){
        rotateShape(GameTetris.RIGHT);
        if (!isWrongPosition()) {
            figure.clear();
            createFromShape();
        } else
            rotateShape(GameTetris.LEFT);
    }

   public void rotateShape(int dir) {

        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < size - 1 - i; j++) {
                if (dir == GameTetris.RIGHT) { //по часовой стрелке
                    int tmp;
                    tmp = shape[size - 1 - j][i];
                    shape[size - 1 - j][i] = shape[size - 1 - i][size - 1 - j];
                    shape[size - 1 - i][size - 1 - j] = shape[j][size - 1 - i];
                    shape[j][size - 1 - i] = shape[i][j];
                    shape[i][j] = tmp;
                } else {
                    int tmp;
                    tmp = shape[i][j];
                    shape[i][j] = shape[j][size - 1 - i];
                    shape[j][size - 1 - i] = shape[size - 1 - i][size - 1 - j];
                    shape[size - 1 - i][size - 1 - j] = shape[size - 1 - j][i];
                    shape[size - 1 - j][i] = tmp;
                }
            }
        }
    }

    public boolean isWrongPosition() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (shape[y][x] == 1) {
                    if (y + this.y < 0) return true;
                    if (x + this.x < 0 || x + this.x > GameTetris.FIELD_WIDTH-1) return true;
                    if (GameTetris.mine[y + this.y][x + this.x] > 0) return true;
                }
            }
        }
        return false;
    }

    public void move(int direction) {
        if (!isTouchWall(direction)) {
            int dx = direction - 38;
            for (Block block : figure) block.setX(block.getX() + dx);
            x += dx;
        }
    }

    public boolean isTouchWall(int dir) {
        for (Block block : figure) {
            if (dir == GameTetris.LEFT && (block.getX() == 0 || GameTetris.mine[block.getY()][block.getX() - 1] > 0))
                return true;
            if (dir == GameTetris.RIGHT && (block.getX() == GameTetris.FIELD_WIDTH - 1 || GameTetris.mine[block.getY()][block.getX() + 1] > 0))
                return true;
        }
        return false;
    }

    public boolean isTouchGround() {
        for (Block block : figure) if (GameTetris.mine[block.getY() + 1][block.getX()] > 0) return true;
        return false;
    }

    public void leaveOnTheGround() {
        for (Block block : figure) GameTetris.mine[block.getY()][block.getX()] = color;

    }

    public boolean isCrossGround() {
        for (Block block : figure) if (GameTetris.mine[block.getY()][block.getX()] > 0) return true;
        return false;
    }

    public void stepDown() {
        for (Block block : figure) block.setY(block.getY() + 1);
        y++;
    }

    public void paint(Graphics g) {
        for (Block block : figure) block.paint(g, color);
    }

}

import java.awt.*; //библиотека для внешних форм
import java.awt.event.*; //библиотека для обрботки событий(напр.нажим на клавиши)
import javax.swing.*; //для графических элементов
import java.util.*; //нужна для генератора случайных чисел

public class GameTetris extends JFrame {

    final String TITLE_OF_PROGRAM = "Tetris";
    static final int BLOCK_SIZE = 25; //размер одного блока (кадратика) в пикселях
    static final int ARC_RADIUS = 6;
    static final int FIELD_WIDTH = 10; //ширина поля (равна 10-ти блокам)
    static final int FIELD_HEIGHT = 20; //высота поля в блоках
    final int START_LOCATION = 180; //в пикселях; где начинается выпадение деталек

    final int FIELD_DX = 17; //нужны для начального экрана, выявлены экспериментальным путем
    final int FIELD_DY = 40;

    static final int LEFT = 37; //коды клавиш - стрелочка влево <-
    static final int UP = 38; //вверх
    static final int RIGHT = 39; //->
    static final int DOWN = 40; //вниз

    final int SHAW_DELAY = 290; //определяет задержку анимации

    final static int[][][] SHAPES = {
            {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {4, 0x00f0f0}}, //I
            {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {4, 0xf0f000}}, //O
            {{1, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x0000f0}}, //J
            {{0, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf0a000}}, //L
            {{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x00f000}}, //S
            {{1, 1, 1, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xa000f0}}, //T
            {{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf00000}}  //Z
    };
    //7 двумерных массивов
    //4,3....- внутренний размер в матрице

    final int[] SCORES = {100, 300, 700, 1500}; //когда заполнена 1 строка за цикл  - 100 очков и т.д.
    //больше 4-ёх заполнений быть не может

    int gameScores = 0; //переменная для хранения очков игрока

    public static int[][] mine = new int[FIELD_HEIGHT + 1][FIELD_WIDTH];

    JFrame frame; //объект основного окна
    Canvas canvasPanel = new Canvas(); //панель, по которой будем всё рисовать
    Figure figure = new Figure();

    boolean gameOver = false;//индикатор проигрыша игры

    final int[][] GAME_OVER_MSG = {
            {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0},
            {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0}
    };
    //для надписи: GAME OVER на экране


    public static void main(String[] args) {
        new GameTetris().go();
    }

    public void go() {
        frame = new JFrame(TITLE_OF_PROGRAM);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //позволяет закрывать окно нажимая на красную кнопку с крестиком

        frame.setSize(FIELD_WIDTH * BLOCK_SIZE + FIELD_DX, FIELD_HEIGHT * BLOCK_SIZE + FIELD_DY);

        frame.setLocation(START_LOCATION, START_LOCATION);

        frame.setResizable(false); //делаем окно неизменяемого размера

        canvasPanel.setBackground(Color.black); //цвет фона окна

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    if (e.getKeyCode() == DOWN) figure.drop();
                    if (e.getKeyCode() == UP) figure.rotate();
                    if (e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT) figure.move(e.getKeyCode());
                }
                canvasPanel.repaint();
            }
        });

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.setVisible(true);

        Arrays.fill(mine[FIELD_HEIGHT], 1); //инициализация дна

        //главный цикл игры
        while (!gameOver) {
            try {
                Thread.sleep(SHAW_DELAY); //определение задержки анимации перед прорисовкой
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            canvasPanel.repaint();//окно перерисовывается
            //проверяем кснулась ли фигура земли
            checkFilling();
            if (figure.isTouchGround()) {
                figure.leaveOnTheGround();
                checkFilling();
                figure = new Figure();
                gameOver = figure.isCrossGround(); //если возникшая фигура пересеклась с землёй -> game over
            } else {
                figure.stepDown();
            }
        }
    }

    public void checkFilling() { //метод проверит, заполнилась ли хоть одна дорожка
        int row = FIELD_HEIGHT - 1;
        int countFillRows = 0;
        while (row > 0) {
            int filled = 1;
            for (int col = 0; col < FIELD_WIDTH; col++)
                filled *= Integer.signum(mine[row][col]);
            if (filled > 0) {
                countFillRows++;//считает количество заполненных строк
                for (int i = row; i > 0; i--) System.arraycopy(mine[i - 1], 0, mine[i], 0, FIELD_WIDTH);
            } else
                row--;
        }
        if (countFillRows > 0) {
            gameScores += SCORES[countFillRows - 1];
            frame.setTitle(TITLE_OF_PROGRAM + " : " + gameScores);
        }
    }

    public class Canvas extends JPanel {
        //обеспечивает процедуру рисования

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < FIELD_WIDTH; x++) {
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (mine[y][x] > 0) {
                        g.setColor(new Color(mine[y][x]));
                        g.fill3DRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 1, BLOCK_SIZE - 1, true);
                    }
                }
            }
            if (gameOver) {
                g.setColor(Color.white);
                for (int y = 0; y < GAME_OVER_MSG.length; y++) {
                    for (int x = 0; x < GAME_OVER_MSG[y].length; x++) {
                        if (GAME_OVER_MSG[y][x] == 1) {
                            g.fill3DRect(x * 11 + 16, y * 11 + 160, 10, 10, true);
                        }
                    }
                }
            } else {
                figure.paint(g);
            }
        }
    }
}



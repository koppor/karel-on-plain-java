package karel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Karel the Robot for PROG1 (THU), plain Java 25 with Swing, no libraries.
 *
 * <p>Version 1.1.0 (2026-10-21). Semantic versioning: major = commands change incompatibly,
 * minor = new worlds or features, patch = fixes.
 *
 * <p>Usage in a compact source file next to the folder {@code karel}:
 *
 * <pre>
 * import static karel.Karel.*;
 *
 * void main() {
 *     openWorld(World.DEFUSE_ONE_BOMB);
 *     moveForward();
 * }
 * </pre>
 *
 * <p>Commands and conditions follow Karel by Fred Overflow (github.com/fredoverflow/karel). Worlds are
 * text maps: {@code .} empty, {@code *} beeper, {@code #} wall block, {@code > ^ < v} Karel
 * facing east/north/west/south, {@code E N W S} Karel on a beeper.
 *
 * <p>Developer mode: {@code -Dkarel.headless=true} runs without window and delay and prints the
 * result and the final map. {@code -Dkarel.snapshot=world.png} (with {@code -Dkarel.snapshotGoal=true})
 * saves the start world as an image and exits; {@code -Dkarel.snapshotEnd=true} saves the goal state instead.
 */
public final class Karel {

    public static final String VERSION = "1.1.0";
    public static final String RELEASE_DATE = "2026-10-21";

    /** Thrown when Karel cannot carry out a command. */
    public static final class KarelException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        KarelException(String message) {
            super(message);
        }
    }

    private static final String[] DIRECTIONS = {"east", "north", "west", "south"};
    private static final int[] DX = {1, 0, -1, 0};
    private static final int[] DY = {0, -1, 0, 1};

    private static final Map<String, Supplier<String[]>> WORLDS = new LinkedHashMap<>();

    static {
        // name, start map, goal map
        world("obtainArtifact", """
                ..........
                ..........
                ..........
                ..........
                ..........
                ....*.....
                ....#.....
                ....#.....
                ....^.....
                ..........
                """, """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ....#.....
                ....#.....
                ....E.....
                ..........
                """);
        world("defuseOneBomb", """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                >........*
                """, """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                >.........
                """);
        world("climbTheStairs", """
                ..........
                ..........
                ..........
                ..........
                .......#..
                ......##..
                .....###..
                ....####..
                ...#####..
                >.######..
                """, """
                ..........
                ..........
                ..........
                .......>..
                .......#..
                ......##..
                .....###..
                ....####..
                ...#####..
                ..######..
                """);
        world("fillTheHoles", """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                .>........
                ##.#.#.#.#
                """, """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                .........>
                ##*#*#*#*#
                """);
        world("mowTheLawn", """
                ..........
                ..........
                ..******..
                ..******..
                ..******..
                ..******..
                ..******..
                .>******..
                ..........
                ..........
                """, """
                ..........
                ..........
                .<........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                """);
        world("defuseTwoBombs", """
                *.........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                >........*
                """, """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ^.........
                """);
        world("practiceHomeRun", """
                *........*
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                E........*
                """, """
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                ..........
                >.........
                """);
        world("saveTheFlower", """
                ..........
                ..........
                .....#....
                .....#....
                ....###...
                ....###...
                ...#####..
                ...#####..
                ..#######.
                >*#######.
                """, """
                ..........
                .....*....
                .....#....
                .....#....
                ....###...
                ....###...
                ...#####..
                ...#####..
                ..#######.
                ..#######>
                """);
        world("stealOlympicFire", """
                ..........
                ..........
                ..........
                .......*..
                .......#..
                ......##..
                .....###..
                ....####..
                ...#####..
                >.######..
                """, """
                ..........
                ..........
                ..........
                ..........
                .......#..
                ......##..
                .....###..
                ....####..
                ...#####..
                ..######.>
                """);
        // random worlds: new variant on every run, fixed with -Dkarel.seed=...
        WORLDS.put("hangTheLampions", Karel::lampions);
        WORLDS.put("walkTheLabyrinth", Karel::labyrinth);
    }

    private static void world(String name, String start, String goal) {
        String[] maps = {start, goal};
        WORLDS.put(name, () -> maps);
    }

    private static final Random RANDOM = Long.getLong("karel.seed") == null
            ? new Random()
            : new Random(Long.getLong("karel.seed"));

    /** Ten lampions on the floor, ceiling hanging down to a random depth in each column. */
    private static String[] lampions() {
        char[][] start = grid(10, 10, '.');
        char[][] goal = grid(10, 10, '.');
        for (int col = 0; col < 10; col++) {
            int ceiling = 1 + RANDOM.nextInt(6);
            for (int row = 0; row < ceiling; row++) {
                start[row][col] = '#';
                goal[row][col] = '#';
            }
            start[9][col] = '*';
            goal[ceiling][col] = '*';
        }
        start[9][0] = 'E';
        goal[9][9] = '>';
        return new String[] {map(start), map(goal)};
    }

    /** One winding corridor without crossings or dead ends, beeper at its end. */
    private static String[] labyrinth() {
        List<int[]> path = new ArrayList<>();
        while (path.size() < 30) {
            path.clear();
            boolean[][] used = new boolean[10][10];
            int[] first = {RANDOM.nextInt(10), RANDOM.nextInt(10)};
            path.add(first);
            used[first[0]][first[1]] = true;
            grow(path, used, 30, new int[] {20000});
        }
        char[][] start = grid(10, 10, '#');
        char[][] goal = grid(10, 10, '#');
        for (int[] cell : path) {
            start[cell[1]][cell[0]] = '.';
            goal[cell[1]][cell[0]] = '.';
        }
        int[] first = path.get(0);
        int[] last = path.get(path.size() - 1);
        start[last[1]][last[0]] = '*';
        start[first[1]][first[0]] = ">^<v".charAt(direction(first, path.get(1)));
        goal[last[1]][last[0]] = ">^<v".charAt(direction(path.get(path.size() - 2), last));
        return new String[] {map(start), map(goal)};
    }

    /** Depth-first search for a path whose cells touch only their predecessor and successor. */
    private static boolean grow(List<int[]> path, boolean[][] used, int length, int[] budget) {
        if (path.size() >= length) {
            return true;
        }
        if (--budget[0] < 0) {
            return false;
        }
        int[] cell = path.get(path.size() - 1);
        List<Integer> dirs = new ArrayList<>(List.of(0, 1, 2, 3));
        Collections.shuffle(dirs, RANDOM);
        for (int d : dirs) {
            int nx = cell[0] + DX[d];
            int ny = cell[1] + DY[d];
            if (nx < 0 || ny < 0 || nx >= 10 || ny >= 10 || used[nx][ny] || touchesPath(nx, ny, cell, used)) {
                continue;
            }
            used[nx][ny] = true;
            path.add(new int[] {nx, ny});
            if (grow(path, used, length, budget)) {
                return true;
            }
            path.remove(path.size() - 1);
            used[nx][ny] = false;
        }
        return false;
    }

    private static boolean touchesPath(int x, int y, int[] from, boolean[][] used) {
        for (int d = 0; d < 4; d++) {
            int nx = x + DX[d];
            int ny = y + DY[d];
            boolean isFrom = nx == from[0] && ny == from[1];
            if (nx >= 0 && ny >= 0 && nx < 10 && ny < 10 && used[nx][ny] && !isFrom) {
                return true;
            }
        }
        return false;
    }

    private static int direction(int[] from, int[] to) {
        for (int d = 0; d < 4; d++) {
            if (from[0] + DX[d] == to[0] && from[1] + DY[d] == to[1]) {
                return d;
            }
        }
        throw new IllegalArgumentException("cells are not neighbours");
    }

    private static char[][] grid(int width, int height, char fill) {
        char[][] cells = new char[height][width];
        for (char[] row : cells) {
            Arrays.fill(row, fill);
        }
        return cells;
    }

    private static String map(char[][] cells) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : cells) {
            sb.append(row).append('\n');
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------ state

    /** One world state: blocks, beepers, Karel. */
    private static final class State {
        final int width;
        final int height;
        final boolean[][] block;
        final boolean[][] beeper;
        int x;
        int y;
        int dir;

        State(String map) {
            String[] rows = map.strip().split("\\R");
            height = rows.length;
            width = rows[0].strip().length();
            block = new boolean[width][height];
            beeper = new boolean[width][height];
            for (int row = 0; row < height; row++) {
                String line = rows[row].strip();
                for (int col = 0; col < width; col++) {
                    char c = line.charAt(col);
                    block[col][row] = c == '#';
                    beeper[col][row] = c == '*' || "ENWS".indexOf(c) >= 0;
                    int d = Math.max(">^<v".indexOf(c), "ENWS".indexOf(c));
                    if (d >= 0) {
                        x = col;
                        y = row;
                        dir = d;
                    }
                }
            }
        }

        boolean isFree(int cx, int cy) {
            return cx >= 0 && cy >= 0 && cx < width && cy < height && !block[cx][cy];
        }

        String toMap() {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    char c = block[col][row] ? '#' : beeper[col][row] ? '*' : '.';
                    if (col == x && row == y) {
                        c = (beeper[col][row] ? "ENWS" : ">^<v").charAt(dir);
                    }
                    sb.append(c);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    private static final Object LOCK = new Object();
    private static final boolean HEADLESS = Boolean.getBoolean("karel.headless");
    private static State state;
    private static State goal;
    private static String worldName;
    private static int steps;
    private static volatile int delay = 300;
    private static volatile Throwable error;
    private static JPanel panel;
    private static JLabel status;
    private static JCheckBox showGoal;
    private static JButton replay;
    private static JSlider stepSlider;
    private static JLabel stepLabel;
    /** True while the program itself moves the step slider (not the user). */
    private static boolean sliderUpdate;
    /** Maps of all states of the run, for Replay. */
    private static final List<String> HISTORY = new ArrayList<>();
    private static String finalMessage;
    private static Color finalColor;

    private Karel() {
    }

    // ------------------------------------------------------------------ world

    /** All worlds; in IntelliJ, {@code World.} followed by Ctrl+Space lists them. */
    public enum World {
        OBTAIN_ARTIFACT,
        DEFUSE_ONE_BOMB,
        CLIMB_THE_STAIRS,
        FILL_THE_HOLES,
        MOW_THE_LAWN,
        DEFUSE_TWO_BOMBS,
        PRACTICE_HOME_RUN,
        SAVE_THE_FLOWER,
        STEAL_OLYMPIC_FIRE,
        HANG_THE_LAMPIONS,
        WALK_THE_LABYRINTH;

        /** Name in camelCase, e.g. {@code obtainArtifact}: key of the map and window title. */
        String camelCase() {
            String[] words = name().toLowerCase().split("_");
            StringBuilder sb = new StringBuilder(words[0]);
            for (int i = 1; i < words.length; i++) {
                sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
            }
            return sb.toString();
        }
    }

    /** Opens a world, e.g. {@code openWorld(World.DEFUSE_ONE_BOMB)}. */
    public static void openWorld(World world) {
        if (state != null) {
            throw new KarelException("openWorld can only be called once per run");
        }
        String[] maps = WORLDS.get(world.camelCase()).get();
        worldName = "World." + world.name();
        state = new State(maps[0]);
        goal = new State(maps[1]);
        HISTORY.add(state.toMap());
        String snapshot = System.getProperty("karel.snapshot");
        if (snapshot != null) {
            if (Boolean.getBoolean("karel.snapshotEnd")) {
                state = goal;
            }
            saveSnapshot(snapshot, Boolean.getBoolean("karel.snapshotGoal"));
            System.exit(0);
        }
        Thread program = Thread.currentThread();
        program.setUncaughtExceptionHandler((thread, e) -> {
            error = e;
            e.printStackTrace();
        });
        if (!HEADLESS) {
            try {
                SwingUtilities.invokeAndWait(Karel::createWindow);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            pause();
        }
        new Thread(() -> finish(program)).start();
    }

    // ------------------------------------------------------------------ commands

    /** Karel moves one cell forward. */
    public static void moveForward() {
        synchronized (LOCK) {
            State s = current();
            int nx = s.x + DX[s.dir];
            int ny = s.y + DY[s.dir];
            if (!s.isFree(nx, ny)) {
                throw new KarelException("Karel cannot move forward: wall ahead");
            }
            s.x = nx;
            s.y = ny;
        }
        step();
    }

    /** Karel turns 90 degrees counterclockwise. */
    public static void turnLeft() {
        turn(1);
    }

    /** Karel turns 90 degrees clockwise. */
    public static void turnRight() {
        turn(3);
    }

    /** Karel turns 180 degrees. */
    public static void turnAround() {
        turn(2);
    }

    /** Karel picks up the beeper on the current cell. */
    public static void pickBeeper() {
        synchronized (LOCK) {
            State s = current();
            if (!s.beeper[s.x][s.y]) {
                throw new KarelException("Karel cannot pick a beeper: there is none here");
            }
            s.beeper[s.x][s.y] = false;
        }
        step();
    }

    /** Karel drops a beeper on the current cell. */
    public static void dropBeeper() {
        synchronized (LOCK) {
            State s = current();
            if (s.beeper[s.x][s.y]) {
                throw new KarelException("Karel cannot drop a beeper: there is already one here");
            }
            s.beeper[s.x][s.y] = true;
        }
        step();
    }

    // ------------------------------------------------------------------ conditions

    /** Is there a beeper on Karel's cell? */
    public static boolean onBeeper() {
        synchronized (LOCK) {
            State s = current();
            return s.beeper[s.x][s.y];
        }
    }

    /** Is there a beeper on the cell in front of Karel? */
    public static boolean beeperAhead() {
        synchronized (LOCK) {
            State s = current();
            int nx = s.x + DX[s.dir];
            int ny = s.y + DY[s.dir];
            return s.isFree(nx, ny) && s.beeper[nx][ny];
        }
    }

    /** Is there no wall in front of Karel? */
    public static boolean frontIsClear() {
        return isClear(0);
    }

    /** Is there no wall to the left of Karel? */
    public static boolean leftIsClear() {
        return isClear(1);
    }

    /** Is there no wall to the right of Karel? */
    public static boolean rightIsClear() {
        return isClear(3);
    }

    // ------------------------------------------------------------------ internals

    private static State current() {
        if (state == null) {
            throw new KarelException("Call openWorld(World....) first");
        }
        return state;
    }

    private static void turn(int quarterTurns) {
        synchronized (LOCK) {
            State s = current();
            s.dir = (s.dir + quarterTurns) % 4;
        }
        step();
    }

    private static boolean isClear(int quarterTurns) {
        synchronized (LOCK) {
            State s = current();
            int d = (s.dir + quarterTurns) % 4;
            return s.isFree(s.x + DX[d], s.y + DY[d]);
        }
    }

    private static void step() {
        int last;
        synchronized (LOCK) {
            HISTORY.add(state.toMap());
            last = HISTORY.size() - 1;
        }
        steps++;
        if (!HEADLESS) {
            showStep(last, last);
            updateStatus("Step " + steps, Color.BLACK);
            pause();
        }
    }

    private static void pause() {
        panel.repaint();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Waits for the end of the program, then compares the world with the goal. */
    private static void finish(Thread program) {
        try {
            program.join();
        } catch (InterruptedException e) {
            return;
        }
        String message;
        Color color;
        synchronized (LOCK) {
            if (error != null) {
                message = "Error after " + steps + " steps: " + error.getMessage();
                color = Color.RED.darker();
            } else if (state.toMap().equals(goal.toMap())) {
                message = "Goal reached in " + steps + " steps";
                color = new Color(0x2E7D32);
            } else {
                message = "Goal not reached: " + difference();
                color = Color.RED.darker();
            }
        }
        System.out.println(worldName + ": " + message);
        if (HEADLESS) {
            System.out.print(state.toMap());
        } else {
            finalMessage = message;
            finalColor = color;
            updateStatus(message, color);
            SwingUtilities.invokeLater(() -> {
                replay.setEnabled(true);
                stepSlider.setEnabled(true);
            });
        }
    }

    /** Moves the step slider to {@code index} of {@code last} steps; callable from any thread. */
    private static void showStep(int index, int last) {
        SwingUtilities.invokeLater(() -> {
            sliderUpdate = true;
            stepSlider.setMaximum(last);
            stepSlider.setValue(index);
            sliderUpdate = false;
            stepLabel.setText(index + " / " + last);
        });
    }

    /** Shows the recorded state after step {@code index}; called when the user drags the step slider. */
    private static void selectStep(int index) {
        synchronized (LOCK) {
            state = new State(HISTORY.get(index));
        }
        stepLabel.setText(index + " / " + (HISTORY.size() - 1));
        panel.repaint();
    }

    /** Plays the recorded run again at the current speed; does not run the program again. */
    private static void replay() {
        int last = HISTORY.size() - 1;
        for (int i = 0; i <= last; i++) {
            synchronized (LOCK) {
                state = new State(HISTORY.get(i));
            }
            showStep(i, last);
            updateStatus("Replay: step " + i + " of " + last, Color.BLACK);
            pause();
        }
        updateStatus(finalMessage, finalColor);
        SwingUtilities.invokeLater(() -> {
            replay.setEnabled(true);
            stepSlider.setEnabled(true);
        });
    }

    private static String difference() {
        int wrongBeepers = 0;
        for (int col = 0; col < state.width; col++) {
            for (int row = 0; row < state.height; row++) {
                if (state.beeper[col][row] != goal.beeper[col][row]) {
                    wrongBeepers++;
                }
            }
        }
        List<String> parts = new ArrayList<>();
        if (wrongBeepers > 0) {
            parts.add(wrongBeepers + (wrongBeepers == 1 ? " cell has" : " cells have") + " the wrong beepers");
        }
        if (state.x != goal.x || state.y != goal.y) {
            parts.add("Karel is not at the goal position");
        } else if (state.dir != goal.dir) {
            parts.add("Karel should face " + DIRECTIONS[goal.dir]);
        }
        return String.join(", ", parts) + " (tick \"Show goal\")";
    }

    // ------------------------------------------------------------------ window

    private static final int CELL = 48;

    private static void createWindow() {
        try {
            // native look of the operating system (Windows, macOS) instead of Java's own "Metal" look
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // keep the default look
        }
        JFrame frame = new JFrame("Karel " + VERSION + " (" + RELEASE_DATE + ") – " + worldName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                synchronized (LOCK) {
                    // scale the world to the window size, centred (e.g. maximised on a projector)
                    double worldW = state.width * CELL + 2;
                    double worldH = state.height * CELL + 2;
                    double scale = Math.min(getWidth() / worldW, getHeight() / worldH);
                    g2.translate((getWidth() - worldW * scale) / 2 + 1, (getHeight() - worldH * scale) / 2 + 1);
                    g2.scale(scale, scale);
                    paintWorld(g2, showGoal.isSelected());
                }
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(state.width * CELL + 1, state.height * CELL + 1));

        JSlider speed = new JSlider(0, 1000, 1000 - delay);
        speed.addChangeListener(e -> delay = 1000 - speed.getValue());
        showGoal = new JCheckBox("Show goal");
        showGoal.addActionListener(e -> panel.repaint());
        status = new JLabel(" ");
        status.setFont(status.getFont().deriveFont(Font.BOLD));

        JPanel controls = new JPanel(new BorderLayout(8, 4));
        controls.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        // step slider: follows the run; after the end the user can drag it to any step
        stepSlider = new JSlider(0, 0, 0);
        stepSlider.setEnabled(false);
        stepSlider.addChangeListener(e -> {
            if (!sliderUpdate && stepSlider.isEnabled()) {
                selectStep(stepSlider.getValue());
            }
        });
        stepLabel = new JLabel("0 / 0");
        JLabel stepCaption = new JLabel("Step");
        JLabel speedCaption = new JLabel("Speed");
        Dimension caption = speedCaption.getPreferredSize();
        stepCaption.setPreferredSize(caption);
        stepLabel.setPreferredSize(new Dimension(
                stepLabel.getFontMetrics(stepLabel.getFont()).stringWidth("0000 / 0000"), caption.height));
        JPanel stepRow = new JPanel(new BorderLayout(8, 0));
        stepRow.add(stepCaption, BorderLayout.WEST);
        stepRow.add(stepSlider, BorderLayout.CENTER);
        stepRow.add(stepLabel, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.add(speedCaption, BorderLayout.WEST);
        top.add(speed, BorderLayout.CENTER);
        top.add(showGoal, BorderLayout.EAST);
        replay = new JButton("Replay");
        replay.setEnabled(false);
        replay.addActionListener(e -> {
            replay.setEnabled(false);
            stepSlider.setEnabled(false);
            new Thread(Karel::replay).start();
        });
        JButton close = new JButton("Close");
        close.addActionListener(e -> System.exit(0));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.add(replay);
        buttons.add(Box.createHorizontalStrut(8));
        buttons.add(close);
        JPanel sliders = new JPanel(new GridLayout(0, 1, 0, 2));
        sliders.add(stepRow);
        sliders.add(top);
        controls.add(sliders, BorderLayout.NORTH);
        controls.add(status, BorderLayout.CENTER);
        controls.add(buttons, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private static void updateStatus(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            status.setText(text);
            status.setForeground(color);
        });
    }

    private static void paintWorld(Graphics2D g, boolean withGoal) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        State s = state;
        for (int col = 0; col < s.width; col++) {
            for (int row = 0; row < s.height; row++) {
                int px = col * CELL;
                int py = row * CELL;
                if (s.block[col][row]) {
                    g.setColor(new Color(0x5F6B7A));
                    g.fillRect(px, py, CELL, CELL);
                } else {
                    g.setColor(new Color(0xDDDDDD));
                    g.drawRect(px, py, CELL, CELL);
                }
                if (s.beeper[col][row]) {
                    g.setColor(new Color(0x2E7D32));
                    g.fillPolygon(diamond(px, py, 0.32));
                }
            }
        }
        g.setColor(new Color(0x1565C0));
        Polygon karel = robot(s.x, s.y, s.dir, 0.38);
        g.fillPolygon(karel);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(karel);
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(3));
        g.drawRect(0, 0, s.width * CELL, s.height * CELL);

        if (withGoal) {
            g.setColor(new Color(0xE65100));
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] {4, 4}, 0));
            for (int col = 0; col < s.width; col++) {
                for (int row = 0; row < s.height; row++) {
                    if (goal.beeper[col][row]) {
                        g.drawPolygon(diamond(col * CELL, row * CELL, 0.40));
                    }
                }
            }
            g.drawPolygon(robot(goal.x, goal.y, goal.dir, 0.44));
        }
    }

    private static void saveSnapshot(String file, boolean withGoal) {
        int scale = 2;
        BufferedImage image = new BufferedImage((state.width * CELL + 2) * scale, (state.height * CELL + 2) * scale,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.scale(scale, scale);
        g.translate(1, 1);
        paintWorld(g, withGoal);
        g.dispose();
        try {
            ImageIO.write(image, "png", new File(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Polygon diamond(int px, int py, double size) {
        int c = CELL / 2;
        int r = (int) (CELL * size);
        return new Polygon(new int[] {px + c, px + c + r, px + c, px + c - r},
                new int[] {py + c - r, py + c, py + c + r, py + c}, 4);
    }

    /** Triangle pointing in Karel's direction. */
    private static Polygon robot(int col, int row, int dir, double size) {
        double cx = col * CELL + CELL / 2.0;
        double cy = row * CELL + CELL / 2.0;
        double r = CELL * size;
        double angle = -dir * Math.PI / 2;
        Polygon p = new Polygon();
        for (double a : new double[] {0, 2.4, -2.4}) {
            p.addPoint((int) Math.round(cx + r * Math.cos(angle + a)), (int) Math.round(cy + r * Math.sin(angle + a)));
        }
        return p;
    }
}

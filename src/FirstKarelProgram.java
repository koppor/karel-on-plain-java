import static karel.Karel.*;

// Karel program: run it with the green arrow next to void main()
// New task: copy this file (Ctrl+C, Ctrl+V on src), give it the name of the task, change the world
// Commands:   moveForward(); turnLeft(); turnRight(); turnAround(); pickBeeper(); dropBeeper();
// Conditions: onBeeper() beeperAhead() frontIsClear() leftIsClear() rightIsClear()
// Worlds: type World. and press Ctrl+Space
void main() {
    openWorld(World.DEFUSE_ONE_BOMB);
    moveForward();
    moveForward();
    turnLeft();
}

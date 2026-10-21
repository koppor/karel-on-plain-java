import static karel.Karel.*;

// Karel exercise 01: first programs with Karel the Robot (Karel 1.0.0)
// Commands:   moveForward(); turnLeft(); turnRight(); turnAround(); pickBeeper(); dropBeeper();
// Conditions: onBeeper() beeperAhead() frontIsClear() leftIsClear() rightIsClear()
// Worlds: type World. and press Ctrl+Space
void main() {
    openWorld(World.OBTAIN_ARTIFACT);
    obtainArtifact();
}

void obtainArtifact() {
    // commands that let Karel rescue the artifact and bring it back
}

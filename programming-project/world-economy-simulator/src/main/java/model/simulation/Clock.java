package model.simulation;

/**
 * Clock represents a singleton clock that keeps track of the simulation time.
 * It manages the current time and the paused state of the simulation.
 */
public class Clock {
    private static Clock instance;
    private int time;
    private boolean paused;

    /**
     * Constructs a new Clock.
     * This constructor is private to enforce the singleton pattern.
     */
    private Clock() {
        this.time = 0;
        this.paused = false;
    }

    /**
     * Gets the singleton instance of the Clock.
     * @return the singleton instance
     */
    public static Clock getInstance() {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    /**
     * Gets the current time of the simulation.
     * @return the current time
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets the current time of the simulation.
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Checks if the clock is paused.
     * @return true if the clock is paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Pauses the clock.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Resumes the clock.
     */
    public void resume() {
        paused = false;
    }
}
package puppy.code.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WeatherTest {
    @Test
    void rainIsCalmUntilTheStormScoreAndStormyFromThenOn() {
        assertEquals(Weather.CALM, Weather.forScore(0));
        assertEquals(Weather.CALM, Weather.forScore(299));
        assertEquals(Weather.STORM, Weather.forScore(300));
        assertEquals(Weather.STORM, Weather.forScore(Integer.MAX_VALUE));
    }

    @Test
    void phasesAreDeclaredInIncreasingOrderOfStartingScore() {
        Weather[] phases = Weather.values();
        assertEquals(0, phases[0].getStartingScore());
        for (int index = 1; index < phases.length; index++) {
            assertTrue(phases[index].getStartingScore() > phases[index - 1].getStartingScore());
        }
    }
}

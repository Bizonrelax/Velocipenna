package ru.bizonrelax.detector.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

/** Tests for per-mode background memory and the Velo config folder. */
public class AppConfigTest {

    @Test
    public void defaultBackgroundIsTurquoise() {
        AppConfig c = AppConfig.fromProperties(new Properties());
        assertEquals(AppConfig.DEFAULT_BG_R, c.getBgR());
        assertEquals(AppConfig.DEFAULT_BG_G, c.getBgG());
        assertEquals(AppConfig.DEFAULT_BG_B, c.getBgB());
    }

    @Test
    public void switchingModesRestoresEachSectionsColor() {
        AppConfig c = AppConfig.fromProperties(new Properties());
        assertTrue(c.isBgSpinnerMode());
        c.setBackgroundRgb(255, 0, 0);
        c.switchBgMode(false);
        assertFalse(c.isBgSpinnerMode());
        assertEquals(AppConfig.DEFAULT_BG_R, c.getBgR());
        c.setPaletteColor(0, 0, 139);
        assertEquals(0, c.getBgR());
        assertEquals(139, c.getBgB());
        c.switchBgMode(true);
        assertEquals(255, c.getBgR());
        assertEquals(0, c.getBgG());
        c.switchBgMode(false);
        assertEquals(0, c.getBgR());
        assertEquals(139, c.getBgB());
    }

    @Test
    public void roundTripPersistsPerModeColors() {
        AppConfig c = AppConfig.fromProperties(new Properties());
        c.setBackgroundRgb(10, 20, 30);
        c.switchBgMode(false);
        c.setPaletteColor(200, 100, 50);
        Properties p = c.toProperties();
        AppConfig r = AppConfig.fromProperties(p);
        assertFalse(r.isBgSpinnerMode());
        assertEquals(200, r.getBgR());
        r.switchBgMode(true);
        assertEquals(10, r.getBgR());
        assertEquals(30, r.getBgB());
    }

    @Test
    public void legacySharedColorStillLoads() {
        Properties p = new Properties();
        p.setProperty("bg.r", "7");
        p.setProperty("bg.g", "77");
        p.setProperty("bg.b", "777-bad");
        AppConfig c = AppConfig.fromProperties(p);
        assertEquals(7, c.getBgR());
        assertEquals(77, c.getBgG());
        assertEquals(AppConfig.DEFAULT_BG_B, c.getBgB());
    }

    @Test
    public void configFileLivesInVeloSubfolder() {
        File f = ConfigStore.defaultConfigFile();
        assertTrue(f.getAbsolutePath().replace('\\', '/')
                .endsWith("/Velo/" + ConfigStore.DEFAULT_FILE_NAME));
    }
}

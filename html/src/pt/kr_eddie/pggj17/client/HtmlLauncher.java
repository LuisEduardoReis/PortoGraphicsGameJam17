package pt.kr_eddie.pggj17.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import pt.kr_eddie.pggj17.Main;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Main.V_WIDTH * Main.SCALE, Main.V_HEIGHT * Main.SCALE);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Main();
        }
}
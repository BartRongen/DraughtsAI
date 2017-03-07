package nl.tue.s2id90.group41;

import nl.tue.s2id90.group41.samples.UninformedPlayer;
import nl.tue.s2id90.group41.samples.OptimisticPlayer;
import nl.tue.s2id90.group41.samples.BuggyPlayer;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;
import nl.tue.s2id90.group41.samples.DamPunOld;
import nl.tue.s2id90.group41.samples.SkyNet23;



/**
 *
 * @author huub
 */
@PluginImplementation
public class MyDraughtsPlugin extends DraughtsPlayerProvider implements DraughtsPlugin {
    public MyDraughtsPlugin() {
        // make one or more players available to the AICompetition tool
        // During the final competition you should make only your 
        // best player available. For testing it might be handy
        // to make more than one player available.
        super(  new DamPun(15),
                new UninformedPlayer(),
                new OptimisticPlayer(),
                new BuggyPlayer(),
                new SkyNet23(15),
                new DamPunOld(15)
        );
    }
}

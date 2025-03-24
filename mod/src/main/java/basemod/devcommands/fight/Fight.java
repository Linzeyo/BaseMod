package basemod.devcommands.fight;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

import java.util.ArrayList;
import java.util.Arrays;

public class Fight extends ConsoleCommand {

    public Fight() {
        minExtraTokens = 1;
        maxExtraTokens = 1;
        requiresPlayer = true;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        MapRoomNode cur = AbstractDungeon.currMapNode;
        if (cur == null) {
            DevConsole.log("cannot fight when there is no map");
            return;
        }

        String[] encounterArray = Arrays.copyOfRange(tokens, 1, tokens.length);
        String encounterName = String.join(" ", encounterArray);
        // If the ID was written using underscores, find the original ID
        if (BaseMod.underScoreEncounterIDs.containsKey(encounterName)) {
            encounterName = BaseMod.underScoreEncounterIDs.get(encounterName);
        }
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
            // Note: AbstractDungeon.nextRoomTransition() will remove the encounter of the current room from the monster list
            // so if we want the new encounter to be in the front afterwards for our new MonsterRoom, we should insert the encounter at position 1, not 0
            AbstractDungeon.monsterList.add(1, encounterName);
        } else {
            AbstractDungeon.monsterList.add(0, encounterName);
        }

        MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new MonsterRoom();

        ArrayList<MapEdge> curEdges = cur.getEdges();
        for (MapEdge edge : curEdges) {
            node.addEdge(edge);
        }

        AbstractDungeon.nextRoom = node;
        AbstractDungeon.nextRoomTransitionStart();
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {

        ArrayList<String> result = new ArrayList<>();

        for (String id : BaseMod.encounterList) {
            result.add(id.replace(' ', '_'));
        }

        return result;
    }
}

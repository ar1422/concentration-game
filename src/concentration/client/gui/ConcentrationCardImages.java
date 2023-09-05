package concentration.client.gui;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Pokemon Images for Concentration board game. Randomly assigns some pokemon image to all the possible values of cards.
 *
 * @author Arya Girisha Rao, Pradeep Kumar Gontla.
 */

public class ConcentrationCardImages {

    /**
     * Map to store a Pokemon image to a corresponding Character. 'A': Abra is one such example.
     */
    Map<Character, Image> imageMap;

    /**
     * Default case of Pokeball image.
     */
    Image defaultPokeBall = new Image(Objects.requireNonNull(ConcentrationCardImages.class.getResourceAsStream("images/pokeball.png")));

    public final List<String> availablePokeMon = new ArrayList<>(Arrays.asList("abra", "bulbasaur", "pidgey", "snorlak", "pikachu", "charizard", "diglett",
            "golem", "golbat", "jigglypuff", "magikarp", "poliwag", "psyduck", "rattata", "slowpoke", "squirtle", "meowth", "mewtwo", "natu"));

    /**
     * Creates a Pokemon images class with map of character to images of pokemons.
     */
    public ConcentrationCardImages() {

        String fileNameFormat = "images/%s.png";
        List<Image> imageList = new ArrayList<>();
        Function<String, Image> createImageObjects = s -> new Image(Objects.requireNonNull(ConcentrationCardImages.class.getResourceAsStream(s)));
        availablePokeMon.forEach((String p) -> imageList.add(createImageObjects.apply(String.format(fileNameFormat, p))));
        Collections.shuffle(imageList);
        imageMap = new HashMap<>();
        IntConsumer insertImage = (integer) -> imageMap.put((char)(integer +'A'), imageList.get(integer));
        IntStream.range(0, 18).forEach(insertImage);
    }

}

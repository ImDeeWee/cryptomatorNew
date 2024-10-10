package org.cryptomator.ui.recoverykey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WordEncoderTest {

	private static final Random PRNG = new Random(42l);
	private WordEncoder encoder;

	@BeforeAll
	public void setup() {
		encoder = new WordEncoder();
	}

	@DisplayName("decode(encode(input)) == input")
	@ParameterizedTest(name = "test {index}")
	@MethodSource("createRandomByteSequences")
	public void encodeAndDecode(byte[] input) {
		String encoded = encoder.encodePadded(input);
		byte[] decoded = encoder.decode(encoded);
		Assertions.assertArrayEquals(input, decoded);
	}

	public static Stream<byte[]> createRandomByteSequences() {
		return IntStream.range(0, 30).mapToObj(i -> {
			byte[] randomBytes = new byte[i * 3];
			PRNG.nextBytes(randomBytes);
			return randomBytes;
		});
	}

	/**
    * Teste le constructeur de WordEncoder lorsqu'un fichier de mots contenant un nombre insuffisant de mots est fourni.
    * L'objectif est de vérifier que le constructeur lève une IllegalArgumentException lorsque le fichier de mots contient
    * moins de mots que nécessaire (4096 mots), et que le message d'erreur reflète correctement cette situation.
    */
    @DisplayName("Le constructeur avec un fichier de mots insuffisants doit lancer une IllegalArgumentException")
    @Test
    public void constructorWithInsufficientWords() {
        String insufficientWordsFile = "/words/short_words.txt";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new WordEncoder(insufficientWordsFile);
        });
        Assertions.assertTrue(exception.getMessage().contains("Insufficient input file: " + insufficientWordsFile));
    }

	/**
	* Ce test a pour objectif de vérifier que la méthode getWords() de la classe WordEncoder
	* renvoie une liste valide. Plus précisément, nous nous assurons que la liste n'est ni nulle
	* ni vide, ce qui garantit que les mots ont bien été chargés depuis le fichier.
	*/
	@Test
	void testGetWords() {

        WordEncoder wordEncoder = new WordEncoder();

        List<String> words = wordEncoder.getWords();

        assertNotNull(words, "La liste de mots ne doit pas être nulle.");
        
        assertFalse(words.isEmpty(), "La liste de mots ne doit pas être vide.");
        
    }

}
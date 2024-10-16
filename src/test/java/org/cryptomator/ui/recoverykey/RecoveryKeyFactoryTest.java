package org.cryptomator.ui.recoverykey;

import com.google.common.base.Splitter;
import org.cryptomator.cryptolib.api.CryptoException;
import org.cryptomator.cryptolib.api.Masterkey;
import org.cryptomator.cryptolib.common.MasterkeyFileAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Arrays; 
import java.nio.file.Files; 
import java.util.Optional;   
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecoveryKeyFactoryTest {

	private final WordEncoder wordEncoder = new WordEncoder();
	private final MasterkeyFileAccess masterkeyFileAccess = Mockito.mock(MasterkeyFileAccess.class);
	private final RecoveryKeyFactory inTest = new RecoveryKeyFactory(wordEncoder, masterkeyFileAccess);

	@Test
	@DisplayName("createRecoveryKey() creates 44 words")
	public void testCreateRecoveryKey() throws IOException, CryptoException {
		Path pathToVault = Path.of("path/to/vault");
		Masterkey masterkey = Mockito.mock(Masterkey.class);
		Mockito.when(masterkeyFileAccess.load(pathToVault.resolve("masterkey.cryptomator"), "asd")).thenReturn(masterkey);

		Mockito.when(masterkey.getEncoded()).thenReturn(new byte[64]);

		String recoveryKey = inTest.createRecoveryKey(pathToVault, "asd");
		Assertions.assertNotNull(recoveryKey);
		Assertions.assertEquals(44, Splitter.on(' ').splitToList(recoveryKey).size()); // 66 bytes encoded as 44 words
	}

	@Test
	@DisplayName("validateRecoveryKey() with odd number of words")
	public void testValidateValidateRecoveryKeyWithOddNumberOfWords() {
		boolean result = inTest.validateRecoveryKey("pathway");
		Assertions.assertFalse(result);
	}

	@Test
	@DisplayName("validateRecoveryKey() with words not in dictionary")
	public void testValidateValidateRecoveryKeyWithGarbageInput() {
		boolean result = inTest.validateRecoveryKey("Backpfeifengesicht Schweinehund"); // according to le internet these are typical German words
		Assertions.assertFalse(result);
	}

	@Test
	@DisplayName("validateRecoveryKey() with too short input")
	public void testValidateValidateRecoveryKeyWithTooShortInput() {
		boolean result = inTest.validateRecoveryKey("pathway lift");
		Assertions.assertFalse(result);
	}

	@Test
	@DisplayName("validateRecoveryKey() with invalid checksum")
	public void testValidateValidateRecoveryKeyWithInvalidCrc() {
		boolean result = inTest.validateRecoveryKey("""
				pathway lift abuse plenty export texture gentleman landscape beyond ceiling around leaf cafe charity \
				border breakdown victory surely computer cat linger restrict infer crowd live computer true written amazed \
				investor boot depth left theory snow whereby terminal weekly reject happiness circuit partial cup wrong \
				""");
		Assertions.assertFalse(result);
	}

	@Test
	@DisplayName("validateRecoveryKey() with valid input")
	public void testValidateValidateRecoveryKeyWithValidKey() {
		boolean result = inTest.validateRecoveryKey("""
				pathway lift abuse plenty export texture gentleman landscape beyond ceiling around leaf cafe charity \
				border breakdown victory surely computer cat linger restrict infer crowd live computer true written amazed \
				investor boot depth left theory snow whereby terminal weekly reject happiness circuit partial cup ad \
				""");
		Assertions.assertTrue(result);
	}

	@ParameterizedTest(name = "passing validation = {0}")
	@DisplayName("validateRecoveryKey() with extended validation")
	@ValueSource(booleans = {true, false})
	public void testValidateValidateRecoveryKeyWithValidKey(boolean extendedValidationResult) {
		Predicate<byte[]> validator = Mockito.mock(Predicate.class);
		Mockito.doReturn(extendedValidationResult).when(validator).test(Mockito.any());
		boolean result = inTest.validateRecoveryKey("""
				pathway lift abuse plenty export texture gentleman landscape beyond ceiling around leaf cafe charity \
				border breakdown victory surely computer cat linger restrict infer crowd live computer true written amazed \
				investor boot depth left theory snow whereby terminal weekly reject happiness circuit partial cup ad \
				""", validator);
		Mockito.verify(validator).test(Mockito.any());
		Assertions.assertEquals(extendedValidationResult, result);
	}

	/**
    * Lève une IllegalArgumentException lorsque la fonction newMasterkeyFileWithPassphrase() reçoit une clé de récupération invalide.
    * L'objectif est de vérifier que la méthode valide correctement l'entrée et lève une exception en cas de clé de récupération incorrecte.
    */
	@Test
    @DisplayName("newMasterkeyFileWithPassphrase() lève une IllegalArgumentException pour une recovery key invalide")
    public void testNewMasterkeyFileWithPassphrase_InvalidRecoveryKey() {

        Path vaultPath = Path.of("path/to/vault");
        CharSequence newPassword = "newPassword";
        String invalidRecoveryKey = "invalid recovery key";


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inTest.newMasterkeyFileWithPassphrase(vaultPath, invalidRecoveryKey, newPassword);
        });
    }


	/**
    * Lève une IOException lorsque la fonction newMasterkeyFileWithPassphrase() échoue à persister la clé masterkey
    * en raison d'une exception dans la méthode persist() de masterkeyFileAccess.
    * L'objectif est de s'assurer que l'IOException est correctement propagée et non capturée silencieusement par la méthode.
    */
	@Test
    @DisplayName("newMasterkeyFileWithPassphrase() lève une IOException quand masterkeyFileAccess.persist() échoue")
    public void testNewMasterkeyFileWithPassphrase_PersistFails(@TempDir Path tempDir) throws IOException {
     
        Path vaultPath = tempDir;
        CharSequence newPassword = "newPassword";

        
        byte[] rawKey = new byte[64];
        Arrays.fill(rawKey, (byte) 0x01);
        String validRecoveryKey = inTest.createRecoveryKey(rawKey);

        
        Mockito.doThrow(new IOException("Simulated IO Exception")).when(masterkeyFileAccess)
                .persist(Mockito.any(Masterkey.class), Mockito.any(Path.class), Mockito.any(CharSequence.class));

       
        Assertions.assertThrows(IOException.class, () -> {
            inTest.newMasterkeyFileWithPassphrase(vaultPath, validRecoveryKey, newPassword);
        });
    }

}
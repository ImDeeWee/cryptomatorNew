package org.cryptomator.common.keychain;


import org.cryptomator.integrations.keychain.KeychainAccessException;
import org.cryptomator.integrations.keychain.KeychainAccessProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class KeychainManagerTest {

	@Test
	public void testStoreAndLoad() throws KeychainAccessException {
		KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
		keychainManager.storePassphrase("test", "Test", "asd");
		Assertions.assertArrayEquals("asd".toCharArray(), keychainManager.loadPassphrase("test"));
	}

	/**
	 * Cette classe verifie si la fonction {@link KeychainManager#changePassphrase(String, String, CharSequence)} fonctionne correctement.
	 * @throws KeychainAccessException
	 */
	@Test
	public void testStoreAndLoadAfterChangePassphrase() throws KeychainAccessException{
		KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
		keychainManager.storePassphrase("test", "Test", "asd");

		keychainManager.changePassphrase("test", "LBJ23", "ItsWorking");
		Assertions.assertArrayEquals("ItsWorking".toCharArray(), keychainManager.loadPassphrase("test"));



	}

	/**
	 * 	Cette fonction suppose que la cle n'existe pas et simule le test.
	 */
	@Test
	public void catchKeychainAccessException() {
		boolean exceptionCatched = false;


		KeychainAccessProvider mockKeychain = Mockito.mock(KeychainAccessProvider.class);


		try {
			Mockito.doThrow(new KeychainAccessException("Test exception"))
					.when(mockKeychain).loadPassphrase(Mockito.eq("I'm not a key"));


			KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(mockKeychain));

			// Appeler la méthode deletePassphrase avec la clé "I'm not a key"
			keychainManager.loadPassphrase("I'm not a key");

		} catch (KeychainAccessException exception) {
			exceptionCatched = true;
		}

		// Vérifier que l'exception a bien été capturée
		Assertions.assertTrue(exceptionCatched);
	}




	@Nested
	public static class WhenObservingProperties {

		@BeforeAll
		public static void startup() throws InterruptedException {
			CountDownLatch latch = new CountDownLatch(1);
			Platform.startup(latch::countDown);
			var javafxStarted = latch.await(5, TimeUnit.SECONDS);
			Assumptions.assumeTrue(javafxStarted);
		}

		@Test
		public void testPropertyChangesWhenStoringPassword() throws KeychainAccessException, InterruptedException {
			KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
			ReadOnlyBooleanProperty property = keychainManager.getPassphraseStoredProperty("test");
			Assertions.assertFalse(property.get());

			keychainManager.storePassphrase("test", null,"bar");

			AtomicBoolean result = new AtomicBoolean(false);
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				result.set(property.get());
				latch.countDown();
			});
			Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> latch.await());
			Assertions.assertTrue(result.get());
		}

	}

}
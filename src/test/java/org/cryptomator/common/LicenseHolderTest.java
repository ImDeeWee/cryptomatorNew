package org.cryptomator.common;

import static org.mockito.Mockito.*;



import java.util.Optional;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.cryptomator.common.settings.Settings;
import org.junit.jupiter.api.*;

/**
 * Pour la creation de cette classe de test, on d du change la protection de la classe {@link LicenseChecker} qui n'avait de protection a publique pour la creation de ces tests.
 * Le changement ne devrait absolument pas modifier le comportement du programme s'il est bien programme.
 */
public class LicenseHolderTest {

    private LicenseChecker licenseChecker;
    private Settings settings;
    private LicenseHolder licenseHolder;

    /**
     * Creation d'un Mockito de licenseChecker et de settings pour les simuler lors des tests.
     */
    @BeforeEach
    public void setUp() {

        licenseChecker = mock(LicenseChecker.class);
        settings = mock(Settings.class);
        settings.licenseKey = mock(javafx.beans.property.StringProperty.class);


        licenseHolder = new LicenseHolder(licenseChecker, settings);
    }

    /**
     * Cette fonction teste si la fonction {@link LicenseHolder#isValidLicense()} retourne vrai lorsque la clé de license est valide.
     */
    @Test
    public void testWithValidLicense() {
        // Simulation d'une licence valide
        DecodedJWT jwt = mock(DecodedJWT.class);
        when(licenseChecker.check(anyString())).thenReturn(Optional.of(jwt));


        when(settings.licenseKey.get()).thenReturn("valid_license_key");


        licenseHolder = new LicenseHolder(licenseChecker, settings);


        Assertions.assertTrue(licenseHolder.isValidLicense());
    }
    /**
     * Cette fonction teste si la fonction {@link LicenseHolder#isValidLicense()} retourne faux lorsque la clé de license n'est pas valide.
     */
    @Test
    public void testWithInvalidLicense() {

        when(licenseChecker.check(anyString())).thenReturn(Optional.empty());


        licenseHolder = new LicenseHolder(licenseChecker, settings);


        Assertions.assertFalse(licenseHolder.isValidLicense());
    }


}

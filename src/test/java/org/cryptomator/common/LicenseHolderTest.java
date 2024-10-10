package org.cryptomator.common;

import static org.mockito.Mockito.*;



import java.util.Optional;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.cryptomator.common.settings.Settings;
import org.junit.jupiter.api.*;


public class LicenseHolderTest {

    private LicenseChecker licenseChecker;
    private Settings settings;
    private LicenseHolder licenseHolder;

    @BeforeEach
    public void setUp() {

        licenseChecker = mock(LicenseChecker.class);
        settings = mock(Settings.class);
        settings.licenseKey = mock(javafx.beans.property.StringProperty.class);


        licenseHolder = new LicenseHolder(licenseChecker, settings);
    }

    @Test
    public void testConstructorWithValidLicense() {
        // Simulation d'une licence valide
        DecodedJWT jwt = mock(DecodedJWT.class);
        when(licenseChecker.check(anyString())).thenReturn(Optional.of(jwt));

        // Simulation du retour de la clé de licence
        when(settings.licenseKey.get()).thenReturn("valid_license_key");

        // Re-initialisation du LicenseHolder avec les conditions
        licenseHolder = new LicenseHolder(licenseChecker, settings);

        // Vérification que la licence est valide
        Assertions.assertTrue(licenseHolder.isValidLicense());
    }

    @Test
    public void testConstructorWithInvalidLicense() {
        // Simulation d'une licence invalide
        when(licenseChecker.check(anyString())).thenReturn(Optional.empty());

        // Re-initialisation du LicenseHolder
        licenseHolder = new LicenseHolder(licenseChecker, settings);

        // Vérification que la licence n'est pas valide
        Assertions.assertFalse(licenseHolder.isValidLicense());
    }


}

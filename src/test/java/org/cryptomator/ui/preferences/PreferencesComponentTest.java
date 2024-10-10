package org.cryptomator.ui.preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cryptomator.common.LicenseHolder;
import org.cryptomator.ui.preferences.PreferencesComponent;
import org.cryptomator.ui.preferences.SelectedPreferencesTab;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import dagger.Lazy;

import static org.mockito.Mockito.*;

class PreferencesComponentTest {

    private PreferencesComponent preferencesComponent;
    private Stage mockStage;
    private Lazy<Scene> mockScene;
    private ObjectProperty<SelectedPreferencesTab> selectedTabProperty;

    /**
     * Création d'un mock de la scène pour la simuler.
     */
    @BeforeEach
    void setUp() {

        mockStage = mock(Stage.class);
        mockScene = mock(Lazy.class);
        selectedTabProperty = new SimpleObjectProperty<>();


        preferencesComponent = mock(PreferencesComponent.class, Mockito.CALLS_REAL_METHODS);


        when(preferencesComponent.window()).thenReturn(mockStage);
        when(preferencesComponent.scene()).thenReturn(mockScene);
        when(mockScene.get()).thenReturn(mock(Scene.class));
        when(preferencesComponent.selectedTabProperty()).thenReturn(selectedTabProperty);
    }

    /**
     * Cette fonction tests si la fonction {@link PreferencesComponent#showPreferencesWindow(SelectedPreferencesTab)} retourne le correct {@link SelectedPreferencesTab} selectionne
     * si le stage est bel et bien configure. La selection du {@link SelectedPreferencesTab} se fait aleatoirement entre tous les options valides lors de chaque execution du test.
     */
    @Test
    void testShowPreferencesWindow() {

        //Randomized the selectedPreferencesTab value for each test.
        SelectedPreferencesTab[] tab = SelectedPreferencesTab.values();
        int min = 0;
        int max = tab.length-1;
        int randomIndex = (int)(Math.random() * (max - min + 1)) + min;




        SelectedPreferencesTab testTab = tab[randomIndex];


        Stage stage = preferencesComponent.showPreferencesWindow(testTab);


        Assertions.assertEquals(testTab, selectedTabProperty.get());


        verify(mockStage).setScene(mockScene.get());
        verify(mockStage).setMinWidth(420);
        verify(mockStage).setMinHeight(300);
        verify(mockStage).show();
        verify(mockStage).requestFocus();
    }
}
